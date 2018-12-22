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
import com.inno72.mongo.MongoUtil;

@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.TestInsertLoginTask")
public class TestInsertLoginTask implements IJobHandler {

	@Resource
	private Inno72GameUserLoginMapper inno72GameUserLoginMapper;
	@Resource
	private Inno72MerchantTotalCountByUserMapper inno72MerchantTotalCountByUserMapper;

	@Resource
	private MongoUtil mongoUtil;

	@Override
	public ReturnT<String> execute(String param) throws Exception {

		List<String> userIds = new ArrayList<>();
		while (userIds.size()< 10000){
			userIds.add(Uuid.genUuid());
		}

		List<String> cityS = new ArrayList<>();

		cityS.add("杭州市");
//		cityS.add("广州市");
		cityS.add("北京市");
//		cityS.add("成都市");
//		cityS.add("深圳市");
//		cityS.add("南京市");
		cityS.add("上海市");
//		cityS.add("重庆市");
//		cityS.add("武汉市");

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
//		dates.add("KTV");
		dates.add("学校");
		dates.add("商场");
		dates.add("影院");
//		dates.add("办公楼");
//		dates.add("公园");
//		dates.add("社区");
//		dates.add("餐厅");


		List<Inno72MerchantTotalCountByUser> user = new ArrayList<>();

		for (String userId : userIds){
			Inno72MerchantTotalCountByUser byUser = new Inno72MerchantTotalCountByUser();
			byUser.setId(Uuid.genUuid());
			byUser.setActivityId("03e0c821671a4d6f8fad0d47fa25f040");
			byUser.setActivityName("点七二互动活动");
			byUser.setDate(genDate());
			byUser.setUserId(userId);
			byUser.setAge(new Random().nextInt(61 - 5 + 1) + 5);
			byUser.setSex(getGender());
			byUser.setUserTag(genTag());
			byUser.setCity(getCity());
			byUser.setPointTag(getPoint());
			byUser.setLastUpdateTime(LocalDateTime.now());
//			mongoUtil.save(byUser,"Inno72MerchantTotalCountByUser");
			user.add(byUser);
			if (user.size() >= 10000){
				inno72MerchantTotalCountByUserMapper.insertS(user);
//				mongoUtil.save(user, "TestUser");
				user = new ArrayList<>();
			}
		}
		if (user.size() > 0){
			inno72MerchantTotalCountByUserMapper.insertS(user);
		}


		return new ReturnT<>(ReturnT.SUCCESS_CODE, "N B");
	}



	private String getPoint(){
		int rdn = rdn(1000, 1);
		if (rdn < 278){
			return "影院";
		}else if(rdn < 951){
			return "商场";
		}else{
			return "学校";

		}
	}

	private String getCity(){
		int rdn = rdn(1000, 1);
		if (rdn < 265){
			return "北京";
		}else if(rdn < 622){
			return "上海";
		}else{
			return "杭州";

		}
	}
	private int getAge(){
		int rdn = rdn(1000, 1);
		if (rdn < 15){
			return rdn(10, 7);
		}else if (rdn < 94){
			return rdn(20, 11);
		}else if (rdn < 656){
			return rdn(30, 21);
		}else if (rdn < 931){
			return rdn(40, 31);
		}else if (rdn < 953){
			return rdn(50, 41);
		}else if (rdn < 985){
			return rdn(60, 51);
		}else {
			return rdn(65, 61);
		}
	}
	private String getGender(){
		int rdn = rdn(1000, 1);
		if (rdn < 327){
			return "男";
		}else {
			return "女";
		}
	}

	private static int rdn(int max , int min) {
		return new Random().nextInt(max)%(max-min+1) + min;
	}
	private String genDate(){
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate now = LocalDate.parse("2018-11-15");
		int j = (int)(Math.random()*14)+1;
		return now.plusDays(-j).format(dateTimeFormatter);
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
