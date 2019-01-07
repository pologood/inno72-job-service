package com.inno72.job.task.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.MapKey;

import com.inno72.job.task.model.Inno72Activity;

public interface Inno72ActivityMapper {
	@MapKey("id")
	Map<String,Inno72Activity> selectMap();
}
