package com.inno72.job.executer.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import com.inno72.job.executer.model.Inno72MachineDataCount;

@Mapper
public interface Inno72MachineDataCountMapper{
	List<Inno72MachineDataCount> selectAll();
	int insertS(List<Inno72MachineDataCount> list);
	String findLastDate();
	@MapKey("machineCode")
	Map<String, Map<String, String>> findAllMachine();
}