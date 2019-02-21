package com.inno72.job.executer.mapper;

import com.inno72.job.executer.model.Inno72MachineActivityImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface Inno72MachineActivityImageMapper{
    List<Inno72MachineActivityImage> findAll();
}