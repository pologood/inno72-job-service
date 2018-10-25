package com.inno72.job.admin.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.core.util.CronExpression;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inno72.job.admin.common.ExecutorFailStrategyEnum;
import com.inno72.job.admin.common.ExecutorRouteStrategyEnum;
import com.inno72.job.admin.mapper.JobGroupDao;
import com.inno72.job.admin.mapper.JobInfoDao;
import com.inno72.job.admin.mapper.JobLogDao;
import com.inno72.job.admin.model.JobGroup;
import com.inno72.job.admin.model.JobInfo;
import com.inno72.job.admin.model.JobLog;
import com.inno72.job.admin.scheduler.JobDynamicScheduler;
import com.inno72.job.admin.utils.LocalCacheUtil;
import com.inno72.job.core.biz.ExecutorBiz;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.enums.ExecutorBlockStrategyEnum;
import com.inno72.job.core.glue.GlueTypeEnum;
import com.inno72.job.core.util.FileUtil;
import com.inno72.job.core.util.Submittable;


@Service
public class JobService {
	private static Logger logger = LoggerFactory.getLogger(JobService.class);

	@Resource
	private JobGroupDao jobGroupDao;
	@Resource
	private JobInfoDao jobInfoDao;
	@Resource
	public JobLogDao jobLogDao;

	public List<JobGroup> findAllJobGroup() {
		return jobGroupDao.findAll();
	}

	public int saveJobGroup(JobGroup jobGroup) {
		return jobGroupDao.save(jobGroup);
	}

	public int updateJobGroup(JobGroup jobGroup) {
		return jobGroupDao.update(jobGroup);
	}

	public JobInfo findJobInfo(int jobId) {
		return jobInfoDao.loadById(jobId);
	}

	public List<JobInfo> findJobsByGroup(int jobGroup) {
		return jobInfoDao.getJobsByGroup(jobGroup);
	}

	public List<JobLog> jobLogPageList(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart,
			Date triggerTimeEnd, int logStatus) {
		
		return jobLogDao.pageList(offset, pagesize, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);

	}
	
	public JobLog loadJobLogInfo(int id) {
		return jobLogDao.load(id);
	}
	
	public int clearLog(int jobGroup, int jobId, Date clearBeforeTime, int clearBeforeNum){
		return jobLogDao.clearLog(jobGroup, jobId, clearBeforeTime, clearBeforeNum);
	}
	
	public ReturnT<Void> killLog(int logId) {
		JobLog log = this.loadJobLogInfo(logId);
		JobInfo jobInfo = this.findJobInfo(log.getJobId());
		if (jobInfo==null) {
			return new ReturnT<Void>(-1, "任务ID非法");
		}
		if (ReturnT.SUCCESS_CODE != log.getTriggerCode()) {
			return new ReturnT<Void>(-1, "调度失败，无法终止日志");
		}

		// request of kill
		ReturnT<Void> runResult = null;
		try {
			ExecutorBiz executorBiz = JobDynamicScheduler.getExecutorBiz(log.getExecutorAddress());
			runResult = executorBiz.kill(jobInfo.getId());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			runResult = new ReturnT<Void>(-1, e.getMessage());
		}

		if (ReturnT.SUCCESS_CODE == runResult.getCode()) {
			log.setHandleCode(ReturnT.FAIL_CODE);
			log.setHandleMsg("人为操作主动终止:" + (runResult.getMsg()!=null?runResult.getMsg():""));
			log.setHandleTime(new Date());
			jobLogDao.updateHandleInfo(log);
			return new ReturnT<Void>(0, runResult.getMsg());
		} else {
			return new ReturnT<Void>(-1, runResult.getMsg());
		}
	}
	
