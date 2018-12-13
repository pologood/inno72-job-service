package com.inno72.job.task.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.task.mapper.Inno72GameUserLifeMapper;
import com.inno72.job.task.mapper.Inno72GameUserMapper;
import com.inno72.job.task.mapper.Inno72GameUserTagMapper;
import com.inno72.job.task.mapper.Inno72GameUserTagRefMapper;
import com.inno72.job.task.model.Inno72GameUserTag;
import com.inno72.job.task.model.Inno72GameUserTagRef;
import com.inno72.job.task.util.Uuid;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;

/**
 * Hello world!
 *
 */
@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.UserProfilePaymentTask")
public class UserProfilePaymentTask implements IJobHandler {

	private static final String CODE_SHOPPING = "shopping";
	private static final String CODE_SAMPLE = "sample";


	public static final String TAG_SHOPPING = "购物控";
	public static final String TAG_SAMPLE = "样品控";

	@Resource
	private Inno72GameUserTagMapper inno72GameUserTagMapper;

	@Resource
	private Inno72GameUserTagRefMapper inno72GameUserTagRefMapper;

	@Resource
	private Inno72GameUserLifeMapper inno72GameUserLifeMapper;

	@Resource
	private Inno72GameUserMapper inno72GameUserMapper;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		JobLogger.log("支付 job, start");

		Inno72GameUserTag shoppingTag = inno72GameUserTagMapper.selectByCode(CODE_SHOPPING);
		Inno72GameUserTag sampleTag = inno72GameUserTagMapper.selectByCode(CODE_SAMPLE);

		Set<String> shoppingUsers = inno72GameUserMapper.getShoppingUsers(2);
		Set<String> sampleUsers = inno72GameUserMapper.getSampleUsers(2);

		this.saveTag(shoppingTag, shoppingUsers, TAG_SHOPPING);
		this.saveTag(sampleTag, sampleUsers, TAG_SAMPLE);

		JobLogger.log("支付 job, end");

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
