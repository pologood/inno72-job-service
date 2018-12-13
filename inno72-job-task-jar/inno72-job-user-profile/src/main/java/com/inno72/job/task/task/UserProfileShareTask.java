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
 * 分享标签任务
 */
@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.UserProfileShareTask")
public class UserProfileShareTask implements IJobHandler
{

	public static final Integer SHARE_COUNT = 0;

	private static final String CODE_SHARE = "share";

	public static final String TAG_SHARE_FAMILY = "分享族";

	@Resource
	private Inno72GameUserTagMapper inno72GameUserTagMapper;

	@Resource
	private Inno72GameUserTagRefMapper inno72GameUserTagRefMapper;

	@Resource
	private Inno72GameUserLifeMapper inno72GameUserLifeMapper;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		JobLogger.log("分享标签 job, start");

		// 分享族
		Inno72GameUserTag userTag = inno72GameUserTagMapper.selectByCode(CODE_SHARE);
		if (userTag == null){
			return new ReturnT<>(ReturnT.SUCCESS_CODE, "未找到需要处理的标签");
		}

		Set<String> shareUserIds = inno72GameUserLifeMapper.findShareUserIds(SHARE_COUNT);

		int deleteCount = inno72GameUserTagRefMapper.deleteByUserIdAndTagId(userTag.getId(), shareUserIds);
		JobLogger.log("分享标签 deleteCount is " + deleteCount);

		List<Inno72GameUserTagRef> refsAttempt = new ArrayList<>(shareUserIds.size());
		LocalDateTime endTimeLocal = LocalDateTime.now();
		for (String userId : shareUserIds){
			refsAttempt.add(new Inno72GameUserTagRef(Uuid.genUuid(), userId, userTag.getId(), TAG_SHARE_FAMILY, endTimeLocal));
		}
		inno72GameUserTagRefMapper.insertS(refsAttempt);

		JobLogger.log("分享标签 job, end");
		return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
	}

	@Override
	public void init() {
		
		
	}

	@Override
	public void destroy() {
		
		
	}
   
}
