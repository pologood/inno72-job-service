package com.inno72.job.task.mapper;


import java.util.List;
import java.util.Map;

import com.inno72.job.task.model.Inno72GameUserLife;

public interface Inno72GameUserLifeMapper{
	String selectMinDateFromLife();

	List<Inno72GameUserLife> selectLifeByLoginTime(Map<String, String> params);
}