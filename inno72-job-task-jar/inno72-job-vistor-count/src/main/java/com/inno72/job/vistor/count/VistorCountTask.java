package com.inno72.job.vistor.count;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.log.JobLogger;


/**
 * Hello world!
 *
 */
@JobHandler("inno72.task.VistorCountTask")
@Component
public class VistorCountTask implements IJobHandler {

	@Resource
	DataSource ds;

	@Resource
	private MongoOperations mongoOperations;

	@Override
	public void init() {


	}

	@Override
	public void destroy() {


	}

	@Override
	public ReturnT<String> execute(String param) throws Exception {


		boolean isCover = false;
		List<Date> dateList = new ArrayList<Date>();
		if (StringUtils.isNotBlank(param)) {
			String[] params = StringUtils.split(param, ',');
			if (params.length == 2) {
				if ("1".equals(params[1])) isCover = true;

				DateFormat formatStartTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				long workTime = formatStartTime.parse(params[0]).getTime();
				Date workDate = new Date(workTime);
				dateList.add(workDate);

			} else if (params.length == 3) {
				if ("1".equals(params[2])) isCover = true;
				DateFormat formatStartTime = new SimpleDateFormat("yyyy-MM-dd");

				long workTime1 = formatStartTime.parse(params[0]).getTime();
				long workTime2 = formatStartTime.parse(params[1]).getTime();

				int gap = daysBetween(new Date(workTime1), new Date(workTime2));

				for (int i = 0; i < gap; i++) {
					long day = i * 24 * 60 * 60 * 1000L;
					for (int j = 0; j < 24; j++) {
						dateList.add(new Date(workTime1 + day + j * 1000L * 60 * 60));
					}
				}
			} else {
				return new ReturnT<String>(ReturnT.FAIL_CODE, "参数错误:" + param);
			}
		} else {
			long currentTime = System.currentTimeMillis();
			long workTime = currentTime - 1000 * 60 * 60;
			Date workDate = new Date(workTime);
			dateList.add(workDate);
		}


		DateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH");
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");

		for (Date workDate : dateList) {

			String date = formatDate.format(workDate);
			String time = formatTime.format(workDate);
			ReturnT<String> ret = handleData(date, time, isCover);
			JobLogger.log(ret.getMsg());
		}

		StringBuilder sb = new StringBuilder("已处理	cover is " + isCover + ":[");
		for (Date workDate : dateList) {
			sb.append(formatTime.format(workDate) + ":00:00, ");
		}
		sb.append("]");

		return new ReturnT<String>(ReturnT.SUCCESS_CODE, sb.toString());

	}


	private ReturnT<String> handleData(String dateTime, String hourTime, boolean isCover)
			throws SQLException, ParseException {

		String startTime = hourTime + ":00:00";
		String endTime = hourTime + ":59:59";

		DateFormat formatStartTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		int deleteNum = 0;
		if (isCover) {
			deleteNum = deleteMachineInfos(startTime);
		}

		if (queryVistorInfo(startTime)) {
			return new ReturnT<String>(ReturnT.SUCCESS_CODE, hourTime + "已经被处理");
		}

		Query pointLogQuery = new Query();

		Criteria cond = new Criteria();
		cond.andOperator(Criteria.where("type").is("1"), Criteria.where("pointTime").gte(startTime),
				Criteria.where("pointTime").lte(endTime));
		pointLogQuery.addCriteria(cond);

		List<PointLog> pointLogs = mongoOperations.find(pointLogQuery, PointLog.class, "PointLog");

		if (pointLogs == null || pointLogs.isEmpty()) {
			return new ReturnT<String>(ReturnT.SUCCESS_CODE, "处理0条pointLogs");
		}

		Map<String, Integer> visterCount = new HashMap<String, Integer>();

		for (PointLog pointLog : pointLogs) {

			if (StringUtils.isEmpty(pointLog.getMachineCode()) || StringUtils.isEmpty(pointLog.getTag())) {
				continue;
			}

			JSONObject tagInfo = null;
			try {
				tagInfo = JSON.parseObject(pointLog.getTag());
			} catch (Exception e) {
			}

			if (tagInfo == null) {
				continue;
			}

			String count = tagInfo.getString("count");
			int vistor = 0;
			try {
				vistor = Integer.parseInt(count);
			} catch (Exception e) {
			}


			if (visterCount.containsKey(pointLog.getMachineCode())) {
				Integer perCount = visterCount.get(pointLog.getMachineCode());
				perCount += vistor;
				visterCount.put(pointLog.getMachineCode(), perCount);

			} else {
				visterCount.put(pointLog.getMachineCode(), vistor);
			}
		}

		List<Inno72MachineVistorCountHour> vistorInfos = new LinkedList<Inno72MachineVistorCountHour>();

		Connection conn = ds.getConnection();
		try {
			for (String machineCode : visterCount.keySet()) {

				Query activityQuery = new Query();
				activityQuery.addCriteria(Criteria.where("machineCode").is(machineCode));
				activityQuery.addCriteria(Criteria.where("date").is(dateTime));
				List<MachineDataCount> machineDataCountList = mongoOperations.find(activityQuery,
						MachineDataCount.class, "MachineDataCount");

				String activityId = null;
				if (machineDataCountList.size() == 1) {
					activityId = machineDataCountList.get(0).getActivityId();
				} else if (machineDataCountList.size() > 1) {

					List<String> ids = new LinkedList<String>();

					for (MachineDataCount item : machineDataCountList) {
						ids.add(item.getActivityId());
					}

					Date startTimeDate = formatStartTime.parse(startTime);
					List<String> activityIds = queryVistorMachinePlanInfo(conn, ids, startTimeDate);
					if (!activityIds.isEmpty()) {
						activityId = activityIds.get(0);
					}
				}

				Inno72MachineVistorCountHour insertItem = new Inno72MachineVistorCountHour();
				insertItem.setActivityId(activityId);
				insertItem.setTime(startTime);
				insertItem.setMachineCode(machineCode);
				insertItem.setVistor(visterCount.get(machineCode).intValue());
				vistorInfos.add(insertItem);
			}
		} finally {
			if (conn != null) conn.close();
		}

		if (!vistorInfos.isEmpty()) {
			insertMachineInfos(vistorInfos);
		}

		return new ReturnT<String>(ReturnT.SUCCESS_CODE, startTime + " 处理" + pointLogs.size() + "条pointLogs "
				+ vistorInfos.size() + "条vistorInfos 删除:" + deleteNum + "条");

	}


