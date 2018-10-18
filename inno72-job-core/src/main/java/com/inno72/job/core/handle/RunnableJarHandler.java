package com.inno72.job.core.handle;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.executor.JobExecutor;
import com.inno72.job.core.glue.GlueTypeEnum;
import com.inno72.job.core.log.JobFileAppender;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.core.util.ExeJarManager;
import com.inno72.job.core.util.FileUtil;
import com.inno72.job.core.util.ScriptUtil;
import com.inno72.job.core.util.ShardingUtil;

public class RunnableJarHandler extends IJobHandler {

	private int jobId;
	private long glueUpdatetime;
	private String gluesource;
	private GlueTypeEnum glueType;

	public RunnableJarHandler(int jobId, long glueUpdatetime, String gluesource, GlueTypeEnum glueType) {
		this.jobId = jobId;
		this.glueUpdatetime = glueUpdatetime;
		this.gluesource = gluesource;
		this.glueType = glueType;
	}

	@Override
	public ReturnT<String> execute(String param) throws Exception {

		// make script file
		ExeJarManager exeJarManager = JobExecutor.getApplicationContext().getBean(ExeJarManager.class);
		String jarFileName = exeJarManager.getJarFileName(jobId, glueUpdatetime);
		

		ScriptUtil.markScriptFile(jarFileName, gluesource);

		// log file
		String logFileName = JobFileAppender.contextHolder.get();

		// script params：0=param、1=分片序号、2=分片总数
		ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
		String[] scriptParams = new String[3];
		scriptParams[0] = param;
		scriptParams[1] = String.valueOf(shardingVO.getIndex());
		scriptParams[2] = String.valueOf(shardingVO.getTotal());

		// invoke
		JobLogger.log("----------- jar:" + jarFileName + " -----------");
		int exitValue = ScriptUtil.execToFile("java -jar", jarFileName, logFileName, scriptParams);

		FileUtil.deleteFile(jarFileName);


		ReturnT<String> result = (exitValue == 0) ? new ReturnT<String>(ReturnT.SUCCESS_CODE, "ok")
				: new ReturnT<String>(ReturnT.FAIL_CODE, "script exit value(" + exitValue + ") is failed");
		return result;
	}

}
