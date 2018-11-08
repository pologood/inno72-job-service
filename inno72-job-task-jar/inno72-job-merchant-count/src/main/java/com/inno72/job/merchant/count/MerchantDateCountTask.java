package com.inno72.job.merchant.count;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import javax.sql.DataSource;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;


/**
 *
 */
@JobHandler("inno72.task.MerchantDataCountTask")
@Component
public class MerchantDateCountTask implements IJobHandler {

	@Resource
	DataSource ds;

	@Resource
	private MongoOperations mongoOperations;

	@Override
	public ReturnT<String> execute(String param) throws Exception {

		String lastActionTime = getLastActionTime();
		Query query = new Query();
		if (!StringUtils.isEmpty(lastActionTime)){
			query.addCriteria(Criteria.where("clientTime").gt(lastActionTime));
		}
		query.with(new Sort(new Sort.Order(Sort.Direction.ASC,"clientTime")));

		List<Inno72MachineInfomation> inno72MachineInfomations = mongoOperations
				.find(query, Inno72MachineInfomation.class, "Inno72MachineInfomation");

		return null;
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}

	private String getLastActionTime() throws SQLException {

		Connection conn = null;
		PreparedStatement stm = null;

		try {
			conn = ds.getConnection();
			stm = conn.prepareStatement("select DATE_FORMAT(MAX(last_update_time),'%Y-%m-%d %T') from inno72_merchant_total_count_by_day");
			ResultSet resultSet = stm.executeQuery();
			String str = resultSet.getString(0);

			return str;
		} finally {
			if (stm != null)
				stm.close();
			if (conn != null)
				conn.close();
		}
	}

}
