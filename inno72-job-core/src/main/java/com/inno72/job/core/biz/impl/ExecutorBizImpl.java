package com.inno72.job.core.biz.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inno72.job.core.biz.AdminBiz;
import com.inno72.job.core.biz.ExecutorBiz;
import com.inno72.job.core.biz.model.LogResult;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.biz.model.TriggerParam;
import com.inno72.job.core.enums.ExecutorBlockStrategyEnum;
import com.inno72.job.core.executor.JobExecutor;
import com.inno72.job.core.glue.GlueFactory;
import com.inno72.job.core.glue.GlueTypeEnum;
import com.inno72.job.core.handle.GlueJobHandler;
import com.inno72.job.core.handle.HttpJobHandler;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.RunnableJarHandler;
import com.inno72.job.core.handle.ScriptJobHandler;
import com.inno72.job.core.log.JobFileAppender;
import com.inno72.job.core.thread.JobThread;
import com.inno72.job.core.util.ExeJarManager;
import com.inno72.job.core.util.FileUtil;
import com.inno72.job.core.util.HttpFormConnector;


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


	protected byte[] downloadJar(int jobId) throws IOException {

		List<AdminBiz> adminBizs = JobExecutor.getAdminBizList();
		if (adminBizs.size() == 0) {
			throw new IOException("adminBiz not found.");
		}

		AdminBiz adminBiz = adminBizs.get(0);
		ReturnT<String> checksum = adminBiz.getJarChecksum(jobId);
		if (checksum.getCode() != ReturnT.SUCCESS_CODE) {
			throw new IOException(checksum.getMsg());
		}

		if (StringUtil.isBlank(checksum.getData())) {
			throw new IOException("retJarResponse Checksum is null");
		}


		Map<String, String> form = new HashMap<String, String>();
		form.put("jodId", String.valueOf(jobId));
		String[] address = StringUtils.split(JobExecutor.getAdminAddresses(), ",");
		byte[] jarFile = null;
		try {
			jarFile = HttpFormConnector.doPost(address[0] + "/api/download/jar", form, 10000);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		if (jarFile == null) {
			throw new IOException("jar download fail");
		}


		if (!checksum.getData().equalsIgnoreCase(FileUtil.GetMD5Code(jarFile))) {
			throw new IOException("retJarResponse Checksum is wrong");
		}

		return jarFile;

	}

	@Override
	public ReturnT<Void> run(TriggerParam triggerParam) {
		// load old：jobHandler + jobThread
		JobThread jobThread = JobExecutor.loadJobThread(triggerParam.getJobId());
		String removeOldReason = null;

		// valid：jobHandler + jobThread
		GlueTypeEnum glueTypeEnum = GlueTypeEnum.match(triggerParam.getGlueType());

		if (GlueTypeEnum.JAVA_BEAN == glueTypeEnum) {

			IJobHandler jobHandler = JobExecutor.loadJobHandler(triggerParam.getExecutorHandler());
			if (jobHandler == null) {
				return new ReturnT<Void>(ReturnT.FAIL_CODE,
						"job handler [" + triggerParam.getExecutorHandler() + "] not found.");
			}

			triggerParam.setHandler(jobHandler);

		} else if (GlueTypeEnum.JAVA_JAR_INTERNAL == glueTypeEnum) {

			try {
				byte[] jarFile = downloadJar(triggerParam.getJobId());
				triggerParam.setJarFile(jarFile);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return new ReturnT<Void>(ReturnT.FAIL_CODE, e.getMessage());
			}

		} else if (GlueTypeEnum.JAVA_JAR_EXTERNAL == glueTypeEnum) {

			try {
				byte[] jar = downloadJar(triggerParam.getJobId());
				ExeJarManager exeJarManager = JobExecutor.getApplicationContext().getBean(ExeJarManager.class);
				File jarFile = new File(
						exeJarManager.getJarFileName(triggerParam.getJobId(), triggerParam.getGlueUpdatetime()));
				exeJarManager.saveJarFile(jarFile, jar);
				IJobHandler jobHandler = new RunnableJarHandler(triggerParam.getJobId(),
						triggerParam.getGlueUpdatetime(), triggerParam.getGlueSource(),
						GlueTypeEnum.match(triggerParam.getGlueType()));
				triggerParam.setHandler(jobHandler);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return new ReturnT<Void>(ReturnT.FAIL_CODE, e.getMessage());
			}

		} else if (GlueTypeEnum.HTTP == glueTypeEnum) {

			IJobHandler jobHandler = JobExecutor.getApplicationContext().getBean(HttpJobHandler.class);
			if (jobHandler == null) {
				return new ReturnT<Void>(ReturnT.FAIL_CODE,
						"job httphandler not found.");
			}
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
				} else {
					logger.warn("由于 JAVA_JAR_INTERNAL 无法使用COVER_EARLY 改为SERIAL_EXECUTION handler:"
							+ triggerParam.getExecutorHandler());
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
