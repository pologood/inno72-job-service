package com.inno72.job.activity.report.task;

import com.inno72.job.activity.report.mapper.Inno72StatisticActivityReportMapper;
import com.inno72.job.activity.report.model.Inno72StatisticActivityReport;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobLogger;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JobMapperScanner(value = "classpath*:/com/inno72/job/activity/report/mapper/*.xml", basePackage="com.inno72.job.activity.report.mapper")
@JobHandler("inno72.activity.report.ActivityReportTask")
public class ActivityReportTask implements IJobHandler {

	@Resource
	private Inno72StatisticActivityReportMapper inno72StatisticActivityReportMapper;


	@Override
	public ReturnT<String> execute(String param) throws Exception {
		JobLogger.log("同步活动报表任务开始");

		String startDate = "";
		String endDate = "";

		if (null != param && !param.equals("")) {
			String[] dates = param.split(",");
			startDate = dates[0];
			endDate = dates[1];
		} else {
			Date d = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			startDate = dateFormat.format(new Date(d.getTime() - (long)24 * 60 * 60 * 10000)); // 获取昨天时间
			endDate = startDate;
		}

		JobLogger.log("startDate " + startDate + " endDate " + endDate);

		Map paramsDel = new HashMap<String, String>();
		paramsDel.put("startTime", startDate);
		paramsDel.put("endTime", endDate);

		inno72StatisticActivityReportMapper.delete(paramsDel);

		Map params = new HashMap<String, String>();
		params.put("startTime", startDate + " 00:00:00");
		params.put("endTime", endDate + " 23:59:59");

		List<Inno72StatisticActivityReport> inno72StatisticActivityReports = inno72StatisticActivityReportMapper
				.queryActivityReports(params);

		JobLogger.log("符合条件的活动报表数据 " + inno72StatisticActivityReports.size() + " 条");

		try {
			if (inno72StatisticActivityReports.size() > 0) {
				inno72StatisticActivityReportMapper.insertBatch(inno72StatisticActivityReports);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JobLogger.log("保存活动报表数据报错");
		}

		JobLogger.log("同步活动报表任务结束");
		return  new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");

	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
