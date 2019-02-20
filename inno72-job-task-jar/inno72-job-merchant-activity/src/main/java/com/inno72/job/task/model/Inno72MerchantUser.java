package com.inno72.job.task.model;

import java.time.LocalDateTime;

public class Inno72MerchantUser {
	private String id;

	/**
	 * 商户表主键id
	 */
	private String merchantId;

	/**
	 * 登录名
	 */
	private String loginName;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 商户名称
	 */
	private String merchantName;

	/**
	 * 验证手机号
	 */
	private String phone;


	/**
	 * 验证手机号
	 */
	private String loginStatus;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	/**
	 * 创建人
	 */
	private String creator;

	/**
	 * 行业
	 */
	private String industry;

	/**
	 * 行业
	 */
	private String industryCode;



	/**
	 * 最后更新时间
	 */
	private LocalDateTime lastUpdateTime;

	/**
	 * 最后更新人
	 */
	private String lastUpdator;

	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id 主键
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取商户表主键id
	 *
	 * @return inno72_merchant_id - 商户表主键id
	 */
	public String getMerchantId() {
		return merchantId;
	}

	/**
	 * 设置商户表主键id
	 *
	 * @param merchantId 商户表主键id
	 */
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	/**
	 * 获取登录名
	 *
	 * @return login_name - 登录名
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * 设置登录名
	 *
	 * @param loginName 登录名
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * 获取密码
	 *
	 * @return password - 密码
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 设置密码
	 *
	 * @param password 密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 获取商户名称
	 *
	 * @return seller_name - 商户名称
	 */
	public String getMerchantName() {
		return merchantName;
	}

	/**
	 * 设置商户名称
	 *
	 * @param merchantName 商户名称
	 */
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	/**
	 * 获取验证手机号
	 *
	 * @return phone - 验证手机号
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * 设置验证手机号
	 *
	 * @param phone 验证手机号
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(String loginStatus) {
		this.loginStatus = loginStatus;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public LocalDateTime getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getLastUpdator() {
		return lastUpdator;
	}

	public void setLastUpdator(String lastUpdator) {
		this.lastUpdator = lastUpdator;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getIndustryCode() {
		return industryCode;
	}

	public void setIndustryCode(String industryCode) {
		this.industryCode = industryCode;
	}
}