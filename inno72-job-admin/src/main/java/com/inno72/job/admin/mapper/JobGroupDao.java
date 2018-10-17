package com.inno72.job.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.inno72.job.admin.model.JobGroup;



public interface JobGroupDao {

    public List<JobGroup> findAll();

    public List<JobGroup> findByAddressType(@Param("addressType") int addressType);

    public int save(JobGroup JobGroup);

    public int update(JobGroup JobGroup);

    public int remove(@Param("id") int id);

    public JobGroup load(@Param("id") int id);
}
