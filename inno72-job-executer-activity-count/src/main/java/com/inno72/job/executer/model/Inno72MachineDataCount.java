package com.inno72.job.executer.model;

import javax.persistence.Id;

import lombok.Data;

@Data
public class Inno72MachineDataCount {

	@Id
	private String id;
	/**
	 * 	String
	 * 	否	活动ID
	 */
	private String activityId;


	private String activityName;
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
	 * 点位
	 */
	private String point;
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

	private Integer visitor;

	public Inno72MachineDataCount() {
	}
	public Inno72MachineDataCount(String id) {
		this.id = id;
	}

	public Inno72MachineDataCount(String id, String activityId, String activityName, String date, String machineCode, String point,
			Integer pv, Integer uv, Integer order, Integer shipment, Integer fans, Integer visitor, Integer concern) {
		this.id = id;
		this.activityId = activityId;
		this.activityName = activityName;
		this.date = date;
		this.machineCode = machineCode;
		this.point = point;
		this.pv = pv;
		this.uv = uv;
		this.order = order;
		this.shipment = shipment;
		this.fans = fans;
		this.visitor = visitor;
		this.concern = concern;
	}
}