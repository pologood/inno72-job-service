package com.inno72.job.executer.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.inno72.job.executer.model.Inno72GameUserLife;

@Mapper
public interface Inno72GameUserLifeMapper{
	List<Inno72GameUserLife> selectLifeByLoginTime(Map<String, String> params);
}