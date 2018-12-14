package com.inno72.job.task.task;


import static java.lang.Math.random;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.task.mapper.Inno72GameUserLoginMapper;
import com.inno72.job.task.mapper.Inno72MerchantTotalCountByUserMapper;
import com.inno72.job.task.model.Inno72MerchantTotalCountByUser;
import com.inno72.job.task.util.Uuid;

@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.TestInsertLoginTask")
public class TestInsertLoginTask implements IJobHandler {

	@Resource
	private Inno72GameUserLoginMapper inno72GameUserLoginMapper;
	@Resource
	private Inno72MerchantTotalCountByUserMapper inno72MerchantTotalCountByUserMapper;

	@Override
	public ReturnT<String> execute(String param) throws Exception {

		List<String> userIds = inno72GameUserLoginMapper.selectAllUseId();

		List<String> cityS = inno72GameUserLoginMapper.selectAllCity();
		/**
		 * @param id
		 * @param activityId
		 * @param activityName
		 * @param merchantId
		 * @param date
		 * @param userId
		 * @param age
		 * @param sex
		 * @param userTag
		 * @param pointTag
		 * @param city
		 * @param lastUpdateTime
		 */

		List<String> dates = new ArrayList<>();
		dates.add("KTV");
		dates.add("学校");
		dates.add("商场");
		dates.add("办公楼");
		dates.add("公园");
		dates.add("社区");
		dates.add("餐厅");

		List<Inno72MerchantTotalCountByUser> user = new ArrayList<>();

		for (String userId : userIds){
			Inno72MerchantTotalCountByUser byUser = new Inno72MerchantTotalCountByUser();
			byUser.setId(Uuid.genUuid());
			byUser.setActivityId("bc0dbe5ce64d45d78ab5605555bec58d");
			byUser.setActivityName("自动化互派派样活动");
			byUser.setDate(genDate());
			byUser.setUserId(userId);
			byUser.setAge(new Random().nextInt(61 - 5 + 1) + 5);
			byUser.setSex((new Random().nextInt(2 - 1 + 1) + 1)==1 ? "男":"女");
			byUser.setUserTag(genTag());
			byUser.setCity(cityS.get((int)(Math.random() * cityS.size())));
			byUser.setPointTag(dates.get((int)(Math.random() * dates.size())));
			byUser.setLastUpdateTime(LocalDateTime.now());
			user.add(byUser);
		}
		inno72MerchantTotalCountByUserMapper.insertS(user);

		return new ReturnT<>(ReturnT.SUCCESS_CODE, "N B");
	}

	private String genDate(){
		List<String> dates = new ArrayList<>();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate now = LocalDate.now();
		dates.add(now.format(dateTimeFormatter));
		int j = (int) (random() * 10);
		for (int i = 0; i < 10; i++) {
			now = now.plusDays(-1);
			if (j == i){
				return now.format(dateTimeFormatter);
			}
		}
		return now.format(dateTimeFormatter);
	}

	private String genTag(){
		List<String> dates = new ArrayList<>();
		dates.add("尝鲜族");
		dates.add("购物控");
		dates.add("样品控");
		dates.add("互动控");
		dates.add("分享族");
		dates.add("高消费控");
		dates.add("普通消费控");
		dates.add("游戏达人");

		int s = (int)(Math.random()* dates.size()) + 1;

		Set<String> tags = new HashSet<>();

		while (tags.size() < s){
			int s1 =  (int)(Math.random()*dates.size());
			tags.add(dates.get(s1));
		}

		return JSON.toJSONString(tags);
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
