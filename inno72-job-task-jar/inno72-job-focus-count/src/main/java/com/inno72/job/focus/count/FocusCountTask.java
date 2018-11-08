package com.inno72.job.focus.count;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;

/**
 * Hello world!
 *
 */
@JobHandler("inno72.task.FocusCountTask")
@Component
public class FocusCountTask implements IJobHandler {

	@Resource
	DataSource ds;

	@Resource
	private MongoOperations mongoOperations;

	@Override
	public ReturnT<String> execute(String param) throws Exception {


		DateFormat formatStartTime = new SimpleDateFormat("yyyy-MM-dd");

		long workTime = 0;
		boolean isCover = false;
		if (StringUtils.isNotBlank(param)) {
			String[] params = StringUtils.split(param, ',');
			if (params.length > 1) {
				if ("1".equals(params[1]))
					isCover = true;
			}
			workTime = formatStartTime.parse(params[0]).getTime();
		} else {
			workTime = System.currentTimeMillis() - 1000 * 60 * 60 * 24;
		}

		Date workDate = new Date(workTime);

		DateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd ");
		String time = formatTime.format(workDate);
		String startTime = time + "00:00:00 000";
		String endTime = time + "23:59:59 999";
		String handTime = time + "00:00:00";

		int deleteNum = 0;
		if (isCover) {
			deleteNum = this.deleteMachineInfos(handTime);
		}

		if (queryFocusInfo(handTime)) {
			return new ReturnT<String>(ReturnT.SUCCESS_CODE, time + "已经被处理");
		}

		Query pointLogQuery = new Query();

		Criteria cond = new Criteria();
		cond.andOperator(Criteria.where("serviceTime").gte(startTime), Criteria.where("serviceTime").lte(endTime));
		pointLogQuery.addCriteria(cond);

		List<Inno72MachineInfomation> machineInfomations = mongoOperations
				.find(pointLogQuery, Inno72MachineInfomation.class, "Inno72MachineInformation");

		if (machineInfomations == null || machineInfomations.isEmpty()) {
			return new ReturnT<String>(ReturnT.SUCCESS_CODE, "处理0条pointLogs");
		}

		Map<String, MachineInfoLogModel> statistics = new HashMap<String, MachineInfoLogModel>();

		Map<String, GameAverageTime> averageStatistics = new HashMap<String, GameAverageTime>();

		for (Inno72MachineInfomation info : machineInfomations) {

			if ("002001".equals(info.getType())) { // 关注
				countInfo(handTime, 2, statistics, info);
			} else if ("003001".equals(info.getType())) { // 入会
				countInfo(handTime, 3, statistics, info);
			} else if ("100100".equals(info.getType())) { // 点击商品
				countInfo(handTime, 4, statistics, info);
			}

			//统计平均时常
			countAverageInfo(averageStatistics, info);
		}

		if (statistics.keySet().size() > 0) {
			insertMachineInfos(new ArrayList<MachineInfoLogModel>(statistics.values()));
		}

		if (averageStatistics.keySet().size() > 0) {
			insertAverageInfos(new ArrayList<GameAverageTime>(averageStatistics.values()), handTime);
		}

		return new ReturnT<String>(ReturnT.SUCCESS_CODE,
				handTime + " 处理" + machineInfomations.size() + "条machineInfomations 删除:" + deleteNum + "条");
	}


	private void countAverageInfo(Map<String, GameAverageTime> statistics, Inno72MachineInfomation info) {

		if (StringUtils.isNotEmpty(info.getTraceId()) && StringUtils.isNotEmpty(info.getServiceTime())) {

			if (statistics.containsKey(info.getTraceId())) {

				GameAverageTime model = statistics.get(info.getTraceId());

				if (StringUtils.isNotBlank(info.getActivityId())) {
					model.setActivityId(info.getActivityId());
				}
				if (StringUtils.isNotBlank(info.getMachineCode())) {
					model.setMachineCode(info.getMachineCode());
				}

				if (info.getServiceTime().compareTo(model.getMaxTime()) > 0) {
					model.setMaxTime(info.getServiceTime());
				}

				if (info.getServiceTime().compareTo(model.getMinTime()) < 0) {
					model.setMinTime(info.getServiceTime());
				}

				statistics.put(info.getTraceId(), model);
			} else {
				GameAverageTime model = new GameAverageTime();
				model.setActivityId(info.getActivityId());
				model.setMachineCode(info.getMachineCode());
				model.setTraceId(info.getTraceId());
				model.setMaxTime(info.getServiceTime());
				model.setMinTime(info.getServiceTime());
				statistics.put(info.getTraceId(), model);
			}
		}
	}

