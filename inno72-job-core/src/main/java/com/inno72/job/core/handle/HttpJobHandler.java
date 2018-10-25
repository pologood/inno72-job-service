package com.inno72.job.core.handle;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.core.util.HttpFormConnector;

@Component
public class HttpJobHandler implements IJobHandler {

	@Override
	public ReturnT<String> execute(String param) throws Exception {

		if (StringUtils.isBlank(param)) {

			JobLogger.log("URL Empty");

			return new ReturnT<String>(ReturnT.FAIL_CODE, "URL Empty");
		}

		try {

			byte[] resp = HttpFormConnector.doGet(param, null, 5000);

			JobLogger.log(new String(resp, "utf-8"));

			return new ReturnT<String>(ReturnT.SUCCESS_CODE, "ok");

		} catch (Exception e) {
			JobLogger.log(e);
			return new ReturnT<String>(ReturnT.FAIL_CODE, "fail");
		}
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}

}
