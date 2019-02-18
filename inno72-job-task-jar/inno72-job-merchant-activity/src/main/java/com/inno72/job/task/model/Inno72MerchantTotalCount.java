package com.inno72.job.task.model;

import java.time.LocalDateTime;
import java.util.Optional;

public class Inno72MerchantTotalCount {
	private String id;

	private String activityName;

	private String activityId;

	private String activityStatus;

	/**
	 * 活动类型 1: 常规活动； 2: 互派活动
	 */
	private String activityType;

	/**
	 * 机器数量
	 */
	private Integer machineNum;

	/**
	 * 客流量
	 */
	private Integer visitorNum;

	/**
	 * 停留用户数(最多的点击数量)
	 */
	private Integer stayUser;

	private Integer pv;

	private Integer uv;

	private Integer order;

	private Integer shipment;

	private String merchantId;

	/**
	 * 购买人数
	 */
	private Integer buyer;

	private LocalDateTime lastUpdateTime;

	public Inno72MerchantTotalCount() {
	}

	public Inno72MerchantTotalCount(String activityName, String activityId, String activityStatus, Integer machineNum,
			Integer visitorNum, Integer stayuser, Integer pv, Integer uv, Integer order, Integer shipment,
			String merchantId, Integer buyer, String activityType) {
		this.activityName = activityName;
		this.activityId = activityId;
		this.activityStatus = activityStatus;
		this.machineNum = machineNum;
		this.visitorNum = Optional.ofNullable(visitorNum).orElse(0);
		this.stayUser = Optional.ofNullable(stayuser).orElse(0);
		this.pv = Optional.ofNullable(pv).orElse(0);
		this.uv = Optional.ofNullable(uv).orElse(0);
		this.order = Optional.ofNullable(order).orElse(0);
		this.shipment = Optional.ofNullable(shipment).orElse(0);
		this.merchantId = merchantId;
		this.buyer = Optional.ofNullable(buyer).orElse(0);
		this.activityType = activityType;
	}

	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return activity_name
	 */
	public String getActivityName() {
		return activityName;
	}

	/**
	 * @param activityName
	 */
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	/**
	 * @return activity_id
	 */
	public String getActivityId() {
		return activityId;
	}

	/**
	 * @param activityId
	 */
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	/**
	 * @return activity_status
	 */
	public String getActivityStatus() {
		return activityStatus;
	}

	/**
	 * @param activityStatus
	 */
	public void setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
	}

	/**
	 * 获取机器数量
	 *
	 * @return machine_num - 机器数量
	 */
	public Integer getMachineNum() {
		return machineNum;
	}

	/**
	 * 设置机器数量
	 *
	 * @param machineNum 机器数量
	 */
	public void setMachineNum(Integer machineNum) {
		this.machineNum = machineNum;
	}

	/**
	 * 获取客流量
	 *
	 * @return visitor_num - 客流量
	 */
	public Integer getVisitorNum() {
		return visitorNum;
	}

	/**
	 * 设置客流量
	 *
	 * @param visitorNum 客流量
	 */
	public void setVisitorNum(Integer visitorNum) {
		this.visitorNum = Optional.ofNullable(visitorNum).orElse(0);
	}

	/**
	 * 获取停留用户数(最多的点击数量)
	 *
	 * @return stayUser - 停留用户数(最多的点击数量)
	 */
	public Integer getStayUser() {
		return stayUser;
	}

	/**
	 * 设置停留用户数(最多的点击数量)
	 *
	 * @param stayUser 停留用户数(最多的点击数量)
	 */
	public void setStayUser(Integer stayUser) {
		this.stayUser = Optional.ofNullable(stayUser).orElse(0);
	}

	/**
	 * @return pv
	 */
	public Integer getPv() {
		return pv;
	}

	/**
	 * @param pv
	 */
	public void setPv(Integer pv) {
		this.pv = Optional.ofNullable(pv).orElse(0);;
	}

	/**
	 * @return uv
	 */
	public Integer getUv() {
		return uv;
	}

	/**
	 * @param uv
	 */
	public void setUv(Integer uv) {
		this.uv = Optional.ofNullable(uv).orElse(0);;
	}

	/**
	 * @return order
	 */
	public Integer getOrder() {
		return order;
	}

	/**
	 * @param order
	 */
	public void setOrder(Integer order) {
		this.order = Optional.ofNullable(order).orElse(0);;
	}

	/**
	 * @return shipment
	 */
	public Integer getShipment() {
		return shipment;
	}

	/**
	 * @param shipment
	 */
	public void setShipment(Integer shipment) {
		this.shipment = Optional.ofNullable(shipment).orElse(0);;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public Integer getBuyer() {
		return buyer;
	}

	public void setBuyer(Integer buyer) {
		this.buyer = Optional.ofNullable(buyer).orElse(0);;
	}

	public LocalDateTime getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}
}