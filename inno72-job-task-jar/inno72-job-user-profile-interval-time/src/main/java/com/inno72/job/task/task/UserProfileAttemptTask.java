package com.inno72.job.task.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
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
import com.inno72.job.task.model.Inno72GameUserTag;
import com.inno72.job.task.model.Inno72GameUserTagRef;
import com.inno72.job.task.util.Uuid;

@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.UserProfileAttemptTask")
public class UserProfileAttemptTask implements IJobHandler
{

	@Resource
	private MongoOperations operations;

	@Resource
	private Inno72GameUserTagMapper inno72GameUserTagMapper;

	@Resource
	private Inno72GameUserTagRefMapper inno72GameUserTagRefMapper;

	@Resource
	private Inno72GameUserLifeMapper inno72GameUserLifeMapper;

	private static final String CODE_ATTEMPT = "attempt";

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		JobLogger.log("互动控 job, start");

		//互动控
		Inno72GameUserTag userTag = inno72GameUserTagMapper.selectByCode(CODE_ATTEMPT);
		if (userTag == null){
			return new ReturnT<>(ReturnT.SUCCESS_CODE, "未找到需要处理的标签");
		}

		LocalDateTime startTime = userTag.getUpdateTime();
		if ( startTime == null ){
			String lifeStartTime = inno72GameUserLifeMapper.selectMinDateFromLife();
			if (StringUtils.isEmpty(lifeStartTime)){
				JobLogger.log("未找到需要处理的数据");
				return new ReturnT<>(ReturnT.SUCCESS_CODE, "未找到需要处理的数据");

			}
			startTime = LocalDateTime.parse(lifeStartTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") );
		}
		LocalDateTime startTimeLocal = startTime;
		LocalDateTime endTimeLocal = LocalDateTime.now();

		if (startTimeLocal.isAfter(endTimeLocal)){
			return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
		}

		Map<String, Integer> usersMap = new HashMap<>();
		Set<String> attempt = new HashSet<>();

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

			JobLogger.log("互动控 执行线程 - 参数 *************************** " + JSON.toJSONString(params));

			List<Inno72GameUserLife> lives = inno72GameUserLifeMapper.selectLifeByLoginTime(params);

			JobLogger.log("互动控 执行线程 - 参数 ***** " + JSON.toJSONString(params) + "*****结果【" + lives.size() +"条】*****");

			if (lives.size() == 0){
				JobLogger.log("查询参数 - "+ JSON.toJSONString(params) +"结果为空");
				startTimeLocal = plusDays;
				continue;
			}

			for ( Inno72GameUserLife life : lives ){
				LocalDateTime loginTime = life.getLoginTime();
				Duration between = Duration.between(startTimeLocal, loginTime);
				String gameUserId = life.getGameUserId();

				if (StringUtils.isEmpty(gameUserId)){
					continue;
				}

				Integer integer = usersMap.get(gameUserId);
				if (integer == null){
					integer = 1;
				}else {
					integer += 1;
				}
				if (integer > 1){
					attempt.add(gameUserId);
				}
				usersMap.put(gameUserId, integer);

			}

			startTimeLocal = plusDays;
		}

		JobLogger.log("互动控 用户分组 - "+ JSON.toJSONString(attempt) + "共-" + attempt.size() +"条");

		if (attempt.size() > 0){
			List<Inno72GameUserTagRef> refsAttempt = new ArrayList<>(attempt.size());
			for (String userId : attempt){
				refsAttempt.add(new Inno72GameUserTagRef(Uuid.genUuid(), userId, userTag.getId(), userTag.getName(), endTimeLocal));
			}
			int deleteSize = inno72GameUserTagRefMapper.deleteByUserIdAndTagId(userTag.getId(), attempt);
			JobLogger.log("互动控 job, 删除已有互动控用户ID集合"+JSON.toJSONString(attempt)+"关联,共 【"+deleteSize+"】条");
			int i = inno72GameUserTagRefMapper.insertS(refsAttempt);
			JobLogger.log("互动控 job, 插入互动控用户ID集合"+JSON.toJSONString(attempt)+"关联,共 【"+i+"】条");
		}

		userTag.setUpdateTime(endTimeLocal);
		userTag.setUpdateId("execute");

		inno72GameUserTagMapper.update(userTag);

		JobLogger.log("互动控 job, end");
		return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
	}

	@Override
	public void init() {
		
		
	}

	@Override
	public void destroy() {
		
		
	}
   
}
