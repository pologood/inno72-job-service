package com.inno72.job.task.task;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.task.model.Inno72MachineInfomation;
import com.inno72.job.task.vo.ActivityPointCount;

@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.ActivityPointCountTask")
public class ActivityPointCountTask implements IJobHandler {

	@Resource
	private MongoOperations mongoOperations;

	@Override
	public ReturnT<String> execute(String param) throws Exception {

		String startTime = getStartTime();

		LocalDateTime now = LocalDateTime.now();
		String endTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		Query pointLogQuery = new Query();

		Criteria cond = new Criteria();
		cond.andOperator(Criteria.where("serviceTime").gte(startTime), Criteria.where("serviceTime").lte(endTime));
		pointLogQuery.addCriteria(cond);

		int perPage = 10000;
		long total = mongoOperations.count(pointLogQuery, "Inno72MachineInformation");

		long pageSize = total/perPage + (total%perPage != 0 ? 1 : 0);


		pointLogQuery.with(new Sort(Sort.Direction.ASC, "_id"));
		pointLogQuery.limit(perPage);

		Map<String, ActivityPointCount> pointCountMap = new HashMap<>();

		Set<String> optionsActivityIds = new HashSet<>();

		for(int i = 0; i < pageSize; i++ ) {

			JobLogger.log("FocusCountTask  job, total:"+total + " limit:" + perPage + " current:" + (i+1));

			pointLogQuery.skip(i*perPage);

			List<Inno72MachineInfomation> machineInfomations = mongoOperations.find(pointLogQuery,
					Inno72MachineInfomation.class, "Inno72MachineInformation");

			if (machineInfomations == null || machineInfomations.isEmpty()) {
				return new ReturnT<String>(ReturnT.SUCCESS_CODE, "处理0条pointLogs");
			}

			for (Inno72MachineInfomation info : machineInfomations) {

				String activityId = info.getActivityId();
				String type = info.getType();


				if (StringUtils.isEmpty(activityId) || StringUtils.isEmpty(type)) {
					continue;
				}

				optionsActivityIds.add(activityId);

				ActivityPointCount activityPointCount = pointCountMap.get(activityId);

				if (activityPointCount == null){

					Query findOne_query = new Query();
					findOne_query.addCriteria(Criteria.where("activityId").is(activityId));

					activityPointCount = mongoOperations
							.findOne(findOne_query, ActivityPointCount.class, "ActivityPointCount");

					if (activityPointCount == null){

						activityPointCount = new ActivityPointCount();
						activityPointCount.setActivityId(activityId);

					}

				}
				//设置更新时间
				activityPointCount.setLastUpdateTime(endTime);

				Map<String, Integer> pointCount = activityPointCount.getPointCount();
				if (pointCount == null){
					pointCount = new HashMap<>();
				}

				Integer integer = pointCount.get(type);
				if (integer == null){
					integer = 0;
				}
				integer += 1;
				pointCount.put(type, integer);

				activityPointCount.setPointCount(pointCount);

				pointCountMap.put(activityId, activityPointCount);

			}

		}

		if ( optionsActivityIds.size() == 0 ){
			return  new ReturnT<>(ReturnT.SUCCESS_CODE, "无处理的活动!");
		}

		Query remove_query = new Query();
		remove_query.addCriteria(Criteria.where("activityId").in(optionsActivityIds));
		mongoOperations.remove(remove_query, ActivityPointCount.class, "ActivityPointCount");

		mongoOperations.insert(pointCountMap.values(), ActivityPointCount.class);

		return new ReturnT<String>(ReturnT.SUCCESS_CODE, "");
	}

	private String getStartTime(){
		Query query = new Query();
		query.with(new Sort(Sort.Direction.DESC, "lastUpdateTime"));
		ActivityPointCount activityPointCount = mongoOperations
				.findOne(query, ActivityPointCount.class, "ActivityPointCount");
		if (activityPointCount == null){
			return "2018-06-20 12:00:00";
		}
		return activityPointCount.getLastUpdateTime();
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
