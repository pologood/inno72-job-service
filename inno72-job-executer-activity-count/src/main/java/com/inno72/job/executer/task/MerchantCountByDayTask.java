package com.inno72.job.executer.task;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.inno72.common.utils.StringUtil;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.executer.mapper.Inno72MerchantTotalCountByDayMapper;
import com.inno72.job.executer.mapper.Inno72MerchantTotalCountMapper;
import com.inno72.job.executer.model.Inno72MachineInformation;
import com.inno72.job.executer.model.Inno72MerchantTotalCountByDay;
import com.inno72.redis.IRedisUtil;


@Component
@JobHandler("merchant.MerchantCountByDayTask")
public class MerchantCountByDayTask implements IJobHandler {

	@Resource
	private Inno72MerchantTotalCountMapper inno72MerchantTotalCountMapper;
	@Resource
	private Inno72MerchantTotalCountByDayMapper inno72MerchantTotalCountByDayMapper;
	@Resource
	private IRedisUtil redisUtil;
	@Resource
	private MongoOperations mongoOperations;
	private ExecutorService exec = Executors.newFixedThreadPool(10);
	@Override
	public ReturnT<String> execute(String param) {
		JobLogger.log("商户日统计任务开始");
		String lastActionTime = inno72MerchantTotalCountByDayMapper.getLastTime();
		Query query = new Query();
		if (StringUtils.isEmpty(lastActionTime)){
			JobLogger.log("从埋点中获取初始化的时间");
			Query _query = new Query();
			_query.with(new Sort(new Sort.Order(Sort.Direction.ASC,"serviceTime")));
			_query.limit(1);
			Inno72MachineInformation inno72MachineInformation = mongoOperations
					.findOne(_query, Inno72MachineInformation.class, "Inno72MachineInformation");
			if (inno72MachineInformation == null){
				return  new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
			}
			lastActionTime = inno72MachineInformation.getServiceTime();
		}

		JobLogger.log("商户日统计任务上次执行时间" + lastActionTime);

		List<String> types = new ArrayList<>();
		types.add("100100");//停留用户数 用户体验数
		types.add("002001");//关注数
		types.add("007001");//订单数
		types.add("007002");//订单数
		types.add("008001");//出货数
		types.add("011002");//支付
		types.add("011002 ");//支付
		types.add("001001");//互动次数  去重后是互动人数

		query.addCriteria(Criteria.where("type").in(types));

		query.addCriteria(Criteria.where("serviceTime").gt(lastActionTime));
		query.with(new Sort(new Sort.Order(Sort.Direction.ASC,"serviceTime")));

		List<Inno72MachineInformation> inno72MachineInfomations = mongoOperations
				.find(query, Inno72MachineInformation.class, "Inno72MachineInformation");

		JobLogger.log("商户日统计任务查询到待统计数据" + JSON.toJSONString(inno72MachineInfomations));

		if (inno72MachineInfomations.size() == 0){
			return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
		}

		Map<String, List<Inno72MachineInformation>> group = new HashMap<>();
		//按日期分组
		for (Inno72MachineInformation information : inno72MachineInfomations){

			String activityId = information.getActivityId();
			String city = information.getCity();
			String goodsId = information.getGoodsId();
			String serviceTime = information.getServiceTime();
			if (StringUtil.isEmpty(serviceTime)){
				continue;
			}
			String groupKey = activityId+city+goodsId+serviceTime.substring(0, 10);

			List<Inno72MachineInformation> inno72MachineInformations = group.get(groupKey);
			if (inno72MachineInformations == null){
				inno72MachineInformations = new ArrayList<>();
			}
			inno72MachineInformations.add(information);
			group.put(groupKey,inno72MachineInformations);
		}


		for (Map.Entry<String, List<Inno72MachineInformation>> entry: group.entrySet()){

			List<Inno72MachineInformation> value = entry.getValue();

			exec.execute(() ->{

				System.out.println("当前分组数据 -> " + JSON.toJSONString(value));

				int stay = 0;//停留用户数
				int concern = 0;//关注用户数
				int gorder = 0;//订单数
				int corder = 0;//优惠券订单数
				int goods = 0;//出货数
				int pay = 0;//支付数
				int pv = 0;//pv
				int uv = 0;

				String date = "";
				String city = "";
				String goodsId = "";
				String goodsName = "";
				//inno72_merchant.merchant_code
				String sellerId = "";
				//inno72_merchant_user.id = inno72_merchant.merchant_account_id
				String merchantId = "";

				String activityId = "";
				String activityName = "";

				// 商户总名称 - table -> inno72_merchant_user.merchant_name =  inno72_merchant.merchant_account_name
				String merchantName = "";// TODO 新
				// 渠道商家ID - table -> inno72_merchant.id
				String channelMerchantId = "";//TODO 新
				// 渠道ID - table -> inno72_merchant.channel_id
				String channelId = "";//TODO 新
				// 渠道名称 - table -> inno72_merchant.channel_name
				String channelName = "";//TODO 新

				String machineCode = "";

				Set<String> user = new HashSet<>();

				for (Inno72MachineInformation count : value){

					String type = count.getType();
					if (StringUtil.isEmpty(date)){
						date = count.getServiceTime().substring(0, 10);
					}
					if (StringUtil.isEmpty(city) && StringUtil.notEmpty(count.getCity())) {
						city = count.getCity();
					}
					if (StringUtil.isEmpty(goodsId) && StringUtil.notEmpty(count.getGoodsId())) {
						goodsId = count.getGoodsId();
					}
					if (StringUtil.isEmpty(goodsName) && StringUtil.notEmpty(count.getGoodsName())) {
						goodsName = count.getGoodsName();
					}
					if (StringUtil.isEmpty(sellerId) && StringUtil.notEmpty(count.getSellerId())) {
						sellerId = count.getSellerId();
					}
					if (StringUtil.isEmpty(activityId) && StringUtil.notEmpty(count.getActivityId())) {
						activityId = count.getActivityId();
					}
					if (StringUtil.isEmpty(activityName) && StringUtil.notEmpty(count.getActivityName())) {
						activityName = count.getActivityName();
					}
					//merchantAccountId
					if (StringUtil.isEmpty(machineCode) && StringUtil.notEmpty(count.getMachineCode())) {
						machineCode = count.getMachineCode();
					}
					if (StringUtil.isEmpty(merchantName) && StringUtil.notEmpty(count.getMerchantName())) {
						merchantName = count.getMerchantName();
					}
					if (StringUtil.isEmpty(channelMerchantId) && StringUtil.notEmpty(count.getChannelMerchantId())) {
						channelMerchantId = count.getChannelMerchantId();
					}
					if (StringUtil.isEmpty(channelId) && StringUtil.notEmpty(count.getChannelId())) {
						channelId = count.getChannelId();
					}
					if (StringUtil.isEmpty(channelName) && StringUtil.notEmpty(count.getChannelName())) {
						channelName = count.getChannelName();
					}
					if (StringUtil.isEmpty(merchantId) && StringUtil.notEmpty(sellerId)) {
						merchantId = inno72MerchantTotalCountMapper.findMerchantBySellerId(sellerId);
					}

					switch (type){
						case "100100"://停留用户数
							stay++;
							break;
						case "002001"://关注
							concern++;
							break;
						case "007001"://商品订单
							gorder++;
							break;
						case "007002"://优惠券订单
							corder++;
							break;
						case "008001"://出货
							goods++;
							break;
						case "011002"://订单支付
							pay++;
							break;
						case "011002 "://订单支付
							pay++;
							break;
						case "001001"://登录
							pv++;
							user.add(count.getUserId());
							break;
						default:
							System.out.println("类型不匹配的数据埋点 -> " + JSON.toJSONString(count));
					}

					System.out.println("Thread = "+ Thread.currentThread().getName() + " merchantId = "+ merchantId + " 停留用户数 = " + stay + ";关注 = " + concern
										+ "; 商品订单 = " + gorder + "; 优惠券订单 = " + corder + "; 出货 = " + goods
										+ "; 订单支付 = " + pay + "; pv = " + pv + "; uv = " + user.size() + "; user = " + JSON.toJSONString(user));
				}

				Inno72MerchantTotalCountByDay day = new Inno72MerchantTotalCountByDay(
						StringUtil.uuid(), date, city, goodsId, goodsName,
						merchantId, gorder, pay, goods, corder,
						concern, pv, user.size(), sellerId, LocalDateTime.now(),
						activityId, activityName, stay, merchantName, channelMerchantId,
						channelId, channelName, machineCode);

				inno72MerchantTotalCountByDayMapper.insert(day);

				JobLogger.log("商户日统计任务插入统计数据" + JSON.toJSONString(day));
			});
		}
		return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}

}





















