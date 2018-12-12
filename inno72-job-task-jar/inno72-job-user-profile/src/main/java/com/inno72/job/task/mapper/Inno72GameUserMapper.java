package com.inno72.job.task.mapper;

import com.inno72.job.task.model.Inno72GameUser;
import com.inno72.job.task.vo.PaymentUser;

import java.util.List;
import java.util.Set;

public interface Inno72GameUserMapper {

	Inno72GameUser selectByChannelUserKey(String userId);

	/**
	 * 获得购物控用户
	 * @param count
	 * @return
	 */
	Set<String> getShoppingUsers(int count);

	/**
	 * 获得样品控用户
	 * @param count
	 * @return
	 */
	Set<String> getSampleUsers(int count);

}