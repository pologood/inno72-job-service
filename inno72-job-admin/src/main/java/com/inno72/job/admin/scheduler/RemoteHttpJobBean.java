package com.inno72.job.admin.scheduler;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

//@DisallowConcurrentExecution
public class RemoteHttpJobBean extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(RemoteHttpJobBean.class);

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {

		// load jobId
		JobKey jobKey = context.getTrigger().getJobKey();
		Integer jobId = Integer.valueOf(jobKey.getName());

		// trigger
		JobTrigger.trigger(jobId);
	}

}