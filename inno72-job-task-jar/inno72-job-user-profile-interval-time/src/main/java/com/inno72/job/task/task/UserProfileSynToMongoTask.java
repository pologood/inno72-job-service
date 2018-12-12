package com.inno72.job.task.task;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.task.mapper.Inno72GameUserLoginMapper;

import javax.annotation.Resource;

/**
 * 同步用户画像标签到mongo
 */
@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.UserProfileSynToMongoTask")
public class UserProfileSynToMongoTask implements IJobHandler
{
	@Resource
	private Inno72GameUserLoginMapper inno72GameUserLoginMapper;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		JobLogger.log("同步用户画像标签到mongo job, start");

		JobLogger.log("同步用户画像标签到mongo job, end");
		return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
	}

	@Override
	public void init() {
		
		
	}

	@Override
	public void destroy() {
		
		
	}
   
}
