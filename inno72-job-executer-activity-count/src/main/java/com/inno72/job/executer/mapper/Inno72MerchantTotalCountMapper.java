package com.inno72.job.executer.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.inno72.job.executer.model.Inno72MerchantTotalCount;

@Mapper
public interface Inno72MerchantTotalCountMapper{

	List<Inno72MerchantTotalCount> selectByMerchantId(String merchantId);

	List<Map<String, String>> selectActivityAndCity(String date);

	int insertS(List<Inno72MerchantTotalCount> list);

	Inno72MerchantTotalCount getTotolCount(@Param("activityId") String activityId, @Param("merchantId") String merchantId);

	Integer getActivityStatus(@Param("activityId")String activityId, @Param("subDate")String subDate);

	Integer getMachineNum(Map<String, String> param);

	Integer getVisitorNumFromHourLog(Map<String, String> param);

	int deleteByIdS(List<String> ids);

	List<Inno72MerchantTotalCount> selectByIds(List<String> ids);

	List<Inno72MerchantTotalCount> selectAll();

	Integer selectActivityById(@Param("activityId") String activityId);

	Integer getActivityStatusFromInteract(@Param("activityId") String activityId, @Param("merchantId") String merchantId);

	Integer getMachineNumByInteract(Map<String, String> selectByDayParam);

	String findMerchantBySellerId(String sellerId);

	int update(Inno72MerchantTotalCount count);

	String selectMaxUpdateTime();

	Inno72MerchantTotalCount selectByMerIdAndActId(Map<String, String> totalParam);
}