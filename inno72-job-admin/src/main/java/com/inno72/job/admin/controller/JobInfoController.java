package com.inno72.job.admin.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.quartz.SchedulerException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.inno72.job.admin.common.ExecutorFailStrategyEnum;
import com.inno72.job.admin.common.ExecutorRouteStrategyEnum;
import com.inno72.job.admin.model.JobInfo;
import com.inno72.job.admin.service.JobService;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.enums.ExecutorBlockStrategyEnum;
import com.inno72.job.core.glue.GlueTypeEnum;

@Controller
@RequestMapping("/jobinfo")
public class JobInfoController {
	
	
	@Resource
	private JobService jobService;
		
	@RequestMapping("/enumInfo")
	@ResponseBody
	public ReturnT<Map<String, Object>> jobEnumInfo(){
		Map<String, Object> ret = new HashMap<String, Object>();
		
		ret.put("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());
		ret.put("GlueTypeEnum", GlueTypeEnum.values());
		ret.put("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());
		ret.put("ExecutorFailStrategyEnum", ExecutorFailStrategyEnum.values());
		
		return new ReturnT<Map<String, Object>>(ret);	
	}
	
	
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int pageNo,  
			@RequestParam(required = false, defaultValue = "10") int pageSize,
			@RequestParam(required = false, defaultValue = "0") int jobGroup, 
			String jobDesc, String executorHandler, String filterTime) {
		
		return jobService.pageList(pageNo, pageSize, jobGroup, jobDesc, executorHandler, filterTime);
	}
	
	@RequestMapping(value="/addscript", method = RequestMethod.POST)
	@ResponseBody
	public ReturnT<Void> addScript(JobInfo jobInfo) {
		try {
			return jobService.add(jobInfo, null);
		} catch (IOException e) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, e.getMessage());
		}
	}
	
	@RequestMapping(value="/addjar", method = RequestMethod.POST)
	@ResponseBody
	public ReturnT<Void> addJar(MultipartFile file, JobInfo jobInfo) {
		
		byte[] jarFile;
		try {
			jarFile = file.getBytes();
		} catch (IOException e) {
			return new ReturnT<Void>(-1, "上传文件错误");
		}
		
		try {
			return jobService.add(jobInfo, jarFile);
		} catch (IOException e) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, e.getMessage());
		}
	}
	
	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<Void> update(JobInfo jobInfo) {
		try {
			return jobService.update(jobInfo);
		} catch (SchedulerException | IOException e) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, e.getMessage());
		}
	}
	
	@RequestMapping(value="/updateJarSource",  method = RequestMethod.POST)
	@ResponseBody
	public ReturnT<Void> updateJarSource(MultipartFile file, Integer jobId,  String checksum) {
		
		byte[] jarFile;
		try {
			jarFile = file.getBytes();
		} catch (IOException e) {
			return new ReturnT<Void>(-1, "上传文件错误");
		}
		
		try {
			return jobService.updateJarSource(jobId, checksum, jarFile);
		} catch (IOException e) {
			return new ReturnT<Void>(ReturnT.FAIL_CODE, e.getMessage());
		}
	}
	
	
	@RequestMapping(value="/updateScriptSource")
	@ResponseBody
	public ReturnT<Void> updateScriptSource(Integer jobId, String source) {	
		return jobService.updateScriptSource(jobId,source);
	}
	
	
	
	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<Void> remove(int id) {
		return jobService.remove(id);
	}
	
	@RequestMapping("/pause")
	@ResponseBody
	public ReturnT<Void> pause(int id) {
		return jobService.pause(id);
	}
	
	@RequestMapping("/resume")
	@ResponseBody
	public ReturnT<Void> resume(int id) {
		return jobService.resume(id);
	}
	
	@RequestMapping("/trigger")
	@ResponseBody
	public ReturnT<Void> triggerJob(int id) {
		return jobService.triggerJob(id);
	}

}
