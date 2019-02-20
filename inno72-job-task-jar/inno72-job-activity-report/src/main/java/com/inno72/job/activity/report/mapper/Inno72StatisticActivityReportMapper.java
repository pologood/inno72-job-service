package com.inno72.job.activity.report.mapper;


import com.inno72.job.activity.report.model.Inno72StatisticActivityReport;

import java.util.List;

public interface Inno72StatisticActivityReportMapper {

	List<Inno72StatisticActivityReport> queryActivityReports();

	void insertBatch(List<Inno72StatisticActivityReport> activityReports);

	void delete();
}