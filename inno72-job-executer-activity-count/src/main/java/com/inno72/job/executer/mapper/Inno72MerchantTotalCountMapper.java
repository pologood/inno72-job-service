package com.inno72.job.executer.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.inno72.job.executer.model.Inno72MerchantTotalCount;

@Mapper
public interface Inno72MerchantTotalCountMapper{
	List<Inno72MerchantTotalCount> selectByMerchantId(String machantId);
	List<Map<String, String>> selectActivityAndCity(String date);

	int insertS(List<Inno72MerchantTotalCount> list);

	Inno72MerchantTotalCount getTotolCount(@Param("activityId") String activityId, @Param("channelMerchantId") String channelMerchantId);

	Integer getActivityStatus(@Param("activityId")String activityId, @Param("subDate")String subDate);

	Integer getMachineNum(String activityId);

	Integer getVisitorNumFromHourLog(String activityId);

	int deleteByIdS(List<String> ids);

	List<Inno72MerchantTotalCount> selectByIds(List<String> ids);
}