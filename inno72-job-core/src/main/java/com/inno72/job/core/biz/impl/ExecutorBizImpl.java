package com.inno72.job.core.biz.impl;

import java.util.Date;
import java.util.List;

import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inno72.job.core.biz.AdminBiz;
import com.inno72.job.core.biz.ExecutorBiz;
import com.inno72.job.core.biz.model.JarResponse;
import com.inno72.job.core.biz.model.LogResult;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.biz.model.TriggerParam;
import com.inno72.job.core.enums.ExecutorBlockStrategyEnum;
import com.inno72.job.core.executor.JobExecutor;
import com.inno72.job.core.glue.GlueFactory;
import com.inno72.job.core.glue.GlueTypeEnum;
import com.inno72.job.core.handle.GlueJobHandler;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.RunnableJarHandler;
import com.inno72.job.core.handle.ScriptJobHandler;
import com.inno72.job.core.log.JobFileAppender;
import com.inno72.job.core.thread.JobThread;
import com.inno72.job.core.util.FileUtil;


public class ExecutorBizImpl implements ExecutorBiz {
	private static Logger logger = LoggerFactory.getLogger(ExecutorBizImpl.class);

	@Override
	public ReturnT<Void> beat() {
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<Void> idleBeat(int jobId) {

		// isRunningOrHasQueue
		boolean isRunningOrHasQueue = false;
		JobThread jobThread = JobExecutor.loadJobThread(jobId);
		if (jobThread != null && jobThread.isRunningOrHasQueue()) {
			isRunningOrHasQueue = true;
		}

		if (isRunningOrHasQueue) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "job thread is running or has trigger queue.");
		}
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<Void> kill(int jobId) {
		// kill handlerThread, and create new one
		JobThread jobThread = JobExecutor.loadJobThread(jobId);
		if (jobThread != null) {
			JobExecutor.removeJobThread(jobId, "人工手动终止");
			return ReturnT.SUCCESS;
		}

		return new ReturnT<Void>(ReturnT.SUCCESS_CODE, "job thread aleady killed.");
	}

	@Override
	public ReturnT<LogResult> log(long logDateTim, int logId, int fromLineNum) {
		// log filename: logPath/yyyy-MM-dd/9999.log
		String logFileName = JobFileAppender.makeLogFileName(new Date(logDateTim), logId);

		LogResult logResult = JobFileAppender.readLog(logFileName, fromLineNum);
		return new ReturnT<LogResult>(logResult);
	}

	@Override
	public ReturnT<Void> run(TriggerParam triggerParam) {
		// load old：jobHandler + jobThread
		JobThread jobThread = JobExecutor.loadJobThread(triggerParam.getJobId());
		String removeOldReason = null;

		// valid：jobHandler + jobThread
		GlueTypeEnum glueTypeEnum = GlueTypeEnum.match(triggerParam.getGlueType());
		if (GlueTypeEnum.JAVA_JAR_INTERNAL == glueTypeEnum) {

			List<AdminBiz> adminBizs = JobExecutor.getAdminBizList();
			if (adminBizs.size() == 0) {
				return new ReturnT<Void>(ReturnT.FAIL_CODE, "adminBiz not found.");
			}

			AdminBiz adminBiz = adminBizs.get(0);
			ReturnT<JarResponse> retJarResponse = adminBiz.downloadJar(triggerParam.getJobId());

			if (retJarResponse.getCode() != ReturnT.SUCCESS_CODE) {
				return new ReturnT<Void>(retJarResponse.getCode(), retJarResponse.getMsg());
			}

			if (retJarResponse.getData() == null) {
				return new ReturnT<Void>(ReturnT.FAIL_CODE, "retJarResponse data is null");
			}

			if (StringUtil.isBlank(retJarResponse.getData().getChecksum())) {
				return new ReturnT<Void>(ReturnT.FAIL_CODE, "retJarResponse Checksum is null");
			}

			if (!retJarResponse.getData().getChecksum()
					.equalsIgnoreCase(FileUtil.GetMD5Code(retJarResponse.getData().getJarFile()))) {
				return new ReturnT<Void>(ReturnT.FAIL_CODE, "retJarResponse Checksum is wrong");
			}

			triggerParam.setJarFile(retJarResponse.getData().getJarFile());

		} else if (GlueTypeEnum.JAVA_JAR_EXTERNAL == glueTypeEnum) {

			IJobHandler jobHandler = new RunnableJarHandler(triggerParam.getJobId(), triggerParam.getGlueUpdatetime(),
					triggerParam.getGlueSource(), GlueTypeEnum.match(triggerParam.getGlueType()));
			triggerParam.setHandler(jobHandler);

		} else if (GlueTypeEnum.GLUE_GROOVY == glueTypeEnum) {

			try {
				IJobHandler originJobHandler = GlueFactory.getInstance().loadNewInstance(triggerParam.getGlueSource());
				IJobHandler jobHandler = new GlueJobHandler(originJobHandler, triggerParam.getGlueUpdatetime());
				triggerParam.setHandler(jobHandler);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return new ReturnT<Void>(ReturnT.FAIL_CODE, e.getMessage());
			}

		} else if (glueTypeEnum != null && glueTypeEnum.isScript()) {
			IJobHandler jobHandler = new ScriptJobHandler(triggerParam.getJobId(), triggerParam.getGlueUpdatetime(),
					triggerParam.getGlueSource(), GlueTypeEnum.match(triggerParam.getGlueType()));
			triggerParam.setHandler(jobHandler);
		} else {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "glueType[" + triggerParam.getGlueType() + "] is not valid.");
		}

		// executor block strategy
		if (jobThread != null) {
			ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum
					.match(triggerParam.getExecutorBlockStrategy(), null);
			if (ExecutorBlockStrategyEnum.DISCARD_LATER == blockStrategy) {
				// discard when running
				if (jobThread.isRunningOrHasQueue()) {
					return new ReturnT<Void>(ReturnT.FAIL_CODE,
							"阻塞处理策略-生效：" + ExecutorBlockStrategyEnum.DISCARD_LATER.getTitle());
				}
			} else if (ExecutorBlockStrategyEnum.COVER_EARLY == blockStrategy) {
				// kill running jobThread
				if (jobThread.isRunningOrHasQueue() && GlueTypeEnum.JAVA_JAR_INTERNAL != glueTypeEnum) {
					removeOldReason = "阻塞处理策略-生效：" + ExecutorBlockStrategyEnum.COVER_EARLY.getTitle();

					jobThread = null;
				}
			} else {
				// just queue trigger
			}
		}

		// replace thread (new or exists invalid)
		if (jobThread == null) {
			jobThread = JobExecutor.registJobThread(triggerParam.getJobId(), removeOldReason);
		}

		// push data to queue
		ReturnT<Void> pushResult = jobThread.pushTriggerQueue(triggerParam);
		return pushResult;
	}

}
