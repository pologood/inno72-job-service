package com.inno72.job.task.task;

import java.util.List;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.task.mapper.Inno72MerchantTotalCountByDayMapper;
import com.inno72.job.task.model.Inno72MerchantTotalCountByDay;


@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.Inno72DataClearUpTask")
public class Inno72DataClearUpTask implements IJobHandler {

	@Resource
	private Inno72MerchantTotalCountByDayMapper inno72MerchantTotalCountByDayMapper;


	@Override
	public ReturnT<String> execute(String param) throws Exception {

		// 转换数据 活动ID和活动名称颠倒错误
		List<Inno72MerchantTotalCountByDay> inno72MerchantTotalCountByDays =
				inno72MerchantTotalCountByDayMapper.selectByDayList();
		JobLogger.log("执行线程 - 需要调整的数据 " + JSON.toJSONString(inno72MerchantTotalCountByDays));
		for (Inno72MerchantTotalCountByDay day : inno72MerchantTotalCountByDays){
			String activityId = day.getActivityId();
			String activityName = day.getActivityName();
			day.setActivityId(activityName);
			day.setActivityName(activityId);

			inno72MerchantTotalCountByDayMapper.update(day);
		}

		// 修补活动ID为空情况
		return  new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");

	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
