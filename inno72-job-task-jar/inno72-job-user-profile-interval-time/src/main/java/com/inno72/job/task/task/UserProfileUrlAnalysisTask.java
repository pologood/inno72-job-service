package com.inno72.job.task.task;

import com.alibaba.fastjson.JSON;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.task.mapper.Inno72GameUserLoginMapper;
import com.inno72.job.task.model.Inno72GameUserLogin;
import com.inno72.job.task.util.FaceIdentifyUtil;
import com.inno72.job.task.util.FastJsonUtils;
import com.inno72.job.task.vo.FaceV3DetectBean;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * url 解析 性别年龄
 */
@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.UserProfileUrlAnalysisTask")
public class UserProfileUrlAnalysisTask implements IJobHandler
{
	@Resource
	private Inno72GameUserLoginMapper inno72GameUserLoginMapper;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		JobLogger.log("url analysis job, start");

		List<Inno72GameUserLogin> gameUserLogins = inno72GameUserLoginMapper.findAllNoProcessed();
		for (Inno72GameUserLogin gameUserLogin : gameUserLogins) {
			detectFace(gameUserLogin);
		}

		JobLogger.log("url analysis job, end");
		return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
	}

	private String detectFace(Inno72GameUserLogin gameUserLogin) {
		// todo gxg 解析 url
		String url = gameUserLogin.getUrl();
		File file = new File("/Users/72cy-0101-01-0009/Documents/rl1.jpeg");
		String s = FaceIdentifyUtil.detectFace(file, "");
		String errorCode = FastJsonUtils.getString(s, "error_code");
		JobLogger.log("detectFace result is {}", s);
		if (!StringUtils.isEmpty(errorCode)) {
			JSON json = JSON.parseObject(s);
			FaceV3DetectBean bean = JSON.toJavaObject(json, FaceV3DetectBean.class);

			int age = bean.getResult().getFace_list().get(0).getAge();
			String sex = bean.getResult().getFace_list().get(0).getGender().getType();

			if (!StringUtils.isEmpty(age)) {
				gameUserLogin.setAge(String.valueOf(age));
			}
			if (!StringUtils.isEmpty(sex)) {
				if (sex.equals("male")) {
					sex = "男";
				} else if (sex.equals("female")) {
					sex = "女";
				}
			} else {
				sex = "未知";
			}
			gameUserLogin.setSex(sex);
			gameUserLogin.setProcessed(true);
			inno72GameUserLoginMapper.update(gameUserLogin);
		}
		return s;
	}

	@Override
	public void init() {
		
		
	}

	@Override
	public void destroy() {
		
		
	}
   
}
