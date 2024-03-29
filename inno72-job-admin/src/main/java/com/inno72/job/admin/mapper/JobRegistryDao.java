package com.inno72.job.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.inno72.job.admin.model.JobRegistry;

public interface JobRegistryDao {

    public int removeDead(@Param("timeout") int timeout);

    public List<JobRegistry> findAll(@Param("timeout") int timeout);

    public int registryUpdate(@Param("registryGroup") String registryGroup,
                              @Param("registryKey") String registryKey,
                              @Param("registryValue") String registryValue);

    public int registrySave(@Param("registryGroup") String registryGroup,
                            @Param("registryKey") String registryKey,
                            @Param("registryValue") String registryValue);

    public int registryDelete(@Param("registryGroup") String registGroup,
                          @Param("registryKey") String registryKey,
                          @Param("registryValue") String registryValue);

}
