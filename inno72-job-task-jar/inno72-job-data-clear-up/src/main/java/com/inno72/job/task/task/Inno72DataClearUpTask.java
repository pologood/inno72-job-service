package com.inno72.job.task.task;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.task.mapper.Inno72ActivityMapper;
import com.inno72.job.task.mapper.Inno72MerchantTotalCountByDayMapper;
import com.inno72.job.task.model.Inno72Activity;
import com.inno72.job.task.model.Inno72MerchantTotalCountByDay;


@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.Inno72DataClearUpTask")
public class Inno72DataClearUpTask implements IJobHandler {

	@Resource
	private Inno72MerchantTotalCountByDayMapper inno72MerchantTotalCountByDayMapper;
	@Resource
	private Inno72ActivityMapper inno72ActivityMapper;


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

		inno72MerchantTotalCountByDays = inno72MerchantTotalCountByDayMapper.selectActNameIsEmptyList();
		JobLogger.log("执行线程 - 需要调整活动名称为空或者是ID的数据 " + JSON.toJSONString(inno72MerchantTotalCountByDays));
		Map<String, Inno72Activity> actMap = inno72ActivityMapper.selectMap();

		for (Inno72MerchantTotalCountByDay day : inno72MerchantTotalCountByDays){

			String activityId = day.getActivityId();
			String activityName = day.getActivityName();

			if (StringUtils.isNotEmpty(activityName)){
				Inno72Activity inno72Activity = actMap.get(activityName);
				if ( inno72Activity != null ){
					day.setActivityId(activityName);
					day.setActivityName(inno72Activity.getName());
					inno72MerchantTotalCountByDayMapper.update(day);
				}
			}else {
				if (StringUtils.isNotEmpty(activityId)){
					Inno72Activity inno72Activity = actMap.get(activityId);
					if (inno72Activity != null){
						day.setActivityName(inno72Activity.getName());
						inno72MerchantTotalCountByDayMapper.update(day);
					}
				}
			}

		}

		inno72MerchantTotalCountByDays = inno72MerchantTotalCountByDayMapper.selectActIdIsEmptyList();
		Collection<Inno72Activity> values = actMap.values();
		JobLogger.log("执行线程 - 通过名称找ID数据 " + JSON.toJSONString(inno72MerchantTotalCountByDays));
		for (Inno72MerchantTotalCountByDay day : inno72MerchantTotalCountByDays){

			String activityId = day.getActivityId();
			String activityName = day.getActivityName();
			if (!isChinese(activityName)){
				Inno72Activity activity = actMap.get(activityName);
				if (activity != null){
					String name = activity.getName();

					day.setActivityId(activityName);
					day.setActivityName(name);

					inno72MerchantTotalCountByDayMapper.update(day);
					break;
				}
			}

			if (StringUtils.isNotEmpty(activityName)){
				for (Inno72Activity activity : values){
					String name = activity.getName();
					if (name.equals(activityName)){
						day.setActivityId(activity.getId());
						inno72MerchantTotalCountByDayMapper.update(day);
						break;
					}
				}
			}
		}

		// 修补活动ID为空情况
		return  new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");

	}

	public static boolean isChinese(String string){
		int n = 0;
		for(int i = 0; i < string.length(); i++) {
			n = (int)string.charAt(i);
			if(!(19968 <= n && n <40869)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
