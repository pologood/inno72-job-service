package com.inno72.job.vistor.count;

import lombok.Data;

@Data
public class MachineDataCount {

	private Object _id;
	/**
	 * 	String
	 * 	否	活动ID
	 */
	private String activityId;
	/**
	 * String
	 * 	否	日期	yyyy-MM-dd
	 */
	private String date;
	/**
	 * String
	 * 	否	机器Code
	 */
	private String machineCode;
	/**
	 *int
	 * 	否	总访客人数
	 */
	private Integer pv;
	/**
	 *int
	 * 	否	独立访客数
	 */
	private Integer uv;
	/**
	 *int
	 * 	否	订单量
	 */
	private Integer order;
	/**
	 *int
	 * 	否	出货量
	 */
	private Integer shipment;

	/**
	 * 新增粉丝量
	 */
	private Integer fans;

	/**
	 * 新增关注数量
	 */
	private Integer concern;

	/**
	 * 新增关注数量
	 */
	private Integer visitor;

}
