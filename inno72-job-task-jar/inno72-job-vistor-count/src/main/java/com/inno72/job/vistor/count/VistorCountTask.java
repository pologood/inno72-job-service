package com.inno72.job.vistor.count;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

		DateFormat formatStartTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		long workTime = 0;
		boolean isCover = false;
		if (StringUtils.isNotBlank(param)) {
			String[] params = StringUtils.split(param, ',');
			if (params.length > 1) {
				if ("1".equals(params[1]))
					isCover = true;
			}
			workTime = formatStartTime.parse(param).getTime();
		} else {
			long currentTime = System.currentTimeMillis();
			workTime = currentTime - 1000 * 60 * 60;
		}

		Date workDate = new Date(workTime);

		DateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH");
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatDate.format(workDate);
		String time = formatTime.format(workDate);
		String startTime = time + ":00:00";
		String endTime = time + ":59:59";

		int deleteNum = 0;
		if (isCover) {
			deleteNum = deleteMachineInfos(startTime);
		}

		if (queryVistorInfo(startTime)) {
			return new ReturnT<String>(ReturnT.SUCCESS_CODE, time + "已经被处理");
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
				activityQuery.addCriteria(Criteria.where("date").is(date));
				List<MachineDataCount> machineDataCountList = mongoOperations
						.find(activityQuery, MachineDataCount.class, "MachineDataCount");

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
			if (conn != null)
				conn.close();
		}

		if (!vistorInfos.isEmpty()) {
			insertMachineInfos(vistorInfos);
		}

		return new ReturnT<String>(ReturnT.SUCCESS_CODE,
				startTime + " 处理" + pointLogs.size() + "条pointLogs " + vistorInfos.size() + "条vistorInfos 删除:"
						+ deleteNum + "条");

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
					insertSql.append(String
							.format(("(1, '%s','%s','', '', %d,'%s')"), nulltoEmpty(infos.get(i - 1).getActivityId()),
									nulltoEmpty(infos.get(i - 1).getMachineCode()),
									nulltoZero(infos.get(i - 1).getVistor()), nulltoEmpty(infos.get(i - 1).getTime())));
				} else {
					insertSql.append(String
							.format(("(1, '%s','%s','', '', %d,'%s'),"), nulltoEmpty(infos.get(i - 1).getActivityId()),
									nulltoEmpty(infos.get(i - 1).getMachineCode()),
									nulltoZero(infos.get(i - 1).getVistor()), nulltoEmpty(infos.get(i - 1).getTime())));
				}

			}


			conn = ds.getConnection();
			stm = conn.prepareStatement(insertSql.toString());
			stm.executeUpdate();

		} finally {
			if (stm != null)
				stm.close();
			if (conn != null)
				conn.close();
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
			if (rs != null)
				rs.close();
			if (stm != null)
				stm.close();
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
			if (rs != null)
				rs.close();
			if (stm != null)
				stm.close();
			if (conn != null)
				conn.close();
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
			if (stm != null)
				stm.close();
			if (conn != null)
				conn.close();
		}
	}


}

