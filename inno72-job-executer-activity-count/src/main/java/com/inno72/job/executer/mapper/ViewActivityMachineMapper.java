package com.inno72.job.executer.mapper;

import com.inno72.job.executer.model.ViewActivityMachine;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ViewActivityMachineMapper{
    List<ViewActivityMachine> findAll();

    List<String> findAllMachine();
}