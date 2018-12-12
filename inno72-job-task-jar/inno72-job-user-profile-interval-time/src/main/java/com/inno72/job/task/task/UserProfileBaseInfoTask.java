package com.inno72.job.task.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.task.mapper.Inno72GameUserLoginMapper;
import com.inno72.job.task.mapper.Inno72GameUserTagMapper;
import com.inno72.job.task.mapper.Inno72GameUserTagRefMapper;
import com.inno72.job.task.mapper.LocaleMapper;
import com.inno72.job.task.model.Inno72GameUserLogin;
import com.inno72.job.task.model.Inno72GameUserTag;
import com.inno72.job.task.model.Inno72GameUserTagRef;
import com.inno72.job.task.util.Uuid;
import com.inno72.job.task.vo.LocaleVo;

/**
 * 基础信息标签（年龄 性别 购买力）
 */
@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.UserProfileBaseInfoTask")
public class UserProfileBaseInfoTask implements IJobHandler
{
	@Resource
	private Inno72GameUserLoginMapper inno72GameUserLoginMapper;

	@Resource
	private LocaleMapper localeMapper;

	@Resource
	private Inno72GameUserTagMapper inno72GameUserTagMapper;

	@Resource
	private Inno72GameUserTagRefMapper inno72GameUserTagRefMapper;

	// 年龄  ************************************
	private static final String CODE_AGE = "age";


	// 性别
	private static final String CODE_GENDER = "gender";
	// 城市
	private static final String CODE_CITY = "city";
	// 点位
	private static final String CODE_POINT = "pos";

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		JobLogger.log("基础信息标签 job, start");
		Map<String, LocaleVo> localeVoMap = localeMapper.select();

		Inno72GameUserTag userTag = inno72GameUserTagMapper.selectByCode(CODE_AGE);
		if (userTag == null){
			return new ReturnT<>(ReturnT.SUCCESS_CODE, "没有需要处理的age标签");
		}

		LocalDateTime now = LocalDateTime.now();

		String updateTime = Optional.ofNullable(userTag.getUpdateTime()).map(v -> {
			return v.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		}).orElse("");
		// 性别
		Map<String, String> loginParam = new HashMap<>();
		loginParam.put("time", updateTime);
		List<Inno72GameUserLogin> userLogins = inno72GameUserLoginMapper.selectByTime(loginParam);
		Map<String, String> userAgeMap = new HashMap<>();
		Map<String, String> userSexMap = new HashMap<>();
		Map<String, String> userCityMap = new HashMap<>();
		Map<String, String> userPointMap = new HashMap<>();

		for (Inno72GameUserLogin login:userLogins){

			String userId = login.getUserId();
			String age = login.getAge();
			String sex = login.getSex();
			String localeId = login.getLocaleId();

			if (StringUtils.isEmpty(userId)){
				continue;
			}
			//年龄
			if (StringUtils.isNotEmpty(age)){
				String v = userAgeMap.get(userId);
				if (StringUtils.isEmpty(v)){
					userAgeMap.put(userId, age);
				}
			}
			//性别
			if (StringUtils.isNotEmpty(sex)){
				String v = userSexMap.get(userId);
				if (StringUtils.isEmpty(v)){
					userSexMap.put(userId, sex);
				}
			}
			if (StringUtils.isNotEmpty(localeId)){
				LocaleVo localeVo = localeVoMap.get(localeId);
				if (localeVo != null){
					// 城市
					String v = userCityMap.get(userId);
					if (StringUtils.isEmpty(v) && StringUtils.isNotEmpty(localeVo.getCity())){
						userCityMap.put(userId, localeVo.getCity());
					}
					// 点位
					String v1 = userPointMap.get(userId);
					if (StringUtils.isEmpty(v1) && StringUtils.isNotEmpty(localeVo.getPoint())){
						userPointMap.put(userId, localeVo.getPoint().replace("\"", ""));
					}
				}
			}
		}

		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
		Set<String> userAgeIds = userAgeMap.keySet();

		insertThread(userTag, userAgeMap, now);
		insertThread(inno72GameUserTagMapper.selectByCode(CODE_CITY), userCityMap, now);
		insertThread(inno72GameUserTagMapper.selectByCode(CODE_POINT), userPointMap, now);
		insertThread(inno72GameUserTagMapper.selectByCode(CODE_GENDER), userSexMap, now);

		fixedThreadPool.shutdown();

		// 购买力

		JobLogger.log("基础信息标签 job, end");
		return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
	}

	private void insertThread(Inno72GameUserTag userTag, Map<String, String> user, LocalDateTime now) {
		if (user == null || user.size() == 0) {
			return;
		}

		if (userTag == null) {
			return;
		}

		List<String> existUserIds = inno72GameUserTagRefMapper.selectUserIdsByTagIdAndUserId(userTag.getId(), user.keySet());
		List<Inno72GameUserTagRef> tags = new ArrayList<>();
		for (Map.Entry<String, String> u : user.entrySet()) {
			String key = u.getKey();
			if (!existUserIds.contains(key)) {
				tags.add(new Inno72GameUserTagRef(Uuid.genUuid(), key, u.getValue(), userTag.getName(), now));
			}
		}
		inno72GameUserTagRefMapper.insertS(tags);
	}

	@Override
	public void init() {
		
		
	}

	@Override
	public void destroy() {
		
		
	}
   
}
