package com.inno72.job.task.util;

import java.util.UUID;

public class Uuid {
	public static String genUuid(){
		return UUID.randomUUID().toString().replace("-", "");
	}
}
