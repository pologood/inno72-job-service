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

	private static final String CODE_INTERACTION = "interaction";

	private static final String CODE_ATTEMPT = "attempt";

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		JobLogger.log("尝鲜族 job, start");

		//尝鲜族
		Inno72GameUserTag userTag = inno72GameUserTagMapper.selectLastUpdateTime(CODE_INTERACTION);
		if (userTag == null){
			return new ReturnT<>(ReturnT.SUCCESS_CODE, "未找到需要处理的标签");
		}

		LocalDateTime startTime = userTag.getUpdateTime();
		if ( startTime == null ){
			String lifeStartTime = inno72GameUserLifeMapper.selectMinDateFromLife();
			if (StringUtils.isEmpty(lifeStartTime)){
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
		Set<String> interaction = new HashSet<>();
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

			JobLogger.log("尝鲜族 执行线程 - 参数 *************************** " + JSON.toJSONString(params));

			List<Inno72GameUserLife> lives = inno72GameUserLifeMapper.selectLifeByLoginTime(params);

			JobLogger.log("尝鲜族 执行线程 - 参数 ***** " + JSON.toJSONString(params) + "*****结果【" + lives.size() +"条】*****");

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

				// 尝鲜族
				if (between.toMinutes() >= 15){
					interaction.add(gameUserId);
				}
			}

			startTimeLocal = plusDays;
		}

		JobLogger.log("互动控 用户分组 - "+ JSON.toJSONString(attempt) + "共-" + attempt.size() +"条");
		JobLogger.log("尝鲜族 用户分组 - "+ JSON.toJSONString(interaction) + "共-" + interaction.size() +"条");

		if (attempt.size() > 0){
			List<Inno72GameUserTagRef> refsAttempt = new ArrayList<>(attempt.size());
			//互动族
			Inno72GameUserTag userTagAttempt = inno72GameUserTagMapper.selectLastUpdateTime(CODE_ATTEMPT);
			if (userTagAttempt != null){
				for (String userId : attempt){
					//String id, String userId, String tagId, String content
					refsAttempt.add(new Inno72GameUserTagRef(Uuid.genUuid(), userId, userTagAttempt.getId(), userTagAttempt.getName()));
				}
				int i = inno72GameUserTagRefMapper.insertS(refsAttempt);

				userTag.setUpdateTime(endTimeLocal);
				userTag.setUpdateId("execute");

				inno72GameUserTagMapper.update(userTagAttempt);
			}
		}

		if (interaction.size() > 0){
			List<Inno72GameUserTagRef> refsInteraction = new ArrayList<>(attempt.size());
			for (String userId : interaction){
				refsInteraction.add(new Inno72GameUserTagRef(Uuid.genUuid(), userId, userTag.getId(), userTag.getName()));
			}
			int i = inno72GameUserTagRefMapper.insertS(refsInteraction);
		}

		userTag.setUpdateTime(endTimeLocal);
		userTag.setUpdateId("execute");

		inno72GameUserTagMapper.update(userTag);

		JobLogger.log("尝鲜族 job, end");
		return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
	}

	@Override
	public void init() {
		
		
	}

	@Override
	public void destroy() {
		
		
	}
   
}
