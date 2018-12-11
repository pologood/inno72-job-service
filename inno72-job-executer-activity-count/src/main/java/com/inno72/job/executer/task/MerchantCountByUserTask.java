package com.inno72.job.executer.task;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.inno72.common.datetime.LocalDateTimeUtil;
import com.inno72.common.datetime.LocalDateUtil;
import com.inno72.common.utils.StringUtil;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.executer.mapper.Inno72GameUserLifeMapper;
import com.inno72.job.executer.mapper.Inno72MerchantTotalCountByDayMapper;
import com.inno72.job.executer.mapper.Inno72MerchantTotalCountByUserMapper;
import com.inno72.job.executer.model.Inno72GameUserLife;
import com.inno72.job.executer.model.Inno72MerchantTotalCountByDay;
import com.inno72.job.executer.model.Inno72MerchantTotalCountByUser;
import com.inno72.job.executer.vo.OrderVo;

@Component
@JobHandler("merchant.MerchantCountByUserTask")
public class MerchantCountByUserTask implements IJobHandler {

	@Resource
	private Inno72MerchantTotalCountByUserMapper inno72MerchantTotalCountByUserMapper;
	@Resource
	private Inno72MerchantTotalCountByDayMapper inno72MerchantTotalCountByDayMapper;
	@Resource
	private Inno72GameUserLifeMapper inno72GameUserLifeMapper;
	@Override
	public ReturnT<String> execute(String args) throws Exception {

		String startTime = inno72MerchantTotalCountByUserMapper.selectMaxLastUpdateTime();
		if (StringUtil.isEmpty(startTime)){
			startTime = inno72MerchantTotalCountByDayMapper.selectLastDateFromLife();
		}
		LocalDateTime startTimeLocal = LocalDateTimeUtil.transfer(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LocalDateTime endTimeLocal = LocalDateTime.now();

		if (startTimeLocal.isAfter(endTimeLocal)){
			return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
		}

		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

		while (true) {

			LocalDateTime plusDays = startTimeLocal.plusDays(1);

			long days = Duration.between(startTimeLocal, endTimeLocal).toDays();
			if (days <= 0) {
				break;
			}
			if (days < 1) {
				plusDays = endTimeLocal;
			}
			Map<String, String> params = new HashMap<>();
			params.put("startTime", LocalDateUtil.transfer(startTimeLocal.toLocalDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			params.put("endTime",  LocalDateUtil.transfer(plusDays.toLocalDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			fixedThreadPool.execute(() -> {
				JobLogger.log("执行线程 - 参数 " + JSON.toJSONString(params));

				List<Inno72GameUserLife> lives = inno72GameUserLifeMapper.selectLifeByLoginTime(params);

				if (lives.size() == 0){
					JobLogger.log("查询参数 - "+ JSON.toJSONString(params) +"结果为空");
					return;
				}

				List<Inno72MerchantTotalCountByUser> users = new ArrayList<>();
				if (users.size() > 0){
					inno72MerchantTotalCountByUserMapper.insertS(users);
				}

			});
			startTimeLocal = plusDays;
		}
		return  new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
