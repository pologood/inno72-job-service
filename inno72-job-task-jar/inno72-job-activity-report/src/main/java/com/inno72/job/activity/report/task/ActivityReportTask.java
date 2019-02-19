package com.inno72.job.activity.report.task;

import com.inno72.job.activity.report.mapper.Inno72StatisticActivityReportMapper;
import com.inno72.job.activity.report.model.Inno72StatisticActivityReport;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;

import javax.annotation.Resource;
import java.util.List;

@JobMapperScanner(value = "classpath*:/com/inno72/job/activity/report/mapper/*.xml", basePackage="com.inno72.job.activity.report.mapper")
@JobHandler("inno72.activity.report.ActivityReportTask")
public class ActivityReportTask implements IJobHandler {

	@Resource
	private Inno72StatisticActivityReportMapper inno72StatisticActivityReportMapper;


	@Override
	public ReturnT<String> execute(String param) throws Exception {

		List<Inno72StatisticActivityReport> inno72StatisticActivityReports = inno72StatisticActivityReportMapper
				.queryActivityReports();
		System.out.println(inno72StatisticActivityReports.size());
		try {
			inno72StatisticActivityReportMapper.insertBatch(inno72StatisticActivityReports);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return  new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");

	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
