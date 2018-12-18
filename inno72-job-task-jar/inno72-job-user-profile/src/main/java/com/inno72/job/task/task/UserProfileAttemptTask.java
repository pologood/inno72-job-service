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
@JobHandler("inno72.task.UserProfileAttemptTask")
public class UserProfileAttemptTask implements IJobHandler
{

	@Resource
	private Inno72GameUserTagMapper inno72GameUserTagMapper;

	@Resource
	private Inno72GameUserTagRefMapper inno72GameUserTagRefMapper;

	@Resource
	private Inno72GameUserLifeMapper inno72GameUserLifeMapper;

	private static final String CODE_ATTEMPT = "attempt";

	@Override
	public ReturnT<String> execute(String param) {
		JobLogger.log("尝鲜族 job, start");

		//尝鲜族
		Inno72GameUserTag userTag = inno72GameUserTagMapper.selectByCode(CODE_ATTEMPT);
		if (userTag == null){
			return new ReturnT<>(ReturnT.SUCCESS_CODE, "未找到需要处理的标签");
		}

		LocalDateTime startTimeLocal = userTag.getUpdateTime();
		if ( startTimeLocal == null ){
			String lifeStartTime = inno72GameUserLifeMapper.selectMinDateFromLife();
			if (StringUtils.isEmpty(lifeStartTime)){
				JobLogger.log("未找到需要处理的数据");
				return new ReturnT<>(ReturnT.SUCCESS_CODE, "未找到需要处理的数据");

			}
			startTimeLocal = LocalDateTime.parse(lifeStartTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") );
		}

		LocalDateTime endTimeLocal = LocalDateTime.now();

		if (startTimeLocal.isAfter(endTimeLocal)){
			return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
		}

		Map<String, String> machineCodesParam = new HashMap<>();
		machineCodesParam.put("startTime", startTimeLocal.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		machineCodesParam.put("endTime",  endTimeLocal.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		List<String> machineCodes = inno72GameUserLifeMapper.selectMachineByLoginTime(machineCodesParam);
		if (machineCodes.size() == 0){
			return new ReturnT<>(ReturnT.SUCCESS_CODE, JSON.toJSONString(machineCodesParam) + "时间段内，没有需要统计的机器");
		}

		Set<String> attempt = new HashSet<>();
		for (String machineCode : machineCodes){
			List<Inno72GameUserLife> lives = inno72GameUserLifeMapper.selectLifeByLoginTime(machineCodesParam);
			Map<String, Integer> usersMap = new HashMap<>();

			LocalDateTime shipment = null;
			for ( Inno72GameUserLife life : lives ){
				String gameUserId = life.getGameUserId();
				LocalDateTime loginTime = life.getLoginTime();

				if (StringUtils.isEmpty(gameUserId)){
					continue;
				}
				shipment = Optional.ofNullable(shipment).orElse(life.getShipmentTime());
				if (shipment == null){
					continue;
				}

				long minutes = Duration.between(shipment, loginTime).toMinutes();
				if (minutes > 15){
					attempt.add(gameUserId);
				}
			}
		}

		if (attempt.size() > 0){
			List<Inno72GameUserTagRef> refsAttempt = new ArrayList<>(attempt.size());
			List<String> list = inno72GameUserTagRefMapper.selectUserIdsByTagIdAndUserId(userTag.getId(), attempt);
			for (String userId : attempt){
				if (!list.contains(userId)){
					refsAttempt.add(new Inno72GameUserTagRef(Uuid.genUuid(), userId, userTag.getId(), "尝鲜族", endTimeLocal));
				}
			}
			int i = inno72GameUserTagRefMapper.insertS(refsAttempt);
			JobLogger.log("尝鲜族 job, 插入尝鲜族用户ID集合"+JSON.toJSONString(attempt)+"关联,共 【"+i+"】条");
		}

		userTag.setUpdateTime(endTimeLocal);
		userTag.setUpdateId("UserProfileAttemptTask.java");

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
