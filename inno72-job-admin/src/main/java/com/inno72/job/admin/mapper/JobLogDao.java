package com.inno72.job.admin.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.inno72.job.admin.model.JobLog;

public interface JobLogDao {
	
	public List<JobLog> pageList(@Param("offset") int offset,
									@Param("pagesize") int pagesize,
									@Param("jobGroup") int jobGroup,
									@Param("jobId") int jobId,
									@Param("triggerTimeStart") Date triggerTimeStart,
									@Param("triggerTimeEnd") Date triggerTimeEnd,
									@Param("logStatus") int logStatus);
	public int pageListCount(@Param("offset") int offset,
							 @Param("pagesize") int pagesize,
							 @Param("jobGroup") int jobGroup,
							 @Param("jobId") int jobId,
							 @Param("triggerTimeStart") Date triggerTimeStart,
							 @Param("triggerTimeEnd") Date triggerTimeEnd,
							 @Param("logStatus") int logStatus);
	
	public JobLog load(@Param("id") int id);

	public int save(JobLog JobLog);

	public int updateTriggerInfo(JobLog JobLog);

	public int updateHandleInfo(JobLog JobLog);
	
	public int delete(@Param("jobId") int jobId);

	public int triggerCountByHandleCode(@Param("handleCode") int handleCode);

	public List<Map<String, Object>> triggerCountByDay(@Param("from") Date from,
													   @Param("to") Date to);

	public int clearLog(@Param("jobGroup") int jobGroup,
						@Param("jobId") int jobId,
						@Param("clearBeforeTime") Date clearBeforeTime,
						@Param("clearBeforeNum") int clearBeforeNum);

}
