package com.inno72.job.executer.model;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "inno72_merchant_total_count")
public class Inno72MerchantTotalCount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT REPLACE(UUID(),'-','')")
	private String id;

	@Column(name = "activity_name")
	private String activityName;

	@Column(name = "activity_id")
	private String activityId;

	@Column(name = "activity_status")
	private String activityStatus;

	/**
	 * 活动类型 1: 常规活动； 2: 互派活动
	 */
	@Column(name = "activity_type")
	private String activityType;

	/**
	 * 机器数量
	 */
	@Column(name = "machine_num")
	private Integer machineNum;

	/**
	 * 客流量
	 */
	@Column(name = "visitor_num")
	private Integer visitorNum;

	/**
	 * 停留用户数(最多的点击数量)
	 */
	@Column(name = "stay_user")
	private Integer stayUser;

	private Integer pv;

	private Integer uv;

	private Integer order;

	private Integer shipment;

	@Column(name = "merchant_id")
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
		this.visitorNum = visitorNum;
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
		this.stayUser = stayUser;
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
		this.pv = pv;
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
		this.uv = uv;
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
		this.order = order;
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
		this.shipment = shipment;
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
		this.buyer = buyer;
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