	public int jobLogPageCount(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart,
			Date triggerTimeEnd, int logStatus) {
		
		return jobLogDao.pageListCount(offset, pagesize, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);

	}


	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ReturnT<Void> removeJobGroup(int id) {
		int count = jobInfoDao.pageListCount(0, 10, id, null, null);
		if (count > 0) {
			return new ReturnT<Void>(-1, "拒绝删除，该执行器使用中");
		}

		List<JobGroup> allList = jobGroupDao.findAll();
		if (allList.size() == 1) {
			return new ReturnT<Void>(-1, "拒绝删除, 系统至少保留一个执行器");
		}

		int ret = jobGroupDao.remove(id);

		return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
	}

	public Map<String, Object> pageList(int start, int length, int jobGroup, String jobDesc, String executorHandler,
			String filterTime) {

		// page list
		List<JobInfo> list = jobInfoDao.pageList(start, length, jobGroup, jobDesc, executorHandler);
		int list_count = jobInfoDao.pageListCount(start, length, jobGroup, jobDesc, executorHandler);

		// fill job info
		if (list != null && list.size() > 0) {
			for (JobInfo jobInfo : list) {
				JobDynamicScheduler.fillJobInfo(jobInfo);
			}
		}

		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("recordsTotal", list_count); // 总记录数
		maps.put("data", list); // 分页列表
		return maps;
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ReturnT<Void> updateJarSource(Integer jobId, String checksum, byte[] jarFile) throws IOException{
	
		if(jobId == null) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "jobId 不能为空");
		}
		
