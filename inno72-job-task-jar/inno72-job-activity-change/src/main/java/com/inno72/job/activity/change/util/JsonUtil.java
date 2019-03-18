package com.inno72.job.activity.change.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;

public class JsonUtil {
	public JsonUtil() {
	}

	public static <T> T toObj(byte[] bts, Class<T> clazz) {
		ObjectMapper objMapper = new ObjectMapper();
		Object t = null;

		try {
			t = objMapper.readValue(bts, clazz);
		} catch (IOException var5) {
			var5.printStackTrace();
		}

		return (T) t;
	}

	public static String toJson(Object object) {
		return toJson(object, "yyyy-MM-dd HH:mm:ss");
	}

	public static <T> T toObject(String json, Class<T> clazz) {
		return JSON.parseObject(json, clazz);
	}

	public static <T> List<T> toArray(String json, Class<T> clazz) {
		return JSON.parseArray(json, clazz);
	}

	public static String toJson(Object object, String dateFormat) {
		return JSON.toJSONStringWithDateFormat(object, dateFormat, new SerializerFeature[]{SerializerFeature.WriteDateUseDateFormat});
	}
}