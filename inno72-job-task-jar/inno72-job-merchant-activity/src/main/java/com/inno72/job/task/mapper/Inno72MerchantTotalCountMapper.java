package com.inno72.job.task.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.inno72.job.task.model.Inno72MerchantTotalCount;


@Mapper
public interface Inno72MerchantTotalCountMapper{

	int insertS(List<Inno72MerchantTotalCount> list);

	Integer getActivityStatus(@Param("activityId") String activityId, @Param("subDate") String subDate);

	Integer getMachineNum(Map<String, String> param);

	Integer getVisitorNumFromHourLog(Map<String, String> param);

	List<Inno72MerchantTotalCount> selectByIds(List<String> ids);

	List<Inno72MerchantTotalCount> selectAll();

	Integer selectActivityById(@Param("activityId") String activityId);

	Integer getActivityStatusFromInteract(@Param("activityId") String activityId,
			@Param("merchantId") String merchantId);

	Integer getMachineNumByInteract(Map<String, String> selectByDayParam);

	String findMerchantBySellerId(String sellerId);

	Inno72MerchantTotalCount selectByMerIdAndActId(Map<String, String> totalParam);

	List<String> selectActIdByMerchantId(String merchantId);

	List<Inno72MerchantTotalCount> selectNewCount(List<String> list);
}