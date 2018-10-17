package com.inno72.job.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.inno72.job.admin.model.JobInfo;


public interface JobInfoDao {

	public List<JobInfo> pageList(@Param("offset") int offset,
									 @Param("pagesize") int pagesize,
									 @Param("jobGroup") int jobGroup,
									 @Param("jobDesc") String jobDesc,
									 @Param("executorHandler") String executorHandler);
	public int pageListCount(@Param("offset") int offset,
							 @Param("pagesize") int pagesize,
							 @Param("jobGroup") int jobGroup,
							 @Param("jobDesc") String jobDesc,
							 @Param("executorHandler") String executorHandler);
	
	public int save(JobInfo info);

	public JobInfo loadById(@Param("id") int id);
	
	public int update(JobInfo item);
	
	public int delete(@Param("id") int id);

	public List<JobInfo> getJobsByGroup(@Param("jobGroup") int jobGroup);

	public int findAllCount();

}
