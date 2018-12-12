package com.inno72.job.task.mapper;

import com.inno72.job.task.model.Inno72GameUserLogin;

import java.util.List;

public interface Inno72GameUserLoginMapper{

	/**
	 * 查找没有处理过的url
	 * @return
	 */
	List<Inno72GameUserLogin> findAllNoProcessed();

	void update(Inno72GameUserLogin inno72GameUserLogin);

}