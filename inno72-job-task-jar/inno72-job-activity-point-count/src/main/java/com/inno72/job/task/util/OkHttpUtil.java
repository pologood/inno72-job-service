package com.inno72.job.task.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {

	private static Logger logger = LoggerFactory.getLogger(OkHttpUtil.class);

	private static OkHttpClient client = null;

	// public static final MediaType FORM_URLENCODED
	//   = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

	public static String doGet(String url) {

		OkHttpClient client = getClient();


		Request request = new Request.Builder().url(url)
				.get()
				.build();

		Response response = null;

		try {
			response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				ResponseBody responseBody = response.body();
				if (responseBody != null) {
					return responseBody.string();
				}
			}
		} catch (IOException e) {
			logger.warn(e.getMessage(), "网络异常");
		} finally {
			if (response != null) {
				response.close();
			}
		}

		return "";
	}
	public static OkHttpClient getClient() {
		if (client == null) {
			client = new OkHttpClient.Builder()
					.retryOnConnectionFailure(true)
					.connectTimeout(12, TimeUnit.SECONDS)
					.readTimeout(12, TimeUnit.SECONDS)
					.build();
		}
		return client;
	}
}