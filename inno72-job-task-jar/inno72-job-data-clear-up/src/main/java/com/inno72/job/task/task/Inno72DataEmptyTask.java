package com.inno72.job.task.task;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.task.mapper.Inno72ActivityMapper;
import com.inno72.job.task.mapper.Inno72MerchantTotalCountByDayMapper;
import com.inno72.job.task.model.Inno72Activity;
import com.inno72.job.task.model.Inno72MerchantTotalCountByDay;


@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.Inno72DataClearUpTask")
public class Inno72DataEmptyTask implements IJobHandler {

	@Resource
	private Inno72MerchantTotalCountByDayMapper inno72MerchantTotalCountByDayMapper;
	@Resource
	private Inno72ActivityMapper inno72ActivityMapper;


	@Override
	public ReturnT<String> execute(String param) throws Exception {

		inno72MerchantTotalCountByDayMapper.deleteCount();
		inno72MerchantTotalCountByDayMapper.delete();
		// 修补活动ID为空情况
		return  new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");

	}

	public static boolean isChinese(String string){
		int n = 0;
		for(int i = 0; i < string.length(); i++) {
			n = (int)string.charAt(i);
			if(!(19968 <= n && n <40869)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
