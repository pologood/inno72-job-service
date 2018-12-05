package com.inno72.job.executer.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Mapper;

import com.inno72.job.executer.model.Inno72MerchantTotalCount;
import com.inno72.job.executer.model.Inno72MerchantTotalCountByDay;
import com.inno72.job.executer.vo.FlowmeterVo;
import com.inno72.job.executer.vo.OrderVo;

@Mapper
public interface Inno72MerchantTotalCountByDayMapper {
	public String getLastTime();

	void insert(Inno72MerchantTotalCountByDay day);

	List<Inno72MerchantTotalCountByDay> getYestodayData(String subDate);

	List<Inno72MerchantTotalCountByDay> selectByLastTimeAndDayParam(Map<String, String> selectByDayParam);

	List<Inno72MerchantTotalCountByDay> selectByNotInCount(List<String> ids);

	List<Inno72MerchantTotalCountByDay> selectAll();

	void deleteByIds(List<String> dayIds);

	List<Inno72MerchantTotalCountByDay> selectByDate(Map<String, String> sParam);

	String selectLastDateFromOrder();

	String selectLastDateFromLife();

	void insertS(List<Inno72MerchantTotalCountByDay> countByDays);

	List<OrderVo> selectOrderByDate(Map<String, String> params);

	List<FlowmeterVo> selectPv(Map<String, String> params);

	List<FlowmeterVo> selectUv(Map<String, String> params);

	List<FlowmeterVo> selectConcernNum(Map<String, String> params);

	List<FlowmeterVo> selectStay(Map<String, String> params);
}