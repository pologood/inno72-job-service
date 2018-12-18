package com.inno72.job.executer.task;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.swing.text.DateFormatter;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.inno72.common.datetime.LocalDateTimeUtil;
import com.inno72.common.datetime.LocalDateUtil;
import com.inno72.common.utils.StringUtil;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.executer.mapper.Inno72MerchantTotalCountByDayMapper;
import com.inno72.job.executer.mapper.Inno72MerchantTotalCountMapper;
import com.inno72.job.executer.model.Inno72MerchantTotalCountByDay;
import com.inno72.job.executer.vo.FlowmeterVo;
import com.inno72.job.executer.vo.OrderVo;
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
	public ReturnT<String> execute(String param) throws Exception {

		String startTime = inno72MerchantTotalCountByDayMapper.getLastTime();
		if (StringUtil.isEmpty(startTime)){
			startTime = inno72MerchantTotalCountByDayMapper.selectLastDateFromLife();
		}
		LocalDateTime startTimeLocal = LocalDateTimeUtil.transfer(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LocalDateTime endTimeLocal = LocalDateTime.now();

		if (startTimeLocal.isAfter(endTimeLocal)){
			return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
		}

		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

		while (true) {

			LocalDateTime plusDays = startTimeLocal.plusDays(1);

			long days = Duration.between(startTimeLocal, endTimeLocal).toDays();
			if (days <= 0) {
				break;
			}
			Map<String, String> params = new HashMap<>();
			params.put("startTime", LocalDateUtil.transfer(startTimeLocal.toLocalDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			params.put("endTime",  LocalDateUtil.transfer(plusDays.toLocalDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			fixedThreadPool.execute(() -> {
				JobLogger.log("执行线程 - 参数 " + JSON.toJSONString(params));
				List<OrderVo> orderVos = inno72MerchantTotalCountByDayMapper.selectOrderByDate(params);
				if (orderVos.size() == 0){
					JobLogger.log("查询参数 - "+ JSON.toJSONString(params) +"结果为空");
					return;
				}

				//查询日期的PV UV
				Map<String, Integer> pvMap = this.buildMap(inno72MerchantTotalCountByDayMapper.selectPv(params));
				Map<String, Integer> uvMap = this.buildMap(inno72MerchantTotalCountByDayMapper.selectUv(params));
				Map<String, Integer> concernVoMap = this.buildMap(inno72MerchantTotalCountByDayMapper.selectConcernNum(params));
				Map<String, Integer> stayVoMap = this.buildMap(inno72MerchantTotalCountByDayMapper.selectStay(params));


				Map<String, List<OrderVo>> dayMap = new HashMap<>();
				List<Inno72MerchantTotalCountByDay> byDays = new ArrayList<>();
				for (OrderVo orderVo : orderVos){

					String activityId = orderVo.getActivityId();
					String goodsId = orderVo.getGoodsId();
					String merchantId = orderVo.getMerchantId();
					String city = orderVo.getCity();
					String date = orderVo.getDate();
					String key = activityId+city+merchantId+goodsId+date;
					List<OrderVo> orderVosValue = dayMap.get(key);
					if (orderVosValue == null){
						orderVosValue = new ArrayList<>();
					}
					orderVosValue.add(orderVo);
					dayMap.put(key, orderVosValue);

				}

				for (Map.Entry<String, List<OrderVo>> entry : dayMap.entrySet()){

					List<OrderVo> value = entry.getValue();
					String key = entry.getKey();

					Inno72MerchantTotalCountByDay day  = new Inno72MerchantTotalCountByDay();

					Integer goodsNum = 0;
					Integer couponNum = 0;
					Integer orderQtyTotal = 0;
					Integer orderQtySucc = 0;

					Integer pv = -1;
					Integer uv = -1;
					Integer concern = -1;//关注数量
					Integer stay = -1; // 停留数


					for (OrderVo orderVo : value) {
						if (StringUtil.isEmpty(day.getId())){
							day.setId(StringUtil.uuid());
						}
						String date = orderVo.getDate();
						if (StringUtil.isEmpty(day.getDate())){
							day.setDate(date);
						}
						String city = orderVo.getCity();
						if (StringUtil.isEmpty(day.getCity())){
							day.setCity(city);
						}
						String goodsId = orderVo.getGoodsId();
						if (StringUtil.isEmpty(day.getGoodsId())){
							day.setGoodsId(goodsId);
						}
						String activityId = orderVo.getActivityId();
						if (StringUtil.isEmpty(day.getActivityId())){
							day.setActivityId(activityId);
						}
						String activityName = orderVo.getActivityName();
						if (StringUtil.isEmpty(day.getActivityName())){
							day.setActivityName(activityName);
						}

						String goodsName = orderVo.getGoodsName();
						if (StringUtil.isEmpty(day.getGoodsName())){
							day.setGoodsName(goodsName);
						}
						String merchantAccountCode = orderVo.getMerchantAccountCode();
						if (StringUtil.isEmpty(day.getMerchantId())){
							day.setMerchantId(merchantAccountCode);
						}
						String sellerId = orderVo.getSellerId();
						if (StringUtil.isEmpty(day.getSellerId())){
							day.setSellerId(sellerId);
						}

						String merchantId = orderVo.getMerchantId();
						String goodsType = orderVo.getGoodsType();
						String payStatus = orderVo.getPayStatus();
						String status = orderVo.getStatus();

						orderQtyTotal += 1;
						if (!goodsType.equals("1")){
							couponNum += 1;
						}
						if (payStatus.equals("1")){
							orderQtySucc += 1;
						}
						if (status.equals("1")){
							goodsNum += 1;
						}

					}
					day.setCouponNum(couponNum);
					day.setOrderQtySucc(orderQtySucc);
					day.setOrderQtyTotal(orderQtyTotal);
					day.setGoodsNum(goodsNum);

					day.setLastUpdateTime(LocalDateTime.now());

					// id , date, city,
					// goodsId, activityId, activityName,
					// goodsName, merchantId, sellerId,
					// orderQtyTotal, orderQtySucc, goodsNum, couponNum，
					// lastUpdateTime,

					// `id`, `date`, `city`,
					// `goods_id`, `activity_id`, `activity_name`,
					// `goods_name`, `merchant_id`, `seller_id`,
					// `order_qty_total`, `order_qty_succ`, `goods_num`, `coupon_num`,
					// `last_update_time`
           		    // `concern_num`, `pv`, `uv`, `stay_num`
					if (pv == -1){
						String date = day.getDate();
						String activityId = day.getActivityId();
						String city = day.getCity();

						String pvKey = date+city+activityId;
						pv = Optional.ofNullable(pvMap.get(pvKey)).orElse(0);
					}
					if (uv == -1){
						String date = day.getDate();
						String activityId = day.getActivityId();
						String city = day.getCity();

						String uvKey = date+city+activityId;
						uv = Optional.ofNullable(uvMap.get(uvKey)).orElse(0);
					}
					if (concern == -1){
						String date = day.getDate();
						String activityId = day.getActivityId();
						String city = day.getCity();

						String concernKey = date+city+activityId;
						concern = Optional.ofNullable(uvMap.get(concernKey)).orElse(0);//关注数
					}
					if (stay == -1){
						String date = day.getDate();
						String activityId = day.getActivityId();
						String city = day.getCity();

						String stayKey = date+city+activityId;
						stay = Optional.ofNullable(uvMap.get(stayKey)).orElse(0);
					}
					day.setPv(pv);
					day.setUv(uv);
					day.setConcernNum(couponNum);
					day.setStayNum(stay);

					byDays.add(day);

				}
				if (byDays.size() > 0){
					inno72MerchantTotalCountByDayMapper.insertS(byDays);
				}

			});
			startTimeLocal = plusDays;
		}
		return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}

	private Map<String, Integer> buildMap(List<FlowmeterVo> orderVos){
		Map<String, Integer> map = new HashMap<>();
		if (orderVos == null || orderVos.size() == 0){
			return map;
		}
		for (FlowmeterVo flowmeterVo : orderVos){
			String activityId = flowmeterVo.getActivityId();
			String city = flowmeterVo.getCity();
			String date = flowmeterVo.getDate();
			Integer flowmeter = flowmeterVo.getFlowmeter();
			String pvKey = date+city+activityId;
			map.put(pvKey, flowmeter);
		}
		return map;
	}

}





















