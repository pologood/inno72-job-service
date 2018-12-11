package com.inno72.job.executer.model;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class Inno72MachineInformation {

	private String _id;

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
	@NotNull(message = "消息类型不能为空!")
	@Length(max = 6, min = 6, message = "非法类型")
	private String type;

	public static enum ENUM_INNO72_MACHINE_INFORMATION_TYPE{
		LOGIN("001001","登录"),
		CONCERN("002001","关注"),
		MEMBERSHIP("003001","入会"),
		CLICK("004","点击"),// 客户端自由定义
		GAME_START("005001","游戏开始"),
		GAME_OVER("006001","游戏结束"),
		ORDER_GOODS("007001","下单-商品"),
		ORDER_COUPON("007002","下单-优惠券"),
		SHIPMENT("008001","出货"),
		SCAN_LOGIN("009001","扫码"),
		SCAN_PAY("009002","扫码"),
		JUMP("010001","跳转"),
		PAY("011002 ","订单支付"),
		PRODUCT_CLICK("100100","商品点击"),
		;
		private String type;
		private String desc;

		ENUM_INNO72_MACHINE_INFORMATION_TYPE(String type, String desc) {
			this.type = type;
			this.desc = desc;
		}

		public String getType() {
			return type;
		}

		public String getDesc() {
			return desc;
		}
	}

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

	/**
	 * 商户总ID - table -> inno72_merchant_user.merchant_id
	 */
	private String merchantId;// TODO 新

	/**
	 * 商户总名称 - table -> inno72_merchant_user.merchant_name
	 */
	private String merchantName;// TODO 新

	/**
	 * 渠道商家ID - table -> inno72_merchant.id
	 */
	private String channelMerchantId;//TODO 新
	/**
	 * 渠道ID - table -> inno72_merchant.channel_id
	 */
	private String channelId;//TODO 新
	/**
	 * 渠道名称 - table -> inno72_merchant.channel_name
	 */
	private String channelName;//TODO 新

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getChannelMerchantId() {
		return channelMerchantId;
	}

	public void setChannelMerchantId(String channelMerchantId) {
		this.channelMerchantId = channelMerchantId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

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