	private String nulltoEmpty(String item) {
		return item == null ? "" : item;
	}

	private Number nulltoZero(Number item) {
		return item == null ? 0 : item;
	}

	private void insertMachineInfos(List<Inno72MachineVistorCountHour> infos) throws SQLException {


		Connection conn = null;
		PreparedStatement stm = null;

		try {

			StringBuilder insertSql = new StringBuilder(
					"insert into inno72.inno72_machine_info_log_hour(type, activity_id, machine_code, seller_id, goods_id, num, time) values");

			for (int i = 1; i <= infos.size(); i++) {
				if (i == infos.size()) {
					insertSql.append(String.format(("(1, '%s','%s','', '', %d,'%s')"),
							nulltoEmpty(infos.get(i - 1).getActivityId()),
							nulltoEmpty(infos.get(i - 1).getMachineCode()), nulltoZero(infos.get(i - 1).getVistor()),
							nulltoEmpty(infos.get(i - 1).getTime())));
				} else {
					insertSql.append(String.format(("(1, '%s','%s','', '', %d,'%s'),"),
							nulltoEmpty(infos.get(i - 1).getActivityId()),
							nulltoEmpty(infos.get(i - 1).getMachineCode()), nulltoZero(infos.get(i - 1).getVistor()),
							nulltoEmpty(infos.get(i - 1).getTime())));
				}

			}


			conn = ds.getConnection();
			stm = conn.prepareStatement(insertSql.toString());
			stm.executeUpdate();

		} finally {
			if (stm != null) stm.close();
			if (conn != null) conn.close();
		}


	}


	private List<String> queryVistorMachinePlanInfo(Connection conn, List<String> ids, Date startTimeDate)
			throws SQLException {

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			StringBuilder sqlSb = new StringBuilder(
					"select a.id from inno72.inno72_activity a left join inno72.inno72_activity_plan p on a.id = p.activity_id "
							+ " where ? between p.start_time and p.end_time and ");

			if (ids.size() > 1) {
				sqlSb.append("a.id in (");
				int size = ids.size();
				for (int i = 1; i <= ids.size(); i++) {
					if (i == size) {
						sqlSb.append(" ?)");
					} else {
						sqlSb.append(" ?, ");
					}
				}
			} else {
				sqlSb.append(" a.id = ?");
			}

			stm = conn.prepareStatement(sqlSb.toString());
			int index = 1;
			stm.setTimestamp(index++, new java.sql.Timestamp(startTimeDate.getTime()));
			for (String id : ids) {
				stm.setString(index++, id);
			}

			rs = stm.executeQuery();

			List<String> idList = new LinkedList<String>();
			while (rs.next()) {
				idList.add(rs.getString(1));
			}

			return idList;

		} finally {
			if (rs != null) rs.close();
			if (stm != null) stm.close();
		}
	}

	private boolean queryVistorInfo(String startTime) throws SQLException {

		Connection conn = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			conn = ds.getConnection();
			stm = conn.prepareStatement("select * from inno72.inno72_machine_info_log_hour where type=1 and time=?");
			stm.setString(1, startTime);
			rs = stm.executeQuery();
			return rs.next();
		} finally {
			if (rs != null) rs.close();
			if (stm != null) stm.close();
			if (conn != null) conn.close();
		}
	}

	private int deleteMachineInfos(String startTime) throws SQLException {

		Connection conn = null;
		PreparedStatement stm = null;

		try {
			conn = ds.getConnection();
			stm = conn.prepareStatement("delete from inno72.inno72_machine_info_log_hour where type=1 and time=?");
			stm.setString(1, startTime);
			return stm.executeUpdate();
		} finally {
			if (stm != null) stm.close();
			if (conn != null) conn.close();
		}
	}

	private static int daysBetween(Date date1, Date date2) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		long time1 = cal.getTimeInMillis();
		cal.setTime(date2);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}


}

