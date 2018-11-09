package com.inno72.job.executer.mapper;

import com.inno72.job.executer.model.Inno72NeedExportStore;
import com.inno72.job.executer.vo.MachineSellerVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface ActivityMapper {
    List<Inno72NeedExportStore> findAllInteractMachineIdAndSellerId(@Param("currIndex") Integer currIndex, @Param("pageSize") Integer pageSize);

    List<Inno72NeedExportStore> findAllActivityMachineIdAndSellerId(@Param("currIndex") Integer currIndex, @Param("pageSize") Integer pageSize);

    List<MachineSellerVo> findAddDeviceInteractMachineIdAndSellerId(@Param("currIndex") Integer currIndex, @Param("pageSize") Integer pageSize);

    List<MachineSellerVo> findAddDeviceActivityMachineIdAndSellerId(@Param("currIndex") Integer currIndex, @Param("pageSize") Integer pageSize);
}

