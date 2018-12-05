package com.inno72.job.executer.vo;

import lombok.Data;

@Data
public class OrderVo {
	/**
	 * 商户ID
	 */
	private String merchantId;
	/**
	 * 活动ID
	 */
	private String activityId;
	/**
	 * 活动名称
	 */
	private String activityName;
	/**
	 * 支付状态 0 未支付 1 支付
	 */
	private String payStatus;
	/**
	 * 商品类型 1 商品 2 优惠券
	 */
	private String goodsType;
	/**
	 * 商品ID
	 */
	private String goodsId;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * 0失败订单 1成功订单
	 */
	private String status;
	/**
	 * 商家名称
	 */
	private String merchantName;
	/**
	 * 商户ID
	 */
	private String merchantAccountCode;
	/**
	 * 商家ID
	 */
	private String sellerId;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 日期
	 */
	private String date;
}
