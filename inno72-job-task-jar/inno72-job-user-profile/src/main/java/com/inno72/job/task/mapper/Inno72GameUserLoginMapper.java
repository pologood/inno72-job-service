package com.inno72.job.task.mapper;

import java.util.List;
import java.util.Map;

import com.inno72.job.task.model.Inno72GameUserLogin;
import com.inno72.job.task.vo.Inno72GameUserLoginVo;

public interface Inno72GameUserLoginMapper{

	/**
	 * 查找没有处理过的url
	 * @return
	 */
	List<Inno72GameUserLogin> findAllNoProcessed();

	void update(Inno72GameUserLogin inno72GameUserLogin);

	Inno72GameUserLogin findMinTime();

	List<Inno72GameUserLoginVo> selectByTime(Map<String, String> loginParam);

	List<Inno72GameUserLogin> selectByPhoneModel(Map<String, String> param);

	List<String> selectAllUseId();

	List<String> selectAllCity();

}