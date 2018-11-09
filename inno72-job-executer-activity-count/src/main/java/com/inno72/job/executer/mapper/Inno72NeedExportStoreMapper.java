package com.inno72.job.executer.mapper;

import com.inno72.job.executer.model.Inno72NeedExportStore;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
@org.apache.ibatis.annotations.Mapper
public interface Inno72NeedExportStoreMapper extends Mapper<Inno72NeedExportStore> {
    Long findDeviceSize(@Param("sellerId") String sellerId,@Param("machineCode") String machineCode);
}