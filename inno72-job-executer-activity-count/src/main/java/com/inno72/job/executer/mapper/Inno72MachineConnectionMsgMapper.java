package com.inno72.job.executer.mapper;

import com.inno72.job.executer.model.Inno72MachineConnectionMsg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface Inno72MachineConnectionMsgMapper{
    List<Inno72MachineConnectionMsg> findUnManageMsg();

    void updateStatusById(@Param("id") String id, @Param("status") Integer status);
}