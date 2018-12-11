package com.inno72.job.executer.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.inno72.job.executer.model.Inno72MerchantTotalCountByUser;

@Mapper
public interface Inno72MerchantTotalCountByUserMapper {
	String selectMaxLastUpdateTime();

	void insertS(List<Inno72MerchantTotalCountByUser> users);
}