		if(StringUtils.isBlank(checksum)) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "checksum 不能为空");
		}
		
		if(jarFile == null || jarFile.length == 0) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "jarFile 不能为空");
		}
		
		if(!checksum.equalsIgnoreCase(FileUtil.GetMD5Code(jarFile))) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "校验不成功");
		}
		
		JobInfo jobInfo = jobInfoDao.loadById(jobId);
		
		if(jobInfo == null) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "任务ID不存在");
		}
		
		if(!(GlueTypeEnum.JAVA_JAR_EXTERNAL.name().equals( jobInfo.getGlueType())
				|| GlueTypeEnum.JAVA_JAR_INTERNAL.name().equals( jobInfo.getGlueType()))) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "请调用updateScriptSource");
		}
		
		String prefix = jobInfo.getGlueSource();
		final String jarFileName = String.format("./jar_repositories/%s.jar", prefix);
		final String jarLockFileName = String.format("./jar_repositories_lock/%s.lock", prefix);
		
		if(jobInfoDao.update(jobInfo) == 0) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "更新失败");
		}
		
		FileUtil.processByFileLock(new File(jarLockFileName), new Submittable() {
			
			@Override
			public Object submit() throws IOException {
				OutputStream output = null;
				try {
					output = new FileOutputStream(jarFileName);
					output.write(jarFile);
					return null;
				}finally{
					if(output != null) output.close();
				}
			}
		});
		
		return ReturnT.SUCCESS;
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ReturnT<Void> updateScriptSource(Integer jobId, String source){
		
		if(jobId == null) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "jobId 不能为空");
		}
		
		if(StringUtils.isBlank(source)) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "source 不能为空");
		}
		
		JobInfo jobInfo = jobInfoDao.loadById(jobId);
		
		if(jobInfo == null) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "任务ID不存在");
		}
		
		if(GlueTypeEnum.JAVA_JAR_EXTERNAL.name().equals( jobInfo.getGlueType())
				|| GlueTypeEnum.JAVA_JAR_INTERNAL.name().equals( jobInfo.getGlueType())) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "请调用updateJarSource");
		}
		
		jobInfo.setGlueSource(source);
		jobInfoDao.update(jobInfo);
		
		return ReturnT.SUCCESS;
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ReturnT<Void> add(JobInfo jobInfo, byte[] jarFile) throws IOException {

		JobGroup group = jobGroupDao.load(jobInfo.getJobGroup());
		if (group == null) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "请选择执行器");
		}
		if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "Cron格式非法");
		}
		if (StringUtils.isBlank(jobInfo.getJobDesc())) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "请输入任务描述");
		}
		if (StringUtils.isBlank(jobInfo.getAuthor())) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "请输入负责人");
		}
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "路由策略非法");
		}
		if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "阻塞处理策略非法");
		}
		if (ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(), null) == null) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "失败处理策略非法");
		}
		if (GlueTypeEnum.match(jobInfo.getGlueType()) == null) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "运行模式非法");
		}
		
		if(GlueTypeEnum.JAVA_JAR_INTERNAL == GlueTypeEnum.match(jobInfo.getGlueType())
			|| GlueTypeEnum.JAVA_JAR_EXTERNAL == GlueTypeEnum.match(jobInfo.getGlueType())){
			
			if(GlueTypeEnum.JAVA_JAR_INTERNAL == GlueTypeEnum.match(jobInfo.getGlueType())){
				if(StringUtils.isBlank(jobInfo.getExecutorHandler())) {
					return new ReturnT<Void>(ReturnT.FAIL_CODE, "请输入JobHandler");
				}
			}
			
			if(jarFile == null || jarFile.length == 0) {
				return new ReturnT<Void>(ReturnT.FAIL_CODE, "未上传jar文件");
			}
			
			if(StringUtils.isBlank(jobInfo.getChecksum())) {
				return new ReturnT<Void>(ReturnT.FAIL_CODE, "未上传校验码");
			}
			
			if(!jobInfo.getChecksum().equalsIgnoreCase(FileUtil.GetMD5Code(jarFile))) {
				return new ReturnT<Void>(ReturnT.FAIL_CODE, "校验不成功");
			}
			
		}else if(GlueTypeEnum.HTTP ==  GlueTypeEnum.match(jobInfo.getGlueType())) {
			if(StringUtils.isBlank(jobInfo.getExecutorParam())) {
				return new ReturnT<Void>(ReturnT.FAIL_CODE, "请输入ExecutorParam");
			}
		}else if(GlueTypeEnum.JAVA_BEAN ==  GlueTypeEnum.match(jobInfo.getGlueType())){
			if(StringUtils.isBlank(jobInfo.getExecutorHandler())) {
				return new ReturnT<Void>(ReturnT.FAIL_CODE, "请输入JobHandler");
			}
		}else{
			if(jobInfo.getGlueSource() == null || StringUtils.isBlank(jobInfo.getGlueSource() )) {
				return new ReturnT<Void>(ReturnT.FAIL_CODE, "未上传script文件");
			}
		}
		
		// fix "\r" in shell
		if (GlueTypeEnum.GLUE_SHELL == GlueTypeEnum.match(jobInfo.getGlueType()) && jobInfo.getGlueSource() != null) {
			jobInfo.setGlueSource(jobInfo.getGlueSource().replaceAll("\r", ""));
		}
		
		// ChildJobId valid
		if (StringUtils.isNotBlank(jobInfo.getChildJobId())) {
			String[] childJobIds = StringUtils.split(jobInfo.getChildJobId(), ",");
			for (String childJobIdItem : childJobIds) {
				if (StringUtils.isNotBlank(childJobIdItem) && StringUtils.isNumeric(childJobIdItem)) {
					JobInfo childJobInfo = jobInfoDao.loadById(Integer.valueOf(childJobIdItem));
					if (childJobInfo == null) {
						return new ReturnT<Void>(ReturnT.FAIL_CODE,
								MessageFormat.format("子任务ID:{0}不存在", childJobIdItem));
					}
				} else {
					return new ReturnT<Void>(ReturnT.FAIL_CODE, MessageFormat.format("子任务ID:{0}非法", childJobIdItem));
				}
			}
			jobInfo.setChildJobId(StringUtils.join(childJobIds, ","));
		}
		
		if(GlueTypeEnum.JAVA_JAR_INTERNAL == GlueTypeEnum.match(jobInfo.getGlueType())
				|| GlueTypeEnum.JAVA_JAR_EXTERNAL == GlueTypeEnum.match(jobInfo.getGlueType())){
				
			String prefix = FileUtil.getUuid();
			final String jarFileName = String.format("./jar_repositories/%s.jar", prefix);
			final String jarLockFileName = String.format("./jar_repositories_lock/%s.lock", prefix);
			jobInfo.setGlueSource(prefix);
			
			jobInfoDao.save(jobInfo);
			if (jobInfo.getId() < 1) {
				return new ReturnT<Void>(ReturnT.FAIL_CODE, "新增任务失败");
			}
			
			FileUtil.processByFileLock(new File(jarLockFileName), new Submittable() {

				@Override
				public Object submit() throws IOException {
					OutputStream output = null;
					try {
						output = new FileOutputStream(jarFileName);
						output.write(jarFile);
						return null;
					}finally{
						if(output != null) output.close();
					}
				}
			});
				
		}else {
			jobInfoDao.save(jobInfo);
			if (jobInfo.getId() < 1) {
				return new ReturnT<Void>(ReturnT.FAIL_CODE, "新增任务失败");
			}
		}
		
		// add in quartz
		String qz_group = String.valueOf(jobInfo.getJobGroup());
		String qz_name = String.valueOf(jobInfo.getId());
		try {		
			JobDynamicScheduler.addJob(qz_name, qz_group, jobInfo.getJobCron());
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			try {
				jobInfoDao.delete(jobInfo.getId());
				JobDynamicScheduler.removeJob(qz_name, qz_group);
			} catch (SchedulerException e1) {
				logger.error(e.getMessage(), e1);
			}
			
			if(GlueTypeEnum.JAVA_JAR_INTERNAL == GlueTypeEnum.match(jobInfo.getGlueType())
					|| GlueTypeEnum.JAVA_JAR_EXTERNAL == GlueTypeEnum.match(jobInfo.getGlueType())){
				new File(jobInfo.getGlueSource()).delete();
			}
			
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "新增任务失败:" + e.getMessage());
		}
	}

	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ReturnT<Void> update(JobInfo jobInfo) throws SchedulerException, IOException {

		// valid
		if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "Cron格式非法");
		}
		if (StringUtils.isBlank(jobInfo.getJobDesc())) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "请输入任务描述");
		}
		if (StringUtils.isBlank(jobInfo.getAuthor())) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "请输入责任人");
		}
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "路由策略非法");
		}
		if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "阻塞处理策略非法");
		}
		if (ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(), null) == null) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "失败处理策略非法");
		}

		// ChildJobId valid
		if (StringUtils.isNotBlank(jobInfo.getChildJobId())) {
			String[] childJobIds = StringUtils.split(jobInfo.getChildJobId(), ",");
			for (String childJobIdItem : childJobIds) {
				if (StringUtils.isNotBlank(childJobIdItem) && StringUtils.isNumeric(childJobIdItem)) {
					JobInfo childJobInfo = jobInfoDao.loadById(Integer.valueOf(childJobIdItem));
					if (childJobInfo == null) {
						return new ReturnT<Void>(ReturnT.FAIL_CODE,
								MessageFormat.format("子任务ID:{0}不存在", childJobIdItem));
					}
					// avoid cycle relate
					if (childJobInfo.getId() == jobInfo.getId()) {
						return new ReturnT<Void>(ReturnT.FAIL_CODE,
								MessageFormat.format("子任务ID({0})不可与父任务重复", childJobIdItem));
					}
				} else {
					return new ReturnT<Void>(ReturnT.FAIL_CODE, MessageFormat.format("子任务ID{0}非法", childJobIdItem));
				}
			}
			jobInfo.setChildJobId(StringUtils.join(childJobIds, ","));
		}

		// stage job info
		JobInfo exists_jobInfo = jobInfoDao.loadById(jobInfo.getId());
		if (exists_jobInfo == null) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "任务ID不存在");
		}
		
		if(!exists_jobInfo.getGlueType().equals(jobInfo.getGlueType())) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "GlueType不能变更");
		}
		
		exists_jobInfo.setJobCron(jobInfo.getJobCron());
		exists_jobInfo.setJobDesc(jobInfo.getJobDesc());
		exists_jobInfo.setAuthor(jobInfo.getAuthor());
		exists_jobInfo.setAlarmEmail(jobInfo.getAlarmEmail());
		exists_jobInfo.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
		exists_jobInfo.setExecutorHandler(jobInfo.getExecutorHandler());
		exists_jobInfo.setExecutorParam(jobInfo.getExecutorParam());
		exists_jobInfo.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
		exists_jobInfo.setExecutorFailStrategy(jobInfo.getExecutorFailStrategy());
		exists_jobInfo.setChildJobId(jobInfo.getChildJobId());
		jobInfoDao.update(exists_jobInfo);

		// fresh quartz
		String qz_group = String.valueOf(exists_jobInfo.getJobGroup());
		String qz_name = String.valueOf(exists_jobInfo.getId());
		
		if(JobDynamicScheduler.rescheduleJob(qz_group, qz_name, exists_jobInfo.getJobCron())) {
			return ReturnT.SUCCESS;
		}else {
			throw new IOException("调度失败");
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ReturnT<Void> remove(int id) throws IOException {
		JobInfo jobInfo = jobInfoDao.loadById(id);
		String group = String.valueOf(jobInfo.getJobGroup());
		String name = String.valueOf(jobInfo.getId());

		try {
			JobDynamicScheduler.removeJob(name, group);
			jobInfoDao.delete(id);
			jobLogDao.delete(id);
			
			String prefix = jobInfo.getGlueSource();
			final String jarFileName = String.format("./jar_repositories/%s.jar", prefix);
			final String jarLockFileName = String.format("./jar_repositories_lock/%s.lock", prefix);
			
			FileUtil.processByFileLock(new File(jarLockFileName), new Submittable() {
				@Override
				public Object submit() throws IOException {
					return FileUtil.deleteFile(jarFileName);
				}
			});
			
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
		}
		return ReturnT.FAIL;
	}

	public ReturnT<Void> pause(int id) {
		JobInfo jobInfo = jobInfoDao.loadById(id);
		String group = String.valueOf(jobInfo.getJobGroup());
		String name = String.valueOf(jobInfo.getId());

		try {
			boolean ret = JobDynamicScheduler.pauseJob(name, group); // jobStatus do not store
			return ret ? ReturnT.SUCCESS : ReturnT.FAIL;
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			return ReturnT.FAIL;
		}
	}

	public ReturnT<Void> resume(int id) {
		JobInfo jobInfo = jobInfoDao.loadById(id);
		String group = String.valueOf(jobInfo.getJobGroup());
		String name = String.valueOf(jobInfo.getId());

		try {
			boolean ret = JobDynamicScheduler.resumeJob(name, group);
			return ret ? ReturnT.SUCCESS : ReturnT.FAIL;
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			return ReturnT.FAIL;
		}
	}

	public ReturnT<Void> triggerJob(int id) {
		JobInfo jobInfo = jobInfoDao.loadById(id);
		if (jobInfo == null) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "任务ID非法");
		}

		String group = String.valueOf(jobInfo.getJobGroup());
		String name = String.valueOf(jobInfo.getId());

		try {
			JobDynamicScheduler.triggerJob(name, group);
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			return new ReturnT<Void>(ReturnT.FAIL_CODE, e.getMessage());
		}
	}

	public Map<String, Object> dashboardInfo() {

		int jobInfoCount = jobInfoDao.findAllCount();
		int jobLogCount = jobLogDao.triggerCountByHandleCode(-1);
		int jobLogSuccessCount = jobLogDao.triggerCountByHandleCode(ReturnT.SUCCESS_CODE);

		// executor count
		Set<String> executerAddressSet = new HashSet<String>();
		List<JobGroup> groupList = jobGroupDao.findAll();

		if (CollectionUtils.isNotEmpty(groupList)) {
			for (JobGroup group : groupList) {
				if (CollectionUtils.isNotEmpty(group.getRegistryList())) {
					executerAddressSet.addAll(group.getRegistryList());
				}
			}
		}

		int executorCount = executerAddressSet.size();

		Map<String, Object> dashboardMap = new HashMap<String, Object>();
		dashboardMap.put("jobInfoCount", jobInfoCount);
		dashboardMap.put("jobLogCount", jobLogCount);
		dashboardMap.put("jobLogSuccessCount", jobLogSuccessCount);
		dashboardMap.put("executorCount", executorCount);
		return dashboardMap;
	}

	private static final String TRIGGER_CHART_DATA_CACHE = "trigger_chart_data_cache";

	public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
		// get cache
		String cacheKey = TRIGGER_CHART_DATA_CACHE + "_" + startDate.getTime() + "_" + endDate.getTime();
		Map<String, Object> chartInfo = (Map<String, Object>) LocalCacheUtil.get(cacheKey);
		if (chartInfo != null) {
			return new ReturnT<Map<String, Object>>(chartInfo);
		}

		// process
		List<String> triggerDayList = new ArrayList<String>();
		List<Integer> triggerDayCountRunningList = new ArrayList<Integer>();
		List<Integer> triggerDayCountSucList = new ArrayList<Integer>();
		List<Integer> triggerDayCountFailList = new ArrayList<Integer>();
		int triggerCountRunningTotal = 0;
		int triggerCountSucTotal = 0;
		int triggerCountFailTotal = 0;

		List<Map<String, Object>> triggerCountMapAll = jobLogDao.triggerCountByDay(startDate, endDate);
		if (CollectionUtils.isNotEmpty(triggerCountMapAll)) {
			for (Map<String, Object> item : triggerCountMapAll) {
				String day = String.valueOf(item.get("triggerDay"));
				int triggerDayCount = Integer.valueOf(String.valueOf(item.get("triggerDayCount")));
				int triggerDayCountRunning = Integer.valueOf(String.valueOf(item.get("triggerDayCountRunning")));
				int triggerDayCountSuc = Integer.valueOf(String.valueOf(item.get("triggerDayCountSuc")));
				int triggerDayCountFail = triggerDayCount - triggerDayCountRunning - triggerDayCountSuc;

				triggerDayList.add(day);
				triggerDayCountRunningList.add(triggerDayCountRunning);
				triggerDayCountSucList.add(triggerDayCountSuc);
				triggerDayCountFailList.add(triggerDayCountFail);

				triggerCountRunningTotal += triggerDayCountRunning;
				triggerCountSucTotal += triggerDayCountSuc;
				triggerCountFailTotal += triggerDayCountFail;
			}
		} else {
			for (int i = 4; i > -1; i--) {
				triggerDayList.add(FastDateFormat.getInstance("yyyy-MM-dd").format(DateUtils.addDays(new Date(), -i)));
				triggerDayCountSucList.add(0);
				triggerDayCountFailList.add(0);
			}
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("triggerDayList", triggerDayList);
		result.put("triggerDayCountRunningList", triggerDayCountRunningList);
		result.put("triggerDayCountSucList", triggerDayCountSucList);
		result.put("triggerDayCountFailList", triggerDayCountFailList);

		result.put("triggerCountRunningTotal", triggerCountRunningTotal);
		result.put("triggerCountSucTotal", triggerCountSucTotal);
		result.put("triggerCountFailTotal", triggerCountFailTotal);

		// set cache
		LocalCacheUtil.set(cacheKey, result, 60 * 1000); // cache 60s

		return new ReturnT<Map<String, Object>>(result);
	}

}
