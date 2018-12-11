package com.inno72.job.task.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.inno72.job.task.model.Inno72GameUserTagRef;

public interface Inno72GameUserTagRefMapper {
	int insertS(List<Inno72GameUserTagRef> refsInteraction);

	int deleteByUserIdAndTagId(@Param("tagId") String tagId, @Param("set")Set<String> users);
}