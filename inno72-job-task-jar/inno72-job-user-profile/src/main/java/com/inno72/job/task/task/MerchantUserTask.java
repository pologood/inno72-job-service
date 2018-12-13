package com.inno72.job.task.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.task.mapper.Inno72GameUserLoginMapper;
import com.inno72.job.task.mapper.Inno72GameUserTagMapper;
import com.inno72.job.task.mapper.Inno72GameUserTagRefMapper;
import com.inno72.job.task.mapper.Inno72MerchantTotalCountByUserMapper;
import com.inno72.job.task.mapper.LocaleMapper;
import com.inno72.job.task.model.Inno72GameUserLogin;
import com.inno72.job.task.model.Inno72MerchantTotalCountByUser;
import com.inno72.job.task.util.Uuid;
import com.inno72.job.task.vo.Inno72GameUserLoginVo;
import com.inno72.job.task.vo.Inno72GameUserTagRefVo;


@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.MerchantUserTask")
public class MerchantUserTask implements IJobHandler {

	@Resource
	private Inno72GameUserLoginMapper inno72GameUserLoginMapper;

	@Resource
	private LocaleMapper localeMapper;

	@Resource
	private Inno72GameUserTagMapper inno72GameUserTagMapper;

	@Resource
	private Inno72GameUserTagRefMapper inno72GameUserTagRefMapper;

	@Resource
	private Inno72MerchantTotalCountByUserMapper inno72MerchantTotalCountByUserMapper;

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

		Inno72MerchantTotalCountByUser byUser = inno72MerchantTotalCountByUserMapper.selectLastTime();
		LocalDateTime startTimeLocal;
		if (byUser == null){
			Inno72GameUserLogin minTime = inno72GameUserLoginMapper.findMinTime();
			if (minTime == null){
				return new ReturnT<>(ReturnT.SUCCESS_CODE, "未找到需要处理的数据");
			}
			startTimeLocal = minTime.getLoginTime();
		}else {
			startTimeLocal = byUser.getLastUpdateTime();
		}

		LocalDateTime endTimeLocal = LocalDateTime.now();

		if (startTimeLocal.isAfter(endTimeLocal)){
			return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
		}
		Map<String, String> params = new HashMap<>();
		while (true) {

			LocalDateTime plusDays = startTimeLocal.plusDays(1);

			long days = Duration.between(startTimeLocal, endTimeLocal).toDays();
			if (days <= 0) {
				break;
			}
			if (days < 1) {
				plusDays = endTimeLocal;
			}
			params.put("startTime", startTimeLocal.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			params.put("endTime",  plusDays.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			JobLogger.log("执行线程 - 参数 " + JSON.toJSONString(params));

			List<Inno72GameUserLoginVo> userLogins =
					inno72GameUserLoginMapper.selectByTime(params);

			if (userLogins.size() == 0){
				JobLogger.log("查询参数 - "+ JSON.toJSONString(params) +"结果为空");
				startTimeLocal = plusDays;
				continue;
			}
			List<Inno72MerchantTotalCountByUser> users = new ArrayList<>();
			for (Inno72GameUserLoginVo login : userLogins){

				String activityId = login.getActivityId();
				String userId = login.getUserId();
				String loginTime = login.getLoginTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

				if (StringUtils.isEmpty(userId)){
					continue;
				}

				Map<String, String> refParam = new HashMap<>();
				refParam.put("actId", activityId);
				refParam.put("userId", userId);
				List<Inno72GameUserTagRefVo> refs = inno72GameUserTagRefMapper.selectByActIdAndUserId(refParam);

				/**
				 * @param id -
				 * @param activityId -
				 * @param activityName -
				 * @param merchantId -
				 * @param date -
				 * @param sellerId -
				 * @param age 0
				 * @param sex 0
				 * @param userTag 0
				 * @param pointTag 0
				 * @param city 0
				 * @param lastUpdateTime -
				 */

				Integer age = null;
				String gender = "";
				String city = "";
				String point = "";
				List<String> tags = new ArrayList<>();

				for (Inno72GameUserTagRefVo vo : refs){

					String code = vo.getCode();

					switch (code){
						case CODE_AGE:
							age = Integer.parseInt(vo.getContent());
							break;

						case CODE_GENDER:
							gender = vo.getContent();
							break;

						case CODE_CITY:
							city = vo.getContent();
							break;

						case CODE_POINT:
							point = vo.getContent();
							break;

						default:
							tags.add(vo.getContent());

					}

				}

				users.add(new Inno72MerchantTotalCountByUser(Uuid.genUuid(), login.getActivityId(), login.getActivityName(),
						"", loginTime, userId, age, gender, JSON.toJSONString(tags), point, city, endTimeLocal));

			}
			if (users.size() > 0){
				inno72MerchantTotalCountByUserMapper.insertS(users);
			}


			startTimeLocal = plusDays;
		}

		return  new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");

	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
