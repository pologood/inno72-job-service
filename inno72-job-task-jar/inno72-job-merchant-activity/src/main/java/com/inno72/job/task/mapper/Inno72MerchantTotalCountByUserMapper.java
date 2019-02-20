package com.inno72.job.task.mapper;

import java.util.List;

import com.inno72.job.task.model.Inno72MerchantTotalCountByUser;

public interface Inno72MerchantTotalCountByUserMapper{
	Inno72MerchantTotalCountByUser selectLastTime();

	void insertS(List<Inno72MerchantTotalCountByUser> list);
}