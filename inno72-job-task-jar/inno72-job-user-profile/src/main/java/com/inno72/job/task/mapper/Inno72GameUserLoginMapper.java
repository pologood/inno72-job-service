package com.inno72.job.task.mapper;

import java.util.List;
import java.util.Map;

import com.inno72.job.task.model.Inno72GameUserLogin;

public interface Inno72GameUserLoginMapper{

	/**
	 * 查找没有处理过的url
	 * @return
	 */
	List<Inno72GameUserLogin> findAllNoProcessed();

	void update(Inno72GameUserLogin inno72GameUserLogin);

	Inno72GameUserLogin findMinTime();

	List<Inno72GameUserLogin> selectByTime(Map<String, String> loginParam);

	List<Inno72GameUserLogin> selectByPhoneModel(Map<String, String> param);
}