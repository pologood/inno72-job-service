package com.inno72.job.executer.example.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.log.JobLogger;


@Component
@JobHandler("TaskExample")
public class TaskExample implements IJobHandler{

	@Resource
	DataSource ds;
	
	@Override
	public ReturnT<String> execute(String param) throws Exception {
		JobLogger.log("example job, start");
		
		Connection conn = ds.getConnection();
		
		PreparedStatement stm = conn.prepareStatement("select * from inno72.inno72_goods");
		
		ResultSet rs = stm.executeQuery();
		while(rs.next()) {
			String name = rs.getString(1);
			System.out.println(name);
		}
		
		rs.close();
		stm.close();
		conn.close();
		
		
		JobLogger.log("example job, param:"+param);
		JobLogger.log("example job, end");
		return new ReturnT<String>(ReturnT.SUCCESS_CODE, "ok");
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
