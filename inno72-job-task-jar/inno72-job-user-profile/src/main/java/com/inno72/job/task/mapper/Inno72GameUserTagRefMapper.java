package com.inno72.job.task.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.inno72.job.task.model.Inno72GameUserTagRef;
import com.inno72.job.task.vo.Inno72GameUserTagRefVo;

public interface Inno72GameUserTagRefMapper {
	int insertS(List<Inno72GameUserTagRef> refsInteraction);

	int deleteByUserIdAndTagId(@Param("tagId") String tagId, @Param("set")Set<String> users);

	List<String> selectUserIdsByTagIdAndUserId(@Param("tagId") String tagId, @Param("set")Set<String> users);

	/**
	 * 查找需要同步的用户
	 * @return
	 */
	List<String> findUserIdsNeedSyn(String date);

	/**
	 * 查询 Inno72GameUserTagRef 通过条件i
	 * @return
	 */
	List<Inno72GameUserTagRef> findInno72GameUserTagRefByUserId(String userId);

	List<Inno72GameUserTagRefVo> selectByActIdAndUserId(Map<String, String> refParam);
}