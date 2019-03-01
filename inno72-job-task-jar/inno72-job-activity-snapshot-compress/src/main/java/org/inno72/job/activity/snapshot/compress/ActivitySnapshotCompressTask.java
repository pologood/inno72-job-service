package org.inno72.job.activity.snapshot.compress;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.log.JobLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@JobHandler("inno72.task.ActivitySnapshotCompressTask")
@Component
public class ActivitySnapshotCompressTask
		implements IJobHandler
{
	@Resource
	DataSource ds;

	public ReturnT<String> execute(String param)
			throws Exception
	{
		DateFormat formatStartTime = new SimpleDateFormat("yyyy-MM-dd");

		DateFormat formatHandleTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		long workTime = 0L;
		if (StringUtils.isNotBlank(param))
		{
			String[] params = StringUtils.split(param, ',');
			workTime = formatStartTime.parse(params[0]).getTime();
		}
		else
		{
			workTime = System.currentTimeMillis() - 86400000L;
		}
		Date workDate = new Date(workTime);

		DateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd ");
		String time = formatTime.format(workDate);
		String startTime = time + "00:00:00";
		String endTime = time + "23:59:59";
		if (isExistCompressData(formatHandleTime.parse(startTime), formatHandleTime.parse(endTime))) {
			return new ReturnT(0, time + "已经处理");
		}
		int deleteCount = compressActivitySnapShot(formatHandleTime.parse(startTime), formatHandleTime.parse(endTime));

		return new ReturnT(0, startTime + " compress 处理" + deleteCount + "条");
	}

	private boolean isExistCompressData(Date fromDate, Date toDate)
			throws SQLException
	{
		Connection conn = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		try
		{
			String querySql = "select distinct activity_id, machine_id, machine_code, activity_type from inno72.inno72_statistic_activity_machine_snapshot where handle_time between ? and ? and is_compress = 1 ";
			conn = this.ds.getConnection();

			stm = conn.prepareStatement(querySql);
			stm.setTimestamp(1, new Timestamp(fromDate.getTime()));
			stm.setTimestamp(2, new Timestamp(toDate.getTime()));
			rs = stm.executeQuery();
			return rs.next();
		}
		finally
		{
			if (rs != null) {
				rs.close();
			}
			if (stm != null) {
				stm.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

	private int compressActivitySnapShot(Date fromDate, Date toDate)
			throws SQLException
	{
		Connection conn = null;
		PreparedStatement stm = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
		try
		{
			String insertSql = "insert into inno72.inno72_statistic_activity_machine_snapshot(activity_id, machine_id, machine_code, activity_type, handle_time, is_compress) select a.activity_id, a.machine_id, a.machine_code, a.activity_type, ? , 1 from (select distinct activity_id, machine_id, machine_code, activity_type from inno72.inno72_statistic_activity_machine_snapshot where handle_time between ? and ? and is_compress = 0) a";

			conn = this.ds.getConnection();
			conn.setAutoCommit(false);

			stm = conn.prepareStatement(insertSql);

			stm.setTimestamp(1, new Timestamp(fromDate.getTime()));
			stm.setTimestamp(2, new Timestamp(fromDate.getTime()));
			stm.setTimestamp(3, new Timestamp(toDate.getTime()));

			int insertCount = stm.executeUpdate();
			stm.close();

			String deleteSql = "delete from inno72.inno72_statistic_activity_machine_snapshot where handle_time between ? and ? and is_compress = 0";
			stm = conn.prepareStatement(deleteSql);
			stm.setTimestamp(1, new Timestamp(fromDate.getTime()));
			stm.setTimestamp(2, new Timestamp(toDate.getTime()));

			int deleteCount = stm.executeUpdate();
			conn.commit();
			JobLogger.log("compress job, insert:" + insertCount + " delete:" + deleteCount + " from:" + df.format(fromDate) + " to:" + df.format(toDate), new Object[0]);

			return deleteCount;
		}
		catch (SQLException e)
		{
			if (conn != null) {
				conn.rollback();
			}
			throw e;
		}
		finally
		{
			if (stm != null) {
				stm.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

	public void init() {}

	public void destroy() {}
}
