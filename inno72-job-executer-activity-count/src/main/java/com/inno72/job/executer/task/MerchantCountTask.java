package com.inno72.job.executer.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.inno72.common.datetime.LocalDateUtil;
import com.inno72.common.utils.StringUtil;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.executer.mapper.Inno72MerchantTotalCountByDayMapper;
import com.inno72.job.executer.mapper.Inno72MerchantTotalCountMapper;
import com.inno72.job.executer.model.Inno72MerchantTotalCount;
import com.inno72.job.executer.model.Inno72MerchantTotalCountByDay;


@Component
@JobHandler("merchant.MerchantCountTask")
public class MerchantCountTask implements IJobHandler {

	@Resource
	private Inno72MerchantTotalCountByDayMapper inno72MerchantTotalCountByDayMapper;

	@Resource
	private Inno72MerchantTotalCountMapper inno72MerchantTotalCountMapper;

	@Override
	public ReturnT<String> execute(String param) throws Exception {

		String subDate = LocalDateUtil.transfer(LocalDate.now().plusDays(-1), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		List<Inno72MerchantTotalCountByDay> days =  inno72MerchantTotalCountByDayMapper.getYestodayData(subDate);

		JobLogger.log("商户日总计统计任务开始 查询日期"+ subDate +"已获取数据"+JSON.toJSONString(days));
		if (days.size() == 0){
			return  new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
		}

		Map<String, Inno72MerchantTotalCount> countMap = new HashMap<>();
		List<String> ids = new ArrayList<>();
		for (Inno72MerchantTotalCountByDay day : days){
			String activityId = day.getActivityId();
			String activityName = day.getActivityName();

			String sellerId = day.getSellerId();
			Inno72MerchantTotalCount count = countMap.get(activityId);
			if (count == null ){
				count = inno72MerchantTotalCountMapper.getTotolCount(activityId, sellerId);
			}

			Integer machineNum = inno72MerchantTotalCountMapper.getMachineNum(activityId);
			Integer visitorNum = inno72MerchantTotalCountMapper.getVisitorNumFromHourLog(activityId);
			if (count == null){
				Integer i = inno72MerchantTotalCountMapper.getActivityStatus(activityId, subDate);
				count = new Inno72MerchantTotalCount(activityName, activityId, i+"", machineNum,
						visitorNum, day.getStayNum(), day.getPv(), day.getPv(), day.getOrderQtyTotal(), day.getOrderQtySucc(),
						day.getMerchantId(), day.getSellerId(), day.getOrderQtySucc());
			}else {
				count.setBuyer(count.getBuyer() + day.getOrderQtySucc());
				count.setMachineNum(machineNum > count.getMachineNum() ? machineNum  :  count.getMachineNum());
				count.setOrder(count.getOrder() + day.getOrderQtyTotal());
				count.setPv(count.getPv() + day.getPv());
				count.setShipment(count.getShipment() + day.getOrderQtySucc());
				count.setStayUser(count.getStayUser() + day.getStayNum());
				count.setUv(count.getUv() + day.getUv());
				count.setVisitorNum(count.getVisitorNum() + (visitorNum == null ? 0 :visitorNum));
			}
			if (StringUtil.notEmpty(count.getId())){
				ids.add(count.getId());
			}else {
				count.setId(StringUtil.uuid());
			}
			countMap.put(activityId, count);
		}

		if (countMap.size() > 0){
			if (ids.size() > 0){
				List<Inno72MerchantTotalCount> delecount = inno72MerchantTotalCountMapper.selectByIds(ids);
				if(delecount.size() > 0){
					inno72MerchantTotalCountMapper.deleteByIdS(ids);
				}
			}
			List<Inno72MerchantTotalCount> insertS = new ArrayList<>();
			for (Map.Entry<String, Inno72MerchantTotalCount> entry : countMap.entrySet()){
				insertS.add(entry.getValue());
			}

			JobLogger.log("插入数据 "+JSON.toJSONString(insertS));
			inno72MerchantTotalCountMapper.insertS(insertS);

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
