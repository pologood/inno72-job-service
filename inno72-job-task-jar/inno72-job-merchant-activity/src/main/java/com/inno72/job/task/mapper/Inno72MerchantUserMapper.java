package com.inno72.job.task.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.inno72.job.task.model.Inno72MerchantUser;

public interface Inno72MerchantUserMapper{
	Inno72MerchantUser selectByNameAndPwd(Map<String, String> param);

	List<Inno72MerchantUser> selectByMobile(String phone);

	int updatePwdByPhone(Inno72MerchantUser newUser);

	Inno72MerchantUser selectByLoginNameAndPhone(@Param("phone") String phone, @Param("loginName") String loginName);

	Inno72MerchantUser selectByMerchantName(String loginName);

	Inno72MerchantUser selectByMerchantId(String merchantId);

	List<Inno72MerchantUser> selectAll();

}