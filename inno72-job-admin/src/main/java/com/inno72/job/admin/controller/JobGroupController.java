package com.inno72.job.admin.controller;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inno72.job.admin.model.JobGroup;
import com.inno72.job.admin.service.JobService;
import com.inno72.job.core.biz.model.ReturnT;


@Controller
@RequestMapping("/jobgroup")
public class JobGroupController {

	@Resource
	private JobService jobService;
	
	
	@RequestMapping
	@ResponseBody
	public ReturnT<List<JobGroup>> index() {

		List<JobGroup> list = jobService.findAllJobGroup();
		
		return new ReturnT<List<JobGroup>>(list);
	}
	
	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<Void> save(JobGroup jobGroup){

		// valid
		if (jobGroup.getAppName()==null || StringUtils.isBlank(jobGroup.getAppName())) {
			return new ReturnT<Void>(-1, "请输入AppName" );
		}
		if (jobGroup.getAppName().length()<4 || jobGroup.getAppName().length()>64) {
			return new ReturnT<Void>(-1, "appname 长度非法 打印 4 小于 64");
		}
		if (jobGroup.getTitle()==null || StringUtils.isBlank(jobGroup.getTitle())) {
			return new ReturnT<Void>(-1, "请输入标题" );
		}
		if (jobGroup.getAddressType()!=0) {
			if (StringUtils.isBlank(jobGroup.getAddressList())) {
				return new ReturnT<Void>(-1, "手动录入注册方式，机器地址不可为空");
			}
			String[] addresss = jobGroup.getAddressList().split(",");
			for (String item: addresss) {
				if (StringUtils.isBlank(item)) {
					return new ReturnT<Void>(-1, "机器地址格式非法" );
				}
			}
		}

		int ret = jobService.saveJobGroup(jobGroup);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<Void> update(JobGroup jobGroup){
		// valid
		if (jobGroup.getAppName()==null || StringUtils.isBlank(jobGroup.getAppName())) {
			return new ReturnT<Void>(-1, "请输入AppName" );
		}
		if (jobGroup.getAppName().length()<4 || jobGroup.getAppName().length()>64) {
			return new ReturnT<Void>(-1, "appname 长度非法 打印 4 小于 64");
		}
		if (jobGroup.getTitle()==null || StringUtils.isBlank(jobGroup.getTitle())) {
			return new ReturnT<Void>(-1, "请输入标题" );
		}
		if (jobGroup.getAddressType()!=0) {
			if (StringUtils.isBlank(jobGroup.getAddressList())) {
				return new ReturnT<Void>(-1, "手动录入注册方式，机器地址不可为空");
			}
			String[] addresss = jobGroup.getAddressList().split(",");
			for (String item: addresss) {
				if (StringUtils.isBlank(item)) {
					return new ReturnT<Void>(-1, "机器地址格式非法" );
				}
			}
		}

		int ret = jobService.updateJobGroup(jobGroup);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<Void> remove(int id){

		return this.jobService.removeJobGroup(id);
		
	}

}
