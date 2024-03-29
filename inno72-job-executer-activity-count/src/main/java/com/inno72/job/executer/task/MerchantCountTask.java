package com.inno72.job.executer.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.inno72.common.datetime.LocalDateTimeUtil;
import com.inno72.common.utils.StringUtil;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.executer.mapper.Inno72MerchantTotalCountByDayMapper;
import com.inno72.job.executer.mapper.Inno72MerchantTotalCountMapper;
import com.inno72.job.executer.model.Inno72MerchantTotalCount;
import com.inno72.job.executer.model.Inno72MerchantTotalCountByDay;
import com.inno72.job.executer.vo.TimeVo;


@Component
@JobHandler("merchant.MerchantCountTask")
public class MerchantCountTask implements IJobHandler {

	@Resource
	private Inno72MerchantTotalCountByDayMapper inno72MerchantTotalCountByDayMapper;

	@Resource
	private Inno72MerchantTotalCountMapper inno72MerchantTotalCountMapper;

	@Override
	public ReturnT<String> execute(String param) {

		List<Inno72MerchantTotalCount> counts = inno72MerchantTotalCountMapper.selectAll();
		List<String> ids = new ArrayList<>();

		for (Inno72MerchantTotalCount count : counts){

			String merchantId = count.getMerchantId();
			LocalDateTime lastUpdateTime = count.getLastUpdateTime();
			String activityId = count.getActivityId();

			ids.add(count.getMerchantId());

			if (StringUtil.isEmpty(merchantId) || StringUtil.isEmpty(activityId)){
				continue;
			}

			String time = LocalDateTimeUtil
					.transfer(lastUpdateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			Map<String, String> selectByDayParam = new HashMap<>(2);
			selectByDayParam.put("merchantId", merchantId);
			selectByDayParam.put("activityId", activityId);
			selectByDayParam.put("lastUpdateTime", LocalDateTimeUtil.transfer(lastUpdateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			List<Inno72MerchantTotalCountByDay> days = inno72MerchantTotalCountByDayMapper.selectByLastTimeAndDayParam(selectByDayParam);

			String activityType = count.getActivityType();

			Integer machineNum;

			if (activityType.equals("1")){
				machineNum = inno72MerchantTotalCountMapper.getMachineNum(selectByDayParam);
			}else {
				machineNum = inno72MerchantTotalCountMapper.getMachineNumByInteract(selectByDayParam);
			}


			Integer visitorNum = inno72MerchantTotalCountMapper.getVisitorNumFromHourLog(selectByDayParam);
			count.setLastUpdateTime(LocalDateTime.now());
			count.setVisitorNum(count.getVisitorNum() + (visitorNum == null ? 0 :visitorNum));
			count.setMachineNum(machineNum > count.getMachineNum() ? machineNum  :  count.getMachineNum());
			for (Inno72MerchantTotalCountByDay day : days){
				count.setBuyer(count.getBuyer() + day.getOrderQtySucc());
				count.setOrder(count.getOrder() + day.getOrderQtyTotal());
				count.setShipment(count.getShipment() + day.getOrderQtySucc());
				count.setPv(count.getPv() + day.getPv());
				count.setStayUser(count.getStayUser() + day.getStayNum());
				count.setUv(count.getUv() + day.getUv());
				if (StringUtil.isEmpty(count.getActivityName()) && StringUtil.notEmpty(day.getActivityName())){
					count.setActivityName(day.getActivityName());
				}
				if (StringUtil.isEmpty(count.getMerchantId()) && StringUtil.notEmpty(day.getMerchantId())){
					count.setMerchantId(day.getMerchantId());
				}
			}
			String actS = "1";
			TimeVo timeVo = inno72MerchantTotalCountMapper.selectMaxMinTime(selectByDayParam);
			JobLogger.log("活动 "+ count.getActivityName()+" 状态 ["+ count.getActivityStatus()+", 活动时间 "+ JSON.toJSONString(timeVo));
			if (timeVo != null){
				LocalDateTime now = LocalDateTime.now();

				if(timeVo.getStartTime().isAfter(now)){
					actS = "2";
				}else if (timeVo.getEndTime().isBefore(now)){
					actS = "0";
				}
				if (!count.getActivityStatus().equals(actS)){
					count.setActivityStatus(actS);
				}
			}
			JobLogger.log("更新活动统计为 "+ JSON.toJSONString(count));
			int i = inno72MerchantTotalCountMapper.update(count);
		}
		List<Inno72MerchantTotalCountByDay> days;
		if (ids.size() > 0){
			days = inno72MerchantTotalCountByDayMapper.selectByNotInCount(ids);
		}else {
			days = inno72MerchantTotalCountByDayMapper.selectAll();
		}
		JobLogger.log("商户日总计统计任务开始 查询已获取数据"+JSON.toJSONString(days));


		String subDate = LocalDateTimeUtil.transfer(LocalDateTime.now().plusDays(-1), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		if (days.size() > 0){
			List<Inno72MerchantTotalCount> insertCounts = new ArrayList<>();
			Map<String, Inno72MerchantTotalCount> countMap = new HashMap<>();

			for (Inno72MerchantTotalCountByDay day : days){
				String activityId = day.getActivityId();
				String activityName = day.getActivityName();
				String merchantId = day.getMerchantId();

				String key = merchantId + activityId;
				Inno72MerchantTotalCount count = countMap.get(key);

				Map<String, String> selectByDayParam = new HashMap<>(2);
				selectByDayParam.put("merchantId", merchantId);
				selectByDayParam.put("activityId", activityId);

				//查询是否是常规活动 inno72_activity 否则机器数量和活动状态按照互派查询
				int o = inno72MerchantTotalCountMapper.selectActivityById(activityId);

				Integer machineNum;
				if (count == null){
					//当 count 不存在时 初始化一个count 需计算 机器数量 和 活动状态
					String activityType = "";
					if ( o > 0){
						//机器数量
						activityType = "1";
						machineNum = inno72MerchantTotalCountMapper.getMachineNum(selectByDayParam);
					}else {
						activityType = "2";
						machineNum = inno72MerchantTotalCountMapper.getMachineNumByInteract(selectByDayParam);
					}
					Integer visitorNum = inno72MerchantTotalCountMapper.getVisitorNumFromHourLog(selectByDayParam);
					// 活动状态
					Integer i;
					if ( o > 0){
						i = inno72MerchantTotalCountMapper.getActivityStatus(activityId, subDate);
					}else {
						i = inno72MerchantTotalCountMapper.getActivityStatusFromInteract(activityId, merchantId);
					}

					count = new Inno72MerchantTotalCount(activityName, activityId, i+"", machineNum,
							visitorNum, day.getStayNum(), day.getPv(), day.getPv(), day.getOrderQtyTotal(), day.getOrderQtySucc(),
							day.getMerchantId(), day.getOrderQtySucc(), activityType);
				}else {
					count.setBuyer(count.getBuyer() + day.getOrderQtySucc());
					//初始化已经设置
					//				count.setMachineNum(machineNum > count.getMachineNum() ? machineNum  :  count.getMachineNum());
					count.setOrder(count.getOrder() + day.getOrderQtyTotal());
					count.setShipment(count.getShipment() + day.getOrderQtySucc());
					count.setUv(count.getUv() + day.getUv());
					count.setPv(count.getPv() + day.getPv());
					count.setStayUser(count.getStayUser() + day.getStayNum());
					// 初始化已经设置
					//				count.setVisitorNum(count.getVisitorNum() + (visitorNum == null ? 0 :visitorNum));
					if (StringUtil.isEmpty(count.getMerchantId()) && StringUtil.notEmpty(day.getMerchantId())){
						count.setMerchantId(day.getMerchantId());
					}
				}
				if (StringUtil.notEmpty(count.getId())){
					ids.add(count.getId());
				}else {
					count.setId(StringUtil.uuid());
				}
				count.setLastUpdateTime(LocalDateTime.now());
				countMap.put(key, count);
			}

			for (Map.Entry<String, Inno72MerchantTotalCount> entry : countMap.entrySet()){
				insertCounts.add(entry.getValue());
			}

			JobLogger.log("插入数据 " + JSON.toJSONString(insertCounts));
			if (insertCounts.size() > 0){
				inno72MerchantTotalCountMapper.insertS(insertCounts);
			}
		}
		// 补充新加的活动
		List<Inno72MerchantTotalCountByDay> days1 = inno72MerchantTotalCountByDayMapper.selectNewActivity();
		Map<String, Inno72MerchantTotalCount> newActCount = new HashMap<>();
		for (Inno72MerchantTotalCountByDay day : days1){

			String activityId = day.getActivityId();
			String merchantId = day.getMerchantId();
			String activityName = day.getActivityName();
			String key = merchantId + activityId;
			Inno72MerchantTotalCount count = newActCount.get(key);

			Map<String, String> selectByDayParam = new HashMap<>(2);
			selectByDayParam.put("merchantId", merchantId);
			selectByDayParam.put("activityId", activityId);


			Integer machineNum;
			if (count == null) {
				//当 count 不存在时 初始化一个count 需计算 机器数量 和 活动状态
				String activityType = "";
				//查询是否是常规活动 inno72_activity 否则机器数量和活动状态按照互派查询
				int o = inno72MerchantTotalCountMapper.selectActivityById(activityId);

				if (o > 0) {
					//机器数量
					activityType = "1";
					machineNum = inno72MerchantTotalCountMapper.getMachineNum(selectByDayParam);
				} else {
					activityType = "2";
					machineNum = inno72MerchantTotalCountMapper.getMachineNumByInteract(selectByDayParam);
				}
				Integer visitorNum = inno72MerchantTotalCountMapper.getVisitorNumFromHourLog(selectByDayParam);
				// 活动状态
				Integer i;
				if (o > 0) {
					i = inno72MerchantTotalCountMapper.getActivityStatus(activityId, subDate);
				} else {
					i = inno72MerchantTotalCountMapper.getActivityStatusFromInteract(activityId, merchantId);
				}

				count = new Inno72MerchantTotalCount(activityName, activityId, i + "", machineNum, visitorNum, day.getStayNum(), day.getPv(), day.getPv(), day.getOrderQtyTotal(), day.getOrderQtySucc(),
						day.getMerchantId(), day.getOrderQtySucc(), activityType);
				count.setId(StringUtil.uuid());
			}else{

				count.setBuyer(count.getBuyer() + day.getOrderQtySucc());
				//初始化已经设置
				//				count.setMachineNum(machineNum > count.getMachineNum() ? machineNum  :  count.getMachineNum());
				count.setOrder(count.getOrder() + day.getOrderQtyTotal());
				count.setShipment(count.getShipment() + day.getOrderQtySucc());
				count.setUv(count.getUv() + day.getUv());
				count.setPv(count.getPv() + day.getPv());
				count.setStayUser(count.getStayUser() + day.getStayNum());
				// 初始化已经设置
				//				count.setVisitorNum(count.getVisitorNum() + (visitorNum == null ? 0 :visitorNum));
				if (StringUtil.isEmpty(count.getMerchantId()) && StringUtil.notEmpty(day.getMerchantId())){
					count.setMerchantId(day.getMerchantId());
				}
			}
			newActCount.put(key, count);
		}
		List<Inno72MerchantTotalCount> newActS = new ArrayList<>(newActCount.values());
		if (newActS.size() > 0 ){
			JobLogger.log("插入新增活动数据 " + JSON.toJSONString(newActS));
			inno72MerchantTotalCountMapper.insertS(newActS);
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
