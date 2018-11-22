package org.inno72.job.example.task.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface Inno72ActivityMapper {
	
	List<Map<String, String>> queryActivityInfo(@Param("type") int type);
	
}