	private void countInfo(String startTime, int type, Map<String, MachineInfoLogModel> statistics,
			Inno72MachineInfomation info) {

		MachineInfoLogModel model = new MachineInfoLogModel();
		model.setActivityId(info.getActivityId());
		model.setSellerId(info.getSellerId());
		model.setTime(startTime);
		model.setGoodsId(info.getGoodsId());
		model.setMachineCode(info.getMachineCode());
		model.setType(type);

		if (statistics.containsKey(model.getHashKey())) {
			model = statistics.get(model.getHashKey());
			model.setNum(model.getNum() + 1);
			statistics.put(model.getHashKey(), model);
		} else {
			model.setNum(1);
			statistics.put(model.getHashKey(), model);
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


	private int deleteMachineInfos(String startTime) throws SQLException {

		Connection conn = null;
		PreparedStatement stm = null;

		try {
			conn = ds.getConnection();
			stm = conn.prepareStatement("delete from inno72.inno72_machine_info_log_hour where type!=1 and time=?");
			stm.setString(1, startTime);
			return stm.executeUpdate();
		} finally {
			if (stm != null)
				stm.close();
			if (conn != null)
				conn.close();
		}
	}

	private boolean queryFocusInfo(String startTime) throws SQLException {

		Connection conn = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			conn = ds.getConnection();
			stm = conn.prepareStatement("select * from inno72.inno72_machine_info_log_hour where type!=1 and time=?");
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

	private String nulltoEmpty(String item) {
		return item == null ? "" : item;
	}

	private Number nulltoZero(Number item) {
		return item == null ? 0 : item;
	}


	private void insertAverageInfos(List<GameAverageTime> infos, String startTime) throws SQLException, ParseException {

		Connection conn = null;
		PreparedStatement stm = null;

		try {

			StringBuilder insertSql = new StringBuilder(
					"insert into inno72.inno72_machine_info_log_hour(type, activity_id, machine_code, seller_id, goods_id, num, time, reserve1, reserve2, reserve3) values");

			for (int i = 1; i <= infos.size(); i++) {

				DateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");
				Date maxTime = formatTime.parse(infos.get(i - 1).getMaxTime());
				Date minTime = formatTime.parse(infos.get(i - 1).getMinTime());

				if (i == infos.size()) {
					insertSql.append(String.format(("(%d, '%s','%s','', '', %d,'%s','%s','%s','%s')"), 5,
							nulltoEmpty(infos.get(i - 1).getActivityId()),
							nulltoEmpty(infos.get(i - 1).getMachineCode()), maxTime.getTime() - minTime.getTime(),
							startTime, infos.get(i - 1).getMaxTime(), infos.get(i - 1).getMinTime(),
							infos.get(i - 1).getTraceId()));
				} else {
					insertSql.append(String.format(("(%d, '%s','%s','', '', %d,'%s','%s','%s','%s'),"), 5,
							nulltoEmpty(infos.get(i - 1).getActivityId()),
							nulltoEmpty(infos.get(i - 1).getMachineCode()), maxTime.getTime() - minTime.getTime(),
							startTime, infos.get(i - 1).getMaxTime(), infos.get(i - 1).getMinTime(),
							infos.get(i - 1).getTraceId()));
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

	private void insertMachineInfos(List<MachineInfoLogModel> infos) throws SQLException {


		Connection conn = null;
		PreparedStatement stm = null;

		try {

			StringBuilder insertSql = new StringBuilder(
					"insert into inno72.inno72_machine_info_log_hour(type, activity_id, machine_code, seller_id, goods_id, num, time) values");

			for (int i = 1; i <= infos.size(); i++) {


				if (i == infos.size()) {
					insertSql.append(String.format(("(%d, '%s','%s','%s', '%s', %d,'%s')"), infos.get(i - 1).getType(),
							nulltoEmpty(infos.get(i - 1).getActivityId()),
							nulltoEmpty(infos.get(i - 1).getMachineCode()), nulltoEmpty(infos.get(i - 1).getSellerId()),
							nulltoEmpty(infos.get(i - 1).getGoodsId()), nulltoZero(infos.get(i - 1).getNum()),
							nulltoEmpty(infos.get(i - 1).getTime())));
				} else {
					insertSql.append(String.format(("(%d, '%s','%s','%s', '%s', %d,'%s'),"), infos.get(i - 1).getType(),
							nulltoEmpty(infos.get(i - 1).getActivityId()),
							nulltoEmpty(infos.get(i - 1).getMachineCode()), nulltoEmpty(infos.get(i - 1).getSellerId()),
							nulltoEmpty(infos.get(i - 1).getGoodsId()), nulltoZero(infos.get(i - 1).getNum()),
							nulltoEmpty(infos.get(i - 1).getTime())));
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

}
