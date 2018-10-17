package org.inno72.job.example.task;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.log.JobLogger;

/**
 * Hello world!
 *
 */
@JobHandler("inno72.task.example")
public class TaskExample extends IJobHandler
{

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		JobLogger.log("example job, start");
		JobLogger.log("example job, param:"+param);
		JobLogger.log("example job, end");
		return new ReturnT<String>(ReturnT.SUCCESS_CODE, "ok");
	}
   
}
