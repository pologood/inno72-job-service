package com.inno72.job.executer.model;

import java.math.BigDecimal;
import java.util.Date;

public class OrderModel {

	private String id;

	private String orderNum;


	private String userId;


	private String channelId;


	private String machineId;


	private String shopsId;


	private String shopsName;

	private String merchantId;

	private String accessToken;


	private String inno72ActivityId;


	private String inno72ActivityPlanId;


	private Date orderTime;


	private BigDecimal orderPrice;


	private BigDecimal payPrice;


	private Integer orderType;


	private Integer payStatus;

	private Integer goodsType;

	private Integer goodsStatus;

	private Date payTime;

	private String refOrderStatus;


	private String refOrderId;


	private Integer repetition;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getOrderNum() {
		return orderNum;
	}


	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getChannelId() {
		return channelId;
	}


	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}


	public String getMachineId() {
		return machineId;
	}


	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}


	public String getShopsId() {
		return shopsId;
	}


	public void setShopsId(String shopsId) {
		this.shopsId = shopsId;
	}


	public String getShopsName() {
		return shopsName;
	}


	public void setShopsName(String shopsName) {
		this.shopsName = shopsName;
	}


	public String getMerchantId() {
		return merchantId;
	}


	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}


	public String getAccessToken() {
		return accessToken;
	}


	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}


	public String getInno72ActivityId() {
		return inno72ActivityId;
	}


	public void setInno72ActivityId(String inno72ActivityId) {
		this.inno72ActivityId = inno72ActivityId;
	}


	public String getInno72ActivityPlanId() {
		return inno72ActivityPlanId;
	}


	public void setInno72ActivityPlanId(String inno72ActivityPlanId) {
		this.inno72ActivityPlanId = inno72ActivityPlanId;
	}


	public Date getOrderTime() {
		return orderTime;
	}


	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}


	public BigDecimal getOrderPrice() {
		return orderPrice;
	}


	public void setOrderPrice(BigDecimal orderPrice) {
		this.orderPrice = orderPrice;
	}


	public BigDecimal getPayPrice() {
		return payPrice;
	}


	public void setPayPrice(BigDecimal payPrice) {
		this.payPrice = payPrice;
	}


	public Integer getOrderType() {
		return orderType;
	}


	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}


	public Integer getPayStatus() {
		return payStatus;
	}


	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}


	public Integer getGoodsType() {
		return goodsType;
	}


	public void setGoodsType(Integer goodsType) {
		this.goodsType = goodsType;
	}


	public Integer getGoodsStatus() {
		return goodsStatus;
	}


	public void setGoodsStatus(Integer goodsStatus) {
		this.goodsStatus = goodsStatus;
	}


	public Date getPayTime() {
		return payTime;
	}


	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}


	public String getRefOrderStatus() {
		return refOrderStatus;
	}


	public void setRefOrderStatus(String refOrderStatus) {
		this.refOrderStatus = refOrderStatus;
	}


	public String getRefOrderId() {
		return refOrderId;
	}


	public void setRefOrderId(String refOrderId) {
		this.refOrderId = refOrderId;
	}


	public Integer getRepetition() {
		return repetition;
	}


	public void setRepetition(Integer repetition) {
		this.repetition = repetition;
	}
	
	
	
}
