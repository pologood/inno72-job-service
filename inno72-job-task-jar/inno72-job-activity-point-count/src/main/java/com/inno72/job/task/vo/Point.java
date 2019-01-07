package com.inno72.job.task.vo;

import lombok.Data;

@Data
public class Point {
	/**
	 * 序号
	 */
	private String seq;
	/**
	 * 页面名称
	 */
	private String pageName;
	/**
	 * 页面编码
	 */
	private String pageCode;
	/**
	 * type
	 */
	private String type;
	/**
	 * 页面描述
	 */
	private String desc;
	/**
	 * 数量
	 */
	private Integer num;
}
