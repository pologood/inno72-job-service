package com.inno72.job.vistor.count;


public class PointLog {

	/**
	 * 机器CODE {@link } -> instanceName
	 *
	 */
	private String machineCode;
	/**
	 * 日志类型
	 */
	private String type;
	/**
	 * 埋点时间{@link } -> time
	 */
	private String pointTime;
	/**
	 * 标记{@link } -> tag
	 */
	private String tag;
	/**
	 * 详情描述{@link } -> detail
	 */
	private String detail;

	public String getMachineCode() {
		return machineCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPointTime() {
		return pointTime;
	}

	public void setPointTime(String pointTime) {
		this.pointTime = pointTime;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}


}
