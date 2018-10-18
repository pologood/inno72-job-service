package com.inno72.job.admin.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inno72.job.admin.model.JobInfo;
import com.inno72.job.admin.model.JobLog;
import com.inno72.job.admin.scheduler.JobDynamicScheduler;
import com.inno72.job.admin.service.JobService;
import com.inno72.job.core.biz.ExecutorBiz;
import com.inno72.job.core.biz.model.LogResult;
import com.inno72.job.core.biz.model.ReturnT;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/joblog")
public class JobLogController {
	
	private static final Logger logger = LoggerFactory.getLogger(JobLogController.class);
	
	@Resource
	private JobService jobService;
	
	
	@RequestMapping("/getJobsByGroup")
	@ResponseBody
	public ReturnT<List<JobInfo>> getJobsByGroup(@RequestParam(required = false, defaultValue = "0") int jobGroup){
		List<JobInfo> list = jobService.findJobsByGroup(jobGroup);
		return new ReturnT<List<JobInfo>>(list);
	}
	
	
	@RequestMapping("/pageList")
	@ResponseBody
	public ReturnT<Map<String, Object>> pageList(@RequestParam(required = false, defaultValue = "1") int pageNo,  
			@RequestParam(required = false, defaultValue = "10") int pageSize,
			@RequestParam(required = false, defaultValue = "0") int jobGroup, 
			@RequestParam(required = false, defaultValue = "0") int jobId, 
			@RequestParam(required = false, defaultValue = "0") int logStatus, String filterTime) {
		
		// parse param
		Date triggerTimeStart = null;
		Date triggerTimeEnd = null;
		if (StringUtils.isNotBlank(filterTime)) {
			String[] temp = filterTime.split(" - ");
			if (temp!=null && temp.length == 2) {
				try {
					triggerTimeStart = DateUtils.parseDate(temp[0], new String[]{"yyyy-MM-dd HH:mm:ss"});
					triggerTimeEnd = DateUtils.parseDate(temp[1], new String[]{"yyyy-MM-dd HH:mm:ss"});
				} catch (ParseException e) {	}
			}
		}
		
		// page query
		List<JobLog> list = jobService.jobLogPageList(pageNo-1, pageSize, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
		int list_count = jobService.jobLogPageCount(pageNo-1, pageSize, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
		
		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
	    maps.put("recordsTotal", list_count);		// 总记录数
	    maps.put("data", list);  					// 分页列表
		return new ReturnT<Map<String, Object>>(maps);
	}
	
	
	@RequestMapping("/logDetailPage")
	@ResponseBody
	public ReturnT<JobLog> logDetailPage(int id){

		// base check
		
		JobLog jobLog = jobService.loadJobLogInfo(id);
		if (jobLog == null) {
            return new ReturnT<JobLog>(-1, "非法日志id");
		}

        return new ReturnT<JobLog>(jobLog);
	}
	
	@RequestMapping("/logDetailCat")
	@ResponseBody
	public ReturnT<LogResult> logDetailCat(String executorAddress, long triggerTime, int logId, int fromLineNum){
		try {
			
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			ExecutorBiz executorBiz = JobDynamicScheduler.getExecutorBiz(executorAddress);
			ReturnT<LogResult> logResult = executorBiz.log(triggerTime, logId, fromLineNum);

			// is end
            if (logResult.getData()!=null && logResult.getData().getFromLineNum() > logResult.getData().getToLineNum()) {
                JobLog jobLog = jobService.loadJobLogInfo(logId);
                if (jobLog.getHandleCode() > 0) {
                    logResult.getData().setEnd(true);
                }
            }

			return logResult;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ReturnT<LogResult>(ReturnT.FAIL_CODE, e.getMessage());
		}
	}
	
	@RequestMapping("/logKill")
	@ResponseBody
	public ReturnT<Void> logKill(int id){
		return this.jobService.killLog(id);
	}

	@RequestMapping("/clearLog")
	@ResponseBody
	public ReturnT<Void> clearLog(int jobGroup, int jobId, int type){

		Date clearBeforeTime = null;
		int clearBeforeNum = 0;
		if (type == 1) {
			clearBeforeTime = DateUtils.addMonths(new Date(), -1);	// 清理一个月之前日志数据
		} else if (type == 2) {
			clearBeforeTime = DateUtils.addMonths(new Date(), -3);	// 清理三个月之前日志数据
		} else if (type == 3) {
			clearBeforeTime = DateUtils.addMonths(new Date(), -6);	// 清理六个月之前日志数据
		} else if (type == 4) {
			clearBeforeTime = DateUtils.addYears(new Date(), -1);	// 清理一年之前日志数据
		} else if (type == 5) {
			clearBeforeNum = 1000;		// 清理一千条以前日志数据
		} else if (type == 6) {
			clearBeforeNum = 10000;		// 清理一万条以前日志数据
		} else if (type == 7) {
			clearBeforeNum = 30000;		// 清理三万条以前日志数据
		} else if (type == 8) {
			clearBeforeNum = 100000;	// 清理十万条以前日志数据
		} else if (type == 9) {
			clearBeforeNum = 0;			// 清理所有日志数据
		} else {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "清理类型参数异常");
		}

		this.jobService.clearLog(jobGroup, jobId, clearBeforeTime, clearBeforeNum);
		return ReturnT.SUCCESS;
	}

	

}
