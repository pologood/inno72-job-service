package com.inno72.job.task.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

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
	private Inno72GameUserTagMapper inno72GameUserTagMapper;

	@Resource
	private Inno72GameUserTagRefMapper inno72GameUserTagRefMapper;

	@Resource
	private Inno72GameUserLifeMapper inno72GameUserLifeMapper;

	private static final String CODE_INTERACTION = "interaction";

	@Override
	public ReturnT<String> execute(String param) {
		JobLogger.log("互动控 job, start");

		//互动控
		Inno72GameUserTag userTag = inno72GameUserTagMapper.selectByCode(CODE_INTERACTION);
		if (userTag == null){
			return new ReturnT<>(ReturnT.SUCCESS_CODE, "未找到需要处理的标签");
		}

		String lifeStartTime = inno72GameUserLifeMapper.selectMinDateFromLife();
		if (StringUtils.isEmpty(lifeStartTime)){
			return new ReturnT<>(ReturnT.SUCCESS_CODE, "未找到需要处理的数据");
		}
		LocalDateTime startTimeLocal = LocalDateTime.parse(lifeStartTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") );
		LocalDateTime endTimeLocal = LocalDateTime.now();

		if (startTimeLocal.isAfter(endTimeLocal)){
			return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
		}

		Set<String> interaction = new HashSet<>();
		Map<String, Integer> interactionNum = new HashMap<>();
		while (true) {

			LocalDateTime plusDays = startTimeLocal.plusDays(1);

			long days = Duration.between(startTimeLocal, endTimeLocal).toDays();
			if (days <= 0) {
				break;
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
				// 互动控
				Integer integer = Optional.ofNullable(interactionNum.get(gameUserId)).orElse(0);
				int num = integer + 1;
				if (num > 1){
					interaction.add(gameUserId);
				}
				interactionNum.put(gameUserId, integer);
			}
			startTimeLocal = plusDays;
		}

		JobLogger.log("互动控 用户分组 - "+ JSON.toJSONString(interaction) + "共-" + interaction.size() +"条");

		if (interaction.size() > 0){
			List<Inno72GameUserTagRef> refsInteraction = new ArrayList<>(interaction.size());
			List<String> list = inno72GameUserTagRefMapper.selectUserIdsByTagIdAndUserId(userTag.getId(), interaction);
			for (String userId : interaction){
				if (!list.contains(userId)){
					refsInteraction.add(new Inno72GameUserTagRef(Uuid.genUuid(), userId, userTag.getId(), "互动控", endTimeLocal));
				}
			}

			int listSize = 800;
			if (refsInteraction.size() > listSize){

				List<List<Inno72GameUserTagRef>> groupRefs = new ArrayList<>();
				List<Inno72GameUserTagRef> groupRef = new ArrayList<>();
				int i = 0;
				for (Inno72GameUserTagRef ref : refsInteraction) {
					i++;
					groupRef.add(ref);
					if (groupRef.size() >= listSize){
						groupRefs.add(groupRef);
						groupRef = new ArrayList<>();
					}
					if (i == refsInteraction.size() && groupRefs.size() != 0){
						groupRefs.add(groupRef);
					}
				}
				for (List<Inno72GameUserTagRef> ss : groupRefs){
					int g = inno72GameUserTagRefMapper.insertS(ss);
					JobLogger.log("互动控 job, 分组插入互动控用户ID集合"+JSON.toJSONString(ss)+"关联,共 【"+g+"】条");
				}

			}else {
				int i = inno72GameUserTagRefMapper.insertS(refsInteraction);
				JobLogger.log("互动控 job, 插入互动控用户ID集合"+JSON.toJSONString(interaction)+"关联,共 【"+i+"】条");
			}
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
