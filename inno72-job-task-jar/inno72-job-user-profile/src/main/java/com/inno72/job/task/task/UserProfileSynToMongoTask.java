package com.inno72.job.task.task;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.task.mapper.Inno72GameUserLoginMapper;
import com.inno72.job.task.mapper.Inno72GameUserTagRefMapper;
import com.inno72.job.task.model.Inno72GameUserTagRef;
import com.inno72.job.task.util.DateUtil;
import com.inno72.job.task.vo.Inno72UserProfile;
import com.inno72.job.task.vo.TagEnum;
import com.inno72.mongo.MongoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 同步用户画像标签到mongo
 */
@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.UserProfileSynToMongoTask")
public class UserProfileSynToMongoTask implements IJobHandler
{
	@Resource
	private Inno72GameUserTagRefMapper inno72GameUserTagRefMapper;

	@Resource
	private MongoUtil mongoUtil;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		JobLogger.log("同步用户画像标签到mongo job, start");

		String todayShort = DateUtil.getTodayShort();

		String beginTime  = todayShort + " 00:00:00";
		JobLogger.log("beginTime is " + beginTime);

		List<String> userIds = inno72GameUserTagRefMapper.findUserIdsNeedSyn(beginTime);

		for (String userId : userIds) {
			boolean isUpdate = false;
			// 判断用户是否再monggo存在，如果存在更新mongo数据，不存在插入
			Inno72UserProfile _inno72UserProfile = new Inno72UserProfile();
			_inno72UserProfile.setUserId(userId);
			long count = mongoUtil.count(_inno72UserProfile, Inno72UserProfile.class);

			if (count > 1) {
				isUpdate = true;
			}

			List<Inno72GameUserTagRef> gameUserTagRefs = inno72GameUserTagRefMapper.findInno72GameUserTagRefByUserId(userId);
			for (Inno72GameUserTagRef gameUserTagRef : gameUserTagRefs) {

				Map<String, Object> map = new HashMap<>();

				String tagCode = gameUserTagRef.getTagCode();
				String content = gameUserTagRef.getContent();
				Inno72UserProfile inno72UserProfile = new Inno72UserProfile();
				inno72UserProfile.setUserId(userId);

				if (tagCode.equals(TagEnum.AGE.getValue())) {
					if (isUpdate) {
						map.put(TagEnum.AGE.getValue(), content);
					} else {
						inno72UserProfile.setAge(content);
					}
				}

				if (tagCode.equals(TagEnum.GENDER.getValue())) {
					if (isUpdate) {
						map.put(TagEnum.GENDER.getValue(), content);
					} else {
						inno72UserProfile.setGender(content);
					}
				}

				if (tagCode.equals(TagEnum.SHOPPING.getValue())) {
					if (isUpdate) {
						map.put(TagEnum.SHOPPING.getValue(), content);
					} else {
						inno72UserProfile.setShopping(content);
					}
				}

				if (tagCode.equals(TagEnum.SAMPLE.getValue())) {
					if (isUpdate) {
						map.put(TagEnum.SAMPLE.getValue(), content);
					} else {
						inno72UserProfile.setSample(content);
					}
				}

				if (tagCode.equals(TagEnum.INTERACTION.getValue())) {
					if (isUpdate) {
						map.put(TagEnum.INTERACTION.getValue(), content);
					} else {
						inno72UserProfile.setInteraction(content);
					}
				}

				if (tagCode.equals(TagEnum.ATTEMPT.getValue())) {
					if (isUpdate) {
						map.put(TagEnum.ATTEMPT.getValue(), content);
					} else {
						inno72UserProfile.setAttempt(content);
					}
				}

				if (tagCode.equals(TagEnum.CITY.getValue())) {
					if (isUpdate) {
						map.put(TagEnum.CITY.getValue(), content);
					} else {
						inno72UserProfile.setCity(content);
					}
				}

				if (tagCode.equals(TagEnum.POS.getValue())) {
					if (isUpdate) {
						map.put(TagEnum.POS.getValue(), content);
					} else {
						inno72UserProfile.setPos(content);
					}
				}

				if (tagCode.equals(TagEnum.GAMETIME.getValue())) {
					if (isUpdate) {
						map.put(TagEnum.GAMETIME.getValue(), content);
					} else {
						inno72UserProfile.setGametime(content);
					}
				}

				if (tagCode.equals(TagEnum.BUYPOWER.getValue())) {
					if (isUpdate) {
						map.put(TagEnum.BUYPOWER.getValue(), content);
					} else {
						inno72UserProfile.setBuypower(content);
					}
				}

				if (isUpdate) {
					mongoUtil.update(_inno72UserProfile, map ,Inno72UserProfile.class);
				} else {
					mongoUtil.save(inno72UserProfile);
				}
			}
		}
		JobLogger.log("同步用户画像标签到mongo job, end");
		return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
	}

	@Override
	public void init() {
		
		
	}

	@Override
	public void destroy() {
		
		
	}
   
}
