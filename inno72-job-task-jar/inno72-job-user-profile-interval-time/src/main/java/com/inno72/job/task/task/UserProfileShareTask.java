package com.inno72.job.task.task;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.task.mapper.Inno72GameUserLoginMapper;

import javax.annotation.Resource;

/**
 * 分享标签任务
 */
@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.UserProfileUrlAnalysisTask")
public class UserProfileShareTask implements IJobHandler
{
	@Resource
	private Inno72GameUserLoginMapper inno72GameUserLoginMapper;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		JobLogger.log("分享标签 job, start");

		JobLogger.log("分享标签 job, end");
		return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
	}

	@Override
	public void init() {
		
		
	}

	@Override
	public void destroy() {
		
		
	}
   
}
