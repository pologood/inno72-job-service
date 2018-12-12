package com.inno72.job.task.mapper;


import java.util.List;
import java.util.Map;
import java.util.Set;

import com.inno72.job.task.model.Inno72GameUserLife;

public interface Inno72GameUserLifeMapper{
	String selectMinDateFromLife();

	List<Inno72GameUserLife> selectLifeByLoginTime(Map<String, String> params);

	/**
	 * 查找满足分享次数的分享用户
	 * @param count 分享次数
	 * @return
	 */
	Set<String> findShareUserIds(Integer count);

	/**
	 * 查找游戏达人
	 * @return
	 */
	Set<String> findGameTalentUserIds();

	/**
	 * 查找游戏新手
	 * @return
	 */
	Set<String> findGameNoviceUserIds();
}