package com.inno72.job.executer.model;

import java.time.LocalDateTime;

public class Inno72MerchantTotalCountByDay {

	private String id;

	private String date;

	private String city;

	private String goodsId;

	/**
	 * 商品名称
	 */
	private String goodsName;

	/**
	 * 活动Id
	 */
	private String activityId;

	/**
	 * 活动Name
	 */
	private String activityName;

	/**
	 * 停留用户数
	 */
	private Integer stayNum;

	/**
	 * 商户ID主键
	 */
//	private String merchantAccountId;

	private String machineCode;

	/**
	 * 商家code -> inno72_merchant.merchant_code
	 */
	private String merchantCode;

	/**
	 * 商家Id
	 */
	private String merchantId;


	/**
	 * 订单总数（按商品）
	 */
	private Integer orderQtyTotal;

	/**
	 * 成功订单数
	 */
	private Integer orderQtySucc;

	/**
	 * 派发商品数量
	 */
	private Integer goodsNum;

	/**
	 * 优惠券发放数量
	 */
	private Integer couponNum;

	/**
	 * 关注用户数
	 */
	private Integer concernNum;

	private Integer pv;

	private Integer uv;

	/**
	 * 商户CODE
	 */
	private String sellerId;

	private LocalDateTime lastUpdateTime;

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
	 * @return date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return goods_id
	 */
	public String getGoodsId() {
		return goodsId;
	}

	/**
	 * @param goodsId
	 */
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	/**
	 * 获取商品名称
	 *
	 * @return goods_name - 商品名称
	 */
	public String getGoodsName() {
		return goodsName;
	}

	/**
	 * 设置商品名称
	 *
	 * @param goodsName 商品名称
	 */
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
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

	/**
	 * 获取商户ID主键
	 *
	 * @return merchant_id - 商户ID主键
	 */
	public String getMerchantId() {
		return merchantId;
	}

	/**
	 * 设置商户ID主键
	 *
	 * @param merchantId 商户ID主键
	 */
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	/**
	 * 获取订单总数（按商品）
	 *
	 * @return order_qty_total - 订单总数（按商品）
	 */
	public Integer getOrderQtyTotal() {
		return orderQtyTotal;
	}

	/**
	 * 设置订单总数（按商品）
	 *
	 * @param orderQtyTotal 订单总数（按商品）
	 */
	public void setOrderQtyTotal(Integer orderQtyTotal) {
		this.orderQtyTotal = orderQtyTotal;
	}

	/**
	 * 获取成功订单数
	 *
	 * @return order_qty_succ - 成功订单数
	 */
	public Integer getOrderQtySucc() {
		return orderQtySucc;
	}

	/**
	 * 设置成功订单数
	 *
	 * @param orderQtySucc 成功订单数
	 */
	public void setOrderQtySucc(Integer orderQtySucc) {
		this.orderQtySucc = orderQtySucc;
	}

	/**
	 * 获取派发商品数量
	 *
	 * @return goods_num - 派发商品数量
	 */
	public Integer getGoodsNum() {
		return goodsNum;
	}

	/**
	 * 设置派发商品数量
	 *
	 * @param goodsNum 派发商品数量
	 */
	public void setGoodsNum(Integer goodsNum) {
		this.goodsNum = goodsNum;
	}

	/**
	 * 获取优惠券发放数量
	 *
	 * @return coupon_num - 优惠券发放数量
	 */
	public Integer getCouponNum() {
		return couponNum;
	}

	/**
	 * 设置优惠券发放数量
	 *
	 * @param couponNum 优惠券发放数量
	 */
	public void setCouponNum(Integer couponNum) {
		this.couponNum = couponNum;
	}

	/**
	 * 获取关注用户数
	 *
	 * @return concern_num - 关注用户数
	 */
	public Integer getConcernNum() {
		return concernNum;
	}

	/**
	 * 设置关注用户数
	 *
	 * @param concernNum 关注用户数
	 */
	public void setConcernNum(Integer concernNum) {
		this.concernNum = concernNum;
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
	 * 获取商户CODE
	 *
	 * @return seller_id - 商户CODE
	 */
	public String getSellerId() {
		return sellerId;
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

	/**
	 * 设置商户CODE
	 *
	 * @param sellerId 商户CODE
	 */
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public LocalDateTime getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Inno72MerchantTotalCountByDay(String id, String date, String city, String goodsId, String goodsName,
			String merchantId, Integer orderQtyTotal, Integer orderQtySucc, Integer goodsNum, Integer couponNum,
			Integer concernNum, Integer pv, Integer uv, String sellerId, LocalDateTime lastUpdateTime,
			String activityId, String activityName, Integer stayNum, String merchantName, String channelMerchantId,
			String channelId, String channelName, String machineCode) {
		this.id = id;
		this.date = date;
		this.city = city;
		this.goodsName = goodsName;
		this.merchantId = merchantId;
		this.goodsId = goodsId;
		this.orderQtySucc = orderQtySucc;
		this.orderQtyTotal = orderQtyTotal;
		this.goodsNum = goodsNum;
		this.couponNum = couponNum;
		this.concernNum = concernNum;
		this.pv = pv;
		this.uv = uv;
		this.sellerId = sellerId;
		this.lastUpdateTime = lastUpdateTime;
		this.activityId = activityId;
		this.activityName = activityName;
		this.stayNum = stayNum;
		this.merchantName = merchantName;
		this.channelMerchantId = channelMerchantId;
		this.channelId = channelId;
		this.channelName = channelName;
		this.machineCode = machineCode;
	}

	public Inno72MerchantTotalCountByDay() {
	}

	public Integer getStayNum() {
		return stayNum;
	}

	public void setStayNum(Integer stayNum) {
		this.stayNum = stayNum;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

}