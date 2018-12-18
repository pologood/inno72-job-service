package com.inno72.job.payment.label;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;

/**
 * Hello world!
 *
 */
@JobHandler("inno72.task.payment.label")
@Component
public class PaymentLabelTask implements IJobHandler {
	
	final static int TAG_ID = 4;
	
	@Resource
	private DataSource ds;

	@Override
	public ReturnT<String> execute(String param) throws Exception {

		int count = handleData();
		
		return new ReturnT<String>(ReturnT.SUCCESS_CODE,
			 " compress 处理" + count  + "条");
	}


	protected int handleData() throws SQLException {

		Connection conn = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

			String deleteSql = "delete from inno72.inno72_game_user_tag_ref where tag_id=?";
			conn = ds.getConnection();
			conn.setAutoCommit(false);

			stm = conn.prepareStatement(deleteSql);
			stm.setInt(1, TAG_ID);
			stm.executeUpdate();
			stm.close();

			String newSql = " insert into inno72.inno72_game_user_tag_ref(id, user_id, tag_id, content, create_time) " +
					"  select REPLACE(uuid(),'-','') as id, t.id as user_id, ? as tag_id, CASE when t.times >= 2 and t.pay_price >=1 THEN '购物控' " +
					"  ELSE '样品控' END as label, ?  from (select u.id, a.times, a.pay_price " +
					"    from inno72.inno72_game_user u left join ( " +
					"   select user_id, count(1) as times, max(pay_price) as pay_price " +
					"      from inno72.inno72_order o  where o.pay_status = 1 " +
					"        group by o.user_id ) a on a.user_id = u.id) t " ;
			stm = conn.prepareStatement(newSql);
			stm.setInt(1, TAG_ID);
			stm.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
			int count = stm.executeUpdate();
			
			conn.commit();
			return count;
		} catch (SQLException e) {
			if (conn != null) conn.rollback();
			throw e;
		} finally {
			if (rs != null) rs.close();
			if (stm != null) stm.close();
			if (conn != null) conn.close();
		}

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
