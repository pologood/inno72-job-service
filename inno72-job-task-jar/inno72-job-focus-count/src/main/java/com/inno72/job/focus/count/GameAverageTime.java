package com.inno72.job.focus.count;

public class GameAverageTime {

	private String ActivityId;

	private String machineCode;

	private String traceId;

	private String maxTime;

	private String minTime;

	public String getActivityId() {
		return ActivityId;
	}

	public void setActivityId(String activityId) {
		ActivityId = activityId;
	}

	public String getMachineCode() {
		return machineCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public String getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(String maxTime) {
		this.maxTime = maxTime;
	}

	public String getMinTime() {
		return minTime;
	}

	public void setMinTime(String minTime) {
		this.minTime = minTime;
	}


	public String getHashKey() {
		return traceId;
	}


}
