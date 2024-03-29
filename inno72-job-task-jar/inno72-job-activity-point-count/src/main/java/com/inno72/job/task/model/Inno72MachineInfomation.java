package com.inno72.job.task.model;

public class Inno72MachineInfomation {

	/** sessionUuid */
	private String sessionUuid;
	/** traceId */
	private String traceId;

	/** 活动ID */
	private String activityId;

	/** 活动名称 */
	private String activityName;

	/** 机器ID */
	private String machineCode;

	/** 省 */
	private String provence;

	/** 市 */
	private String city;

	/** 区 */
	private String district;

	/** 点位 */
	private String point;

	/** userID */
	private String userId;

	/** 用户昵称 */
	private String nickName;

	/** 时间1(客户端传入时间) */
	private String clientTime;

	/** 时间2(到达服务器时间) */
	private String serviceTime;

	/** 行为 - (登录:  、关注:002 、入会:003  、、、、 */
	private String type;

	/** 品牌ID(seller_id) */
	private String sellerId;

	/** 品牌名称 */
	private String sellerName;

	/** 商品ID(商品code) */
	private String goodsId;

	/** 商品名称 */
	private String goodsName;

	/** playCode */
	private String playCode;

	/** 游戏难度 */
	private String playDifficulty;

	/** 游戏结果 */
	private String playResult;

	/** 订单号  */
	private String orderId;

	/** 三方订单号 */
	private String refOrderId;

	/** 三方订单状态 */
	private String refOrderStatus;

	/** 货道 */
	private String channel;

	/** 出货数量 */
	private String shipmentNum;

	/** 描述 ex: 点击进入游戏 or 跳转位置 等等 */

	private String desc;

	/** 扫码路径 ex:https://。。。。。 */

	private String scanUrl;

	/** pageCode */
	private String pageCode;

	/** pageName */
	private String pageName;

	/** 计划ID */
	private String planId;
	/** 奖池ID */
	private String interactId;

	private String clickType;

	private String requestId;

	public String getSessionUuid() {
		return sessionUuid;
	}

	public void setSessionUuid(String sessionUuid) {
		this.sessionUuid = sessionUuid;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getMachineCode() {
		return machineCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	public String getProvence() {
		return provence;
	}

	public void setProvence(String provence) {
		this.provence = provence;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getClientTime() {
		return clientTime;
	}

	public void setClientTime(String clientTime) {
		this.clientTime = clientTime;
	}

	public String getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(String serviceTime) {
		this.serviceTime = serviceTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getPlayCode() {
		return playCode;
	}

	public void setPlayCode(String playCode) {
		this.playCode = playCode;
	}

	public String getPlayDifficulty() {
		return playDifficulty;
	}

	public void setPlayDifficulty(String playDifficulty) {
		this.playDifficulty = playDifficulty;
	}

	public String getPlayResult() {
		return playResult;
	}

	public void setPlayResult(String playResult) {
		this.playResult = playResult;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getRefOrderId() {
		return refOrderId;
	}

	public void setRefOrderId(String refOrderId) {
		this.refOrderId = refOrderId;
	}

	public String getRefOrderStatus() {
		return refOrderStatus;
	}

	public void setRefOrderStatus(String refOrderStatus) {
		this.refOrderStatus = refOrderStatus;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getShipmentNum() {
		return shipmentNum;
	}

	public void setShipmentNum(String shipmentNum) {
		this.shipmentNum = shipmentNum;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getScanUrl() {
		return scanUrl;
	}

	public void setScanUrl(String scanUrl) {
		this.scanUrl = scanUrl;
	}

	public String getPageCode() {
		return pageCode;
	}

	public void setPageCode(String pageCode) {
		this.pageCode = pageCode;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getInteractId() {
		return interactId;
	}

	public void setInteractId(String interactId) {
		this.interactId = interactId;
	}

	public String getClickType() {
		return clickType;
	}

	public void setClickType(String clickType) {
		this.clickType = clickType;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	

}
