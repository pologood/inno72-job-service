package com.inno72.job.admin.scheduler;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inno72.job.admin.model.JobGroup;
import com.inno72.job.admin.model.JobInfo;
import com.inno72.job.admin.model.JobLog;
import com.inno72.job.admin.utils.MailUtil;
import com.inno72.job.core.biz.model.ReturnT;


public class JobFailMonitorHelper {
	private static Logger logger = LoggerFactory.getLogger(JobFailMonitorHelper.class);
	
	private static JobFailMonitorHelper instance = new JobFailMonitorHelper();
	public static JobFailMonitorHelper getInstance(){
		return instance;
	}

	// ---------------------- monitor ----------------------

	private LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>(0xfff8);

	private Thread monitorThread;
	private volatile boolean toStop = false;
	public void start(){
		monitorThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// monitor
				while (!toStop) {
					try {
						List<Integer> jobLogIdList = new ArrayList<Integer>();
						int drainToNum = JobFailMonitorHelper.instance.queue.drainTo(jobLogIdList);

						if (CollectionUtils.isNotEmpty(jobLogIdList)) {
							for (Integer jobLogId : jobLogIdList) {
								if (jobLogId==null || jobLogId==0) {
									continue;
								}
								JobLog log = JobDynamicScheduler.jobLogDao.load(jobLogId);
								if (log == null) {
									continue;
								}
								if (ReturnT.SUCCESS.getCode() == log.getTriggerCode() && log.getHandleCode() == 1000) {
									JobFailMonitorHelper.monitor(jobLogId);
									logger.info(">>>>>>>>>>> job monitor, job running, JobLogId:{}", jobLogId);
								} else if (ReturnT.SUCCESS.getCode() == log.getTriggerCode() && ReturnT.SUCCESS.getCode() == log.getHandleCode()) {
									// job success, pass
									logger.info(">>>>>>>>>>> job monitor, job success, JobLogId:{}", jobLogId);
								} else if (ReturnT.FAIL.getCode() == log.getTriggerCode()
										|| ReturnT.FAIL.getCode() == log.getHandleCode()
										|| ReturnT.FAIL_RETRY.getCode() == log.getHandleCode() ) {
									// job fail,
									failAlarm(log);
									logger.info(">>>>>>>>>>> job monitor, job fail, JobLogId:{}", jobLogId);
								} else {
									JobFailMonitorHelper.monitor(jobLogId);
									logger.info(">>>>>>>>>>> job monitor, job status unknown, JobLogId:{}", jobLogId);
								}
							}
						}

						TimeUnit.SECONDS.sleep(10);
					} catch (Exception e) {
						logger.error("job monitor error:{}", e);
					}
				}

				// monitor all clear
				List<Integer> jobLogIdList = new ArrayList<Integer>();
				int drainToNum = getInstance().queue.drainTo(jobLogIdList);
				if (jobLogIdList!=null && jobLogIdList.size()>0) {
					for (Integer jobLogId: jobLogIdList) {
						JobLog log = JobDynamicScheduler.jobLogDao.load(jobLogId);
						if (ReturnT.FAIL_CODE == log.getTriggerCode()|| ReturnT.FAIL_CODE==log.getHandleCode()) {
							// job fail,
							failAlarm(log);
							logger.info(">>>>>>>>>>> job monitor last, job fail, JobLogId:{}", jobLogId);
						}
					}
				}

			}
		});
		monitorThread.setDaemon(true);
		monitorThread.start();
	}

	public void toStop(){
		toStop = true;
		// interrupt and wait
		monitorThread.interrupt();
		try {
			monitorThread.join();
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	// producer
	public static void monitor(int jobLogId){
		getInstance().queue.offer(jobLogId);
	}


	// ---------------------- alarm ----------------------

	// email alarm template
	private static final String mailBodyTemplate = "<h5>监控告警明细：</span>" +
			"<table border=\"1\" cellpadding=\"3\" style=\"border-collapse:collapse; width:80%;\" >\n" +
			"   <thead style=\"font-weight: bold;color: #ffffff;background-color: #ff8c00;\" >" +
			"      <tr>\n" +
			"         <td>执行器</td>\n" +
			"         <td>任务ID</td>\n" +
			"         <td>任务描述</td>\n" +
			"         <td>告警类型</td>\n" +
			"      </tr>\n" +
			"   <thead/>\n" +
			"   <tbody>\n" +
			"      <tr>\n" +
			"         <td>{0}</td>\n" +
			"         <td>{1}</td>\n" +
			"         <td>{2}</td>\n" +
			"         <td>调度失败</td>\n" +
			"      </tr>\n" +
			"   <tbody>\n" +
			"</table>";

	/**
	 * fail alarm
	 *
	 * @param jobLog
	 */
	private void failAlarm(JobLog jobLog){

		// send monitor email
		JobInfo info = JobDynamicScheduler.jobInfoDao.loadById(jobLog.getJobId());
		if (info!=null && info.getAlarmEmail()!=null && info.getAlarmEmail().trim().length()>0) {

			Set<String> emailSet = new HashSet<String>(Arrays.asList(info.getAlarmEmail().split(",")));
			for (String email: emailSet) {
				JobGroup group = JobDynamicScheduler.jobGroupDao.load(Integer.valueOf(info.getJobGroup()));

				String title = "调度中心监控报警";
				String content = MessageFormat.format(mailBodyTemplate, group!=null?group.getTitle():"null", info.getId(), info.getJobDesc());

				MailUtil.sendMail(email, title, content);
			}
		}

		// TODO, custom alarm strategy, such as sms

	}

}
