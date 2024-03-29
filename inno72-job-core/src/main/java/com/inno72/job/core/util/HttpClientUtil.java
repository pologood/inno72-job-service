package com.inno72.job.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpClientUtil {
	private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

	private static final int TIME_OUT = 1000000;
	/**
	 * post request
	 */
	public static byte[] postRequest(String reqURL, byte[] data) throws Exception {
		byte[] responseBytes = null;
		
		HttpPost httpPost = new HttpPost(reqURL);
		CloseableHttpClient httpClient = HttpClients.custom().disableAutomaticRetries().build();	// disable retry

		try {
			
			// timeout
			RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(TIME_OUT)
                    .setSocketTimeout(TIME_OUT)
                    .setConnectTimeout(TIME_OUT)
                    .build();

			httpPost.setConfig(requestConfig);

			// data
			if (data != null) {
				httpPost.setEntity(new ByteArrayEntity(data, ContentType.DEFAULT_BINARY));
			}
			// do post
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				responseBytes = EntityUtils.toByteArray(entity);
				EntityUtils.consume(entity);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			httpPost.releaseConnection();
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return responseBytes;
	}
	
	/**
	 * read bytes from http request
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	public static final byte[] readBytes(HttpServletRequest request) throws IOException {
		request.setCharacterEncoding("UTF-8");
        int contentLen = request.getContentLength();
		InputStream is = request.getInputStream();
		if (contentLen > 0) {
			int readLen = 0;
			int readLengthThisTime = 0;
			byte[] message = new byte[contentLen];
			try {
				while (readLen != contentLen) {
					readLengthThisTime = is.read(message, readLen, contentLen - readLen);
					if (readLengthThisTime == -1) {
						break;
					}
					readLen += readLengthThisTime;
				}
				return message;
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
		}
		return new byte[] {};
	}
	
	public static String makeHttpForm(Map<String, String> form) throws IOException{
		
		if(form == null){
			return "";
		}
		
		List<String> keys = new ArrayList<String>(form.keySet());

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = form.get(key);
            if(StringUtils.isEmpty(key)){
            	continue;
            }
            if(value == null){
            	continue;
            }
            if (i == keys.size() - 1) {
                prestr +=  URLEncoder.encode(key, "UTF-8")  + "=" + URLEncoder.encode(value, "UTF-8");
            } else {
                prestr +=  URLEncoder.encode(key, "UTF-8")  + "=" + URLEncoder.encode(value, "UTF-8") + "&";
            }
        }

        return prestr; 
	}

}
