package com.inno72.job.task.mapper;

import java.util.List;

import com.inno72.job.task.model.Inno72MerchantTotalCountByDay;

public interface Inno72MerchantTotalCountByDayMapper {

	List<Inno72MerchantTotalCountByDay> selectByDayList();

	void update(Inno72MerchantTotalCountByDay day);

	List<Inno72MerchantTotalCountByDay> selectActNameIsEmptyList();

	List<Inno72MerchantTotalCountByDay> selectActIdIsEmptyList();

	void delete();

	void deleteCount();

	int insertS(List<Inno72MerchantTotalCountByDay> list);
}