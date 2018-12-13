package com.inno72.job.task.task;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.task.mapper.Inno72GameUserLifeMapper;
import com.inno72.job.task.mapper.Inno72GameUserTagMapper;
import com.inno72.job.task.mapper.Inno72GameUserTagRefMapper;
import com.inno72.job.task.model.Inno72GameUserTag;
import com.inno72.job.task.model.Inno72GameUserTagRef;
import com.inno72.job.task.util.Uuid;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 游戏时长任务 游戏达人 10-15， 游戏新手 > 15
 */
@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.UserProfileGameTimeTask")
public class UserProfileGameTimeTask implements IJobHandler
{

	private static final String CODE_GAME_TALENT = "game_talent";
	private static final String CODE_GAME_NOVICE = "game_novice";

	public static final String TAG_GAME_TALENT = "游戏达人";

	public static final String TAG_GAME_NOVICE = "游戏新手";

	@Resource
	private Inno72GameUserTagMapper inno72GameUserTagMapper;

	@Resource
	private Inno72GameUserTagRefMapper inno72GameUserTagRefMapper;

	@Resource
	private Inno72GameUserLifeMapper inno72GameUserLifeMapper;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		JobLogger.log("游戏达人游戏新手job, start");

		// 游戏时长
		Inno72GameUserTag gameTalentTag = inno72GameUserTagMapper.selectByCode(CODE_GAME_TALENT);

		Inno72GameUserTag gameNoviceTag = inno72GameUserTagMapper.selectByCode(CODE_GAME_NOVICE);

		Set<String> gameTalentUserIds = inno72GameUserLifeMapper.findGameTalentUserIds();

		Set<String> gameNoviceUserIds = inno72GameUserLifeMapper.findGameNoviceUserIds();

		if (gameTalentUserIds.size() > 0) {
			saveTag(gameTalentTag, gameTalentUserIds, TAG_GAME_TALENT);
		}
		if (gameNoviceUserIds.size() > 0) {
			saveTag(gameNoviceTag, gameNoviceUserIds, TAG_GAME_NOVICE);
		}

		JobLogger.log("游戏达人游戏新手job, end");
		return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
	}

	void saveTag(Inno72GameUserTag userTag, Set<String> userIds, String tagContent) {
		int deleteCount = inno72GameUserTagRefMapper.deleteByUserIdAndTagId(userTag.getId(), userIds);
		JobLogger.log("deleteCount is " + deleteCount);

		List<Inno72GameUserTagRef> refsAttempt = new ArrayList<>(userIds.size());
		LocalDateTime endTimeLocal = LocalDateTime.now();
		for (String userId : userIds){
			refsAttempt.add(new Inno72GameUserTagRef(Uuid.genUuid(), userId, userTag.getId(), tagContent, endTimeLocal));
		}
		inno72GameUserTagRefMapper.insertS(refsAttempt);
	}

	@Override
	public void init() {
		
		
	}

	@Override
	public void destroy() {
		
		
	}
   
}
