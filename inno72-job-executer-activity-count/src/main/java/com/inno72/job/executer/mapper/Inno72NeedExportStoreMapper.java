package com.inno72.job.executer.mapper;

import com.inno72.job.executer.model.Inno72NeedExportStore;
import tk.mybatis.mapper.common.Mapper;
@org.apache.ibatis.annotations.Mapper
public interface Inno72NeedExportStoreMapper extends Mapper<Inno72NeedExportStore> {
    Long findDeviceSize(String sellerId, String machineCode);
}