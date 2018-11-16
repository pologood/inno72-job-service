package org.inno72.job.example.task;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.inno72.job.example.task.mapper.Inno72ActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobLogger;

@JobMapperScanner(value = "classpath*:/org/inno72/job/example/task/mapper/*.xml", basePackage="org.inno72.job.example.task.mapper")
@JobHandler("inno72.task.example")
public class TaskExample implements IJobHandler
{
	
	@Resource
	DataSource ds;
	
	@Autowired
	private Inno72ActivityMapper activity;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		JobLogger.log("example job, start");
		
////		Connection conn = ds.getConnection();
////		
////		PreparedStatement stm = conn.prepareStatement("select * from inno72.inno72_goods");
////		
////		ResultSet rs = stm.executeQuery();
////		while(rs.next()) {
////			String name = rs.getString(1);
////			System.out.println(name);
////		}
//		
//		rs.close();
//		stm.close();
//		conn.close();
		
		List<Map<String, String>> rets = activity.queryActivityInfo(0);
		
		for(Map<String, String> ret : rets) {
			for (String key : ret.keySet()) {
				System.out.println(String.format("{%s:%s}", key,ret.get(key)));
			}
		}
		
		JobLogger.log("example job, param:");
		JobLogger.log("example job, end");
		return new ReturnT<String>(ReturnT.SUCCESS_CODE, "ok");
	}

	@Override
	public void init() {
		
		
	}

	@Override
	public void destroy() {
		
		
	}
   
}
