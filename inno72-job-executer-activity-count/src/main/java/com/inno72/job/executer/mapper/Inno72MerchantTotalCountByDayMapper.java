package com.inno72.job.executer.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.inno72.job.executer.model.Inno72MerchantTotalCountByDay;

@Mapper
public interface Inno72MerchantTotalCountByDayMapper {
	public String getLastTime();

	void insert(Inno72MerchantTotalCountByDay day);

	List<Inno72MerchantTotalCountByDay> getYestodayData(String subDate);
}