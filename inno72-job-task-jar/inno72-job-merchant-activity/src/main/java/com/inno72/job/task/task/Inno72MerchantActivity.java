package com.inno72.job.task.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.inno72.job.task.mapper.Inno72MerchantTotalCountMapper;
import com.inno72.job.task.mapper.Inno72MerchantUserMapper;
import com.inno72.job.task.model.Inno72MerchantTotalCount;
import com.inno72.job.task.model.Inno72MerchantUser;
import com.inno72.job.task.vo.TimeVo;


@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.Inno72MerchantActivity")
public class Inno72MerchantActivity implements IJobHandler {

	@Resource
	private Inno72MerchantTotalCountByDayMapper inno72MerchantTotalCountByDayMapper;
	@Resource
	private Inno72ActivityMapper inno72ActivityMapper;
	@Resource
	private Inno72MerchantTotalCountMapper inno72MerchantTotalCountMapper;
	@Resource
	private Inno72MerchantUserMapper inno72MerchantUserMapper;


	@Override
	public ReturnT<String> execute(String param) throws Exception {

		JobLogger.log("统计商户没有数据的活动");

		List<Inno72MerchantUser> inno72MerchantUsers = inno72MerchantUserMapper.selectAll();

		JobLogger.log("统计商户没有数据的活动 - 已存在上商户 " + JSON.toJSONString(inno72MerchantUsers));

		if (inno72MerchantUsers.size() > 0){
			LocalDateTime now = LocalDateTime.now();
			for (Inno72MerchantUser indexUser : inno72MerchantUsers){
				String merchantId = indexUser.getMerchantId();
				if (StringUtils.isNotEmpty(merchantId)){

					List<String> actIds = inno72ActivityMapper.merchantActivityIds(merchantId);
					if (actIds .size() == 0){
						JobLogger.log("商户【"+merchantId+"】没有活动配置");
						continue;
					}
					JobLogger.log("商户【"+merchantId+"】所有活动 "+ JSON.toJSONString(actIds));
					List<String> inno72MerchantTotalCountsIds = inno72MerchantTotalCountMapper
							.selectActIdByMerchantId(merchantId);
					JobLogger.log("商户【"+merchantId+"】已有数据活动 " + JSON.toJSONString(inno72MerchantTotalCountsIds));
					actIds.removeAll(inno72MerchantTotalCountsIds);
					JobLogger.log("商户【"+merchantId+"】去重后需要调整的活动 【"+JSON.toJSONString(actIds)+"】");
					if (actIds.size() > 0) {

						List<Inno72MerchantTotalCount> newCount = inno72MerchantTotalCountMapper.selectNewCount(actIds);

						for (Inno72MerchantTotalCount count : newCount) {

							count.setBuyer(0);
							count.setId(UUID.randomUUID().toString().replaceAll("-", ""));
							count.setLastUpdateTime(now);
							count.setMerchantId(merchantId);
							count.setOrder(0);
							count.setPv(0);
							count.setShipment(0);
							count.setStayUser(0);
							count.setUv(0);

							Map<String, String> selectByDayParam = new HashMap<>(2);
							selectByDayParam.put("merchantId", merchantId);
							selectByDayParam.put("activityId", count.getActivityId());

							Integer machineNum;
							//当 count 不存在时 初始化一个count 需计算 机器数量 和 活动状态
							machineNum = inno72MerchantTotalCountMapper.getMachineNumByInteract(selectByDayParam);
							Integer visitorNum = inno72MerchantTotalCountMapper.getVisitorNumFromHourLog(selectByDayParam);
							// 活动状态

							TimeVo timeVo = inno72MerchantTotalCountMapper.selectMaxMinTime(selectByDayParam);

							String actS = "1";
							if (timeVo != null){
								if(timeVo.getStartTime().isAfter(now)){
									actS = "2";
								}else if (timeVo.getEndTime().isBefore(now)){
									actS = "0";
								}
							}
							count.setActivityStatus(actS);
							count.setActivityType("2");
							count.setMachineNum(machineNum);
							count.setVisitorNum(visitorNum);

						}

						int i = inno72MerchantTotalCountMapper.insertS(newCount);
						JobLogger.log("插入初始化商户统计【"+JSON.toJSONString(newCount)+"】 {}"+ i);
					}
				}
			}
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
