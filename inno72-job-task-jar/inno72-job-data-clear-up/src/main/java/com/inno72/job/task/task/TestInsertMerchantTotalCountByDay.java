package com.inno72.job.task.task;


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
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.task.mapper.Inno72GameUserLoginMapper;
import com.inno72.job.task.mapper.Inno72MerchantTotalCountByDayMapper;
import com.inno72.job.task.mapper.Inno72MerchantTotalCountByUserMapper;
import com.inno72.job.task.model.Inno72MerchantTotalCountByDay;
import com.inno72.job.task.model.Inno72MerchantTotalCountByUser;
import com.inno72.job.task.util.Uuid;
import com.inno72.mongo.MongoUtil;

@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.TestInsertMerchantTotalCountByDay")
public class TestInsertMerchantTotalCountByDay implements IJobHandler {

	@Resource
	private Inno72MerchantTotalCountByDayMapper inno72MerchantTotalCountByDayMapper;

	@Override
	public ReturnT<String> execute(String param) throws Exception {

		if (LocalDateTime.now().isAfter(LocalDateTime.parse("2018-12-31 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))){
			new ReturnT<>(ReturnT.SUCCESS_CODE, "活动完成了!");
		}

		String activityId = "03e0c821671a4d6f8fad0d47fa25f040";
		String activityName = "点七二互动活动";
		String goodsId = "b49871564eb143429f7ecc97c15f3aea";
		String goodsName = "100-5元优惠券";
		String merchantId = "201812210001";
		String sellerId = "11001";
		String nowDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//		String now = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		int couponbj = rdn(1350, 100);
		int couponsh = rdn(1900, 1200);
		int couponhz = rdn(2000, 1300);

		int totalOrder = couponbj + couponhz + couponsh;

		int pvbj = rdn(2000, 1500);
		int pvsh = rdn(2300, 2000);
		int pvhz = rdn(2300, 1900);

		int uvbj = rdn(1500, 1100);
		int uvsh = rdn(2000, 1300);
		int uvhz = rdn(2200, 1500);

		/**
		 * @param id
		 * @param date
		 * @param city
		 *
		 * @param goodsId
		 * @param goodsName
		 * @param activityId
		 *
		 * @param activityName
		 * @param stayNum
		 * @param merchantId
		 *
		 * @param orderQtyTotal
		 * @param orderQtySucc
		 * @param goodsNum
		 *
		 * @param couponNum
		 * @param concernNum
		 * @param pv
		 *
		 * @param uv
		 * @param sellerId
		 * @param lastUpdateTime
		 */

		Inno72MerchantTotalCountByDay daybj = new Inno72MerchantTotalCountByDay(
				Uuid.genUuid(), nowDate, "北京市",
				goodsId, goodsName, activityId,
				activityName,0, merchantId,
				totalOrder, totalOrder, 0,
				couponbj,	0, pvbj,
				uvbj, sellerId,	LocalDateTime.now()
		);

		Inno72MerchantTotalCountByDay daysh = new Inno72MerchantTotalCountByDay(
				Uuid.genUuid(), nowDate, "上海市", goodsId,
				goodsName, activityId, activityName,
				0, merchantId, totalOrder,
				totalOrder, 0, couponsh,
				0, pvsh, uvsh, sellerId,
				LocalDateTime.now()
		);
		Inno72MerchantTotalCountByDay dayhz = new Inno72MerchantTotalCountByDay(
				Uuid.genUuid(), nowDate, "杭州市", goodsId,
				goodsName, activityId, activityName,
				0, merchantId, totalOrder,
				totalOrder, 0, couponhz,
				0, pvhz, uvhz, sellerId,
				LocalDateTime.now()
		);
		List<Inno72MerchantTotalCountByDay> list = new ArrayList<>();
		list.add(daybj);
		list.add(daysh);
		list.add(dayhz);

		JobLogger.log("执行线程 - 插入商品每日随机数据 " + JSON.toJSONString(list));
		int insert = inno72MerchantTotalCountByDayMapper.insertS(list);


		if (nowDate.equals("2018-12-31")){
			inno72MerchantTotalCountByDayMapper.updateCountStop();
		}



		return new ReturnT<>(ReturnT.SUCCESS_CODE, "N B");
	}

	private static int rdn(int max , int min) {
		return new Random().nextInt(max)%(max-min+1) + min;
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
