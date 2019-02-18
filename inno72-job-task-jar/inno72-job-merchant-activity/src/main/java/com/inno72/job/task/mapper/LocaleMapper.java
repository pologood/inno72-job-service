package com.inno72.job.task.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.MapKey;

import com.inno72.job.task.vo.LocaleVo;

public interface LocaleMapper {

	@MapKey("id")
	Map<String, LocaleVo> select();
}
