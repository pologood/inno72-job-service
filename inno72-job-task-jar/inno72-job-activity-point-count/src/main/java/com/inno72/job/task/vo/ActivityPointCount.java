package com.inno72.job.task.vo;

import java.util.Map;

import lombok.Data;

@Data
public class ActivityPointCount {
	private String activityId;
	private String lastUpdateTime;
	private Map<String, Integer> pointCount;
}
