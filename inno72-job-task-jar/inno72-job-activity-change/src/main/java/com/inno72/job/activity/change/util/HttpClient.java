package com.inno72.job.activity.change.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClient {
	private static Logger logger = LoggerFactory.getLogger(HttpClient.class);
	private static ConnectionPool pool = new ConnectionPool();
	private static OkHttpClient client;

	public HttpClient() {
	}

	public static void upload(String url, File file, Callback callback) {
		RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
		RequestBody requestBody = (new Builder()).setType(MultipartBody.FORM).addFormDataPart("media", file.getName(), fileBody).build();
		Request request = (new okhttp3.Request.Builder()).url(url).post(requestBody).build();
		client.newCall(request).enqueue(callback);
	}

	public static Object upload(String url, File file, HttpClient.Response_Type responseType) {
		RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
		RequestBody requestBody = (new Builder()).setType(MultipartBody.FORM).addFormDataPart("media", file.getName(), fileBody).build();
		Request request = (new okhttp3.Request.Builder()).url(url).post(requestBody).build();

		try {
			ResponseBody response = client.newCall(request).execute().body();
			return getResponse(response, responseType);
		} catch (IOException var7) {
			var7.printStackTrace();
			return null;
		}
	}

	public static byte[] getBytes(String url) {
		return (byte[])((byte[])get(url, HttpClient.Response_Type.Byte));
	}

	public static String get(String url, String encoding) {
		String result = get(url);

		try {
			result = new String(result.getBytes("ISO8859-1"), encoding);
		} catch (UnsupportedEncodingException var4) {
			var4.printStackTrace();
		}

		return result;
	}

	public static Object get(String url, HttpClient.Response_Type responseType) {
		Request request = (new okhttp3.Request.Builder()).url(url).build();
		ResponseBody responseBody = async(request);
		return getResponse(responseBody, responseType);
	}

	public static String get(String url) {
		return get(url, HttpClient.Response_Type.String).toString();
	}

	public static void get(String url, Callback callback) {
		Request request = (new okhttp3.Request.Builder()).url(url).build();
		sync(request, callback);
	}

	public static Object post(String url, MediaType mediaType, Object requestBody, HttpClient.Request_Type reqType, HttpClient.Response_Type responseType, Map<String, String> header) {
		RequestBody body = getRequest(reqType, requestBody, mediaType);
		okhttp3.Request.Builder builder = (new okhttp3.Request.Builder()).url(url).post(body);
		if (header != null) {
			header.forEach((k, v) -> {
				builder.addHeader(k, v);
			});
		}

		Request request = builder.build();
		ResponseBody responseBody = async(request);
		return getResponse(responseBody, responseType);
	}

	public static String post(String url, String requestBody) {
		return post(url, MediaType.parse("application/json; charset=utf-8"), requestBody, HttpClient.Request_Type.String, HttpClient.Response_Type.String, (Map)null).toString();
	}

	public static void post(String url, MediaType mediaType, Object requestBody, HttpClient.Request_Type reqType, HttpClient.Response_Type responseType, Map<String, String> header, Callback callback) {
		RequestBody body = getRequest(reqType, requestBody, mediaType);
		okhttp3.Request.Builder builder = (new okhttp3.Request.Builder()).url(url).post(body);
		if (header != null) {
			header.forEach((k, v) -> {
				builder.addHeader(k, v);
			});
		}

		Request request = builder.build();
		sync(request, callback);
	}

	public static void post(String url, Object requestBody, Callback callback) {
		RequestBody body = getRequest(HttpClient.Request_Type.String, requestBody, MediaType.parse("application/json; charset=utf-8"));
		okhttp3.Request.Builder builder = (new okhttp3.Request.Builder()).url(url).post(body);
		Request request = builder.build();
		sync(request, callback);
	}

	public static Object form(String url, Map<String, String> requestForm, HttpClient.Response_Type responseType, Map<String, String> header) {
		okhttp3.FormBody.Builder builder = new okhttp3.FormBody.Builder();
		requestForm.forEach((k, v) -> {
			builder.add(k, v);
		});
		FormBody formBody = builder.build();
		okhttp3.Request.Builder reqBuilder = (new okhttp3.Request.Builder()).url(url).post(formBody);
		if (header != null) {
			header.forEach((k, v) -> {
				reqBuilder.addHeader(k, v);
			});
		}

		Request request = reqBuilder.build();
		ResponseBody responseBody = async(request);
		return getResponse(responseBody, responseType);
	}

	public static String form(String url, Map<String, String> requestForm, Map<String, String> header) {
		okhttp3.FormBody.Builder builder = new okhttp3.FormBody.Builder();
		requestForm.forEach((k, v) -> {
			builder.add(k, v);
		});
		FormBody formBody = builder.build();
		okhttp3.Request.Builder reqBuilder = (new okhttp3.Request.Builder()).url(url).post(formBody);
		if (header != null) {
			header.forEach((k, v) -> {
				reqBuilder.addHeader(k, v);
			});
		}

		Request request = reqBuilder.build();
		ResponseBody responseBody = async(request);
		return getResponse(responseBody, HttpClient.Response_Type.String).toString();
	}

	public static void form(String url, Map<String, String> requestForm, Map<String, String> header, Callback callback) {
		okhttp3.FormBody.Builder builder = new okhttp3.FormBody.Builder();
		requestForm.forEach((k, v) -> {
			builder.add(k, v);
		});
		FormBody formBody = builder.build();
		okhttp3.Request.Builder reqBuilder = (new okhttp3.Request.Builder()).url(url).post(formBody);
		if (header != null) {
			header.forEach((k, v) -> {
				reqBuilder.addHeader(k, v);
			});
		}

		Request request = reqBuilder.build();
		sync(request, callback);
	}

	public static ResponseBody async(Request request) {
		logger.info("{}请求{}", request.method(), request.url());

		try {
			return client.newCall(request).execute().body();
		} catch (IOException var2) {
			var2.printStackTrace();
			return null;
		}
	}

	public static void sync(Request request, Callback callback) {
		client.newCall(request).enqueue(callback);
	}

	private static RequestBody getRequest(HttpClient.Request_Type reqType, Object requestBody, MediaType mediaType) {
		RequestBody body = null;
		switch(reqType) {
			case String:
				body = RequestBody.create(mediaType, (String)requestBody);
				break;
			case Json:
				body = RequestBody.create(mediaType, (String)requestBody);
				break;
			case File:
				body = RequestBody.create(mediaType, (File)requestBody);
		}

		return body;
	}

	public static Object getResponse(ResponseBody responseBody, HttpClient.Response_Type responseType) {
		Object result = null;
		switch(responseType) {
			case String:
				try {
					result = responseBody.string();
					logger.info("请求返回结果：{}", result);
				} catch (IOException var5) {
					var5.printStackTrace();
				}
				break;
			case Byte:
				try {
					result = responseBody.bytes();
					logger.info("请求返回byte数组");
				} catch (IOException var4) {
					var4.printStackTrace();
				}
				break;
			case ByteStream:
				result = responseBody.byteStream();
				logger.info("请求返回字节流");
				break;
			case CharStream:
				result = responseBody.charStream();
				logger.info("请求返回字符流");
		}

		return result;
	}

	static {
		client = (new okhttp3.OkHttpClient.Builder()).connectionPool(pool).build();
	}

	public static enum Request_Type {
		String,
		File,
		Json;

		private Request_Type() {
		}
	}

	public static enum Response_Type {
		String,
		ByteStream,
		Byte,
		CharStream;

		private Response_Type() {
		}
	}
}