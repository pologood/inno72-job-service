package com.inno72.job.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoOperations;

import com.alibaba.fastjson.JSON;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.task.mapper.Inno72GameUserLifeMapper;
import com.inno72.job.task.mapper.Inno72GameUserTagMapper;
import com.inno72.job.task.mapper.Inno72GameUserTagRefMapper;
import com.inno72.job.task.model.Inno72GameUserLife;

import tk.mybatis.mapper.util.StringUtil;

@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.UserProfileIntervalTimeTask")
public class UserProfileIntervalTimeTask implements IJobHandler
{


	@Resource
	private MongoOperations operations;

	@Resource
	private Inno72GameUserTagMapper inno72GameUserTagMapper;

	@Resource
	private Inno72GameUserTagRefMapper inno72GameUserTagRefMapper;

	@Resource
	private Inno72GameUserLifeMapper inno72GameUserLifeMapper;

	private static final String CODE = "interaction";
	@Override
	public ReturnT<String> execute(String param) throws Exception {
		JobLogger.log("example job, start");


		String startTime = inno72GameUserTagMapper.selectLastUpdateTime(CODE);
		if (StringUtil.isEmpty(startTime)){
			startTime = inno72GameUserLifeMapper.selectMinDateFromLife();
		}
		LocalDateTime startTimeLocal = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LocalDateTime endTimeLocal = LocalDateTime.now();

		if (startTimeLocal.isAfter(endTimeLocal)){
			return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
		}

		Map<String, Integer> usersMap = new HashMap<>();
		List<String> interaction = new ArrayList<>();

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
			params.put("startTime", startTimeLocal.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			params.put("endTime",  plusDays.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

			JobLogger.log("尝鲜族 执行线程 - 参数 *************************** " + JSON.toJSONString(params));

			List<Inno72GameUserLife> lives = inno72GameUserLifeMapper.selectLifeByLoginTime(params);

			if (lives.size() == 0){
				JobLogger.log("查询参数 - "+ JSON.toJSONString(params) +"结果为空");
				continue;
			}

//			for ( Inno72GameUserLife life : lives ){
//				LocalDateTime loginTime = life.getLoginTime();
//				Duration between = Duration.between(startTimeLocal, loginTime);
//				String gameUserId = life.getGameUserId();
//
//				Integer integer = usersMap.get(gameUserId);
//				if (integer == null){
//					integer = 1;
//				}else {
//					integer += 1;
//				}
//				usersMap.put(gameUserId, integer);
//
//				// 尝鲜族
//				if (between.toMinutes() >= 15){
//					interaction.add(gameUserId);
//				}
//			}

			startTimeLocal = plusDays;
		}

		JobLogger.log("example job, end");
		return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
	}

	@Override
	public void init() {
		
		
	}

	@Override
	public void destroy() {
		
		
	}
   
}
