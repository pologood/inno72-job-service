package com.inno72.job.task.mapper;

import com.inno72.job.task.model.Inno72GameUserTag;

public interface Inno72GameUserTagMapper {

	Inno72GameUserTag selectByCode(String code);

	void update(Inno72GameUserTag userTag);

}