package com.inno72.job.activity.report.model;

import java.util.Date;

public class Inno72StatisticActivityReport {
    private Integer id;

    /**
     * 机器编码
     */
    private String machineCode;

    /**
     * 日期
     */
	private String handleTime;

    /**
     * 活动id
     */
    private String activityId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 商品id
     */
    private String goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商户id
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 点位id
     */
    private String localId;

    /**
     * 点位名称
     */
    private String localName;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    /**
     * 客流量
     */
    private Integer vistor;

    /**
     * pv
     */
    private Integer pv;

    /**
     * uv
     */
    private Integer uv;

    /**
     * 订单量
     */
    private Integer orders;

    /**
     * 掉货量
     */
    private Integer shipment;

    /**
     * 关注数
     */
    private Integer follow;

    /**
     * 店铺名称
     */
    private String shopName;

	/**
	 * 类型
	 */
	private Integer type;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取机器编码
     *
     * @return machine_code - 机器编码
     */
    public String getMachineCode() {
        return machineCode;
    }

    /**
     * 设置机器编码
     *
     * @param machineCode 机器编码
     */
    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    /**
     * 获取活动id
     *
     * @return activity_id - 活动id
     */
    public String getActivityId() {
        return activityId;
    }

    /**
     * 设置活动id
     *
     * @param activityId 活动id
     */
    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    /**
     * 获取活动名称
     *
     * @return activity_name - 活动名称
     */
    public String getActivityName() {
        return activityName;
    }

    /**
     * 设置活动名称
     *
     * @param activityName 活动名称
     */
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    /**
     * 获取商品id
     *
     * @return goods_id - 商品id
     */
    public String getGoodsId() {
        return goodsId;
    }

    /**
     * 设置商品id
     *
     * @param goodsId 商品id
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

    /**
     * 获取商户id
     *
     * @return merchant_id - 商户id
     */
    public String getMerchantId() {
        return merchantId;
    }

    /**
     * 设置商户id
     *
     * @param merchantId 商户id
     */
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    /**
     * 获取商户名称
     *
     * @return merchant_name - 商户名称
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
     * 获取点位id
     *
     * @return local_id - 点位id
     */
    public String getLocalId() {
        return localId;
    }

    /**
     * 设置点位id
     *
     * @param localId 点位id
     */
    public void setLocalId(String localId) {
        this.localId = localId;
    }

    /**
     * 获取点位名称
     *
     * @return local_name - 点位名称
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * 设置点位名称
     *
     * @param localName 点位名称
     */
    public void setLocalName(String localName) {
        this.localName = localName;
    }

    /**
     * 获取省
     *
     * @return province - 省
     */
    public String getProvince() {
        return province;
    }

    /**
     * 设置省
     *
     * @param province 省
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 获取市
     *
     * @return city - 市
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置市
     *
     * @param city 市
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取区
     *
     * @return district - 区
     */
    public String getDistrict() {
        return district;
    }

    /**
     * 设置区
     *
     * @param district 区
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * 获取客流量
     *
     * @return vistor - 客流量
     */
    public Integer getVistor() {
        return vistor;
    }

    /**
     * 设置客流量
     *
     * @param vistor 客流量
     */
    public void setVistor(Integer vistor) {
        this.vistor = vistor;
    }

    /**
     * 获取pv
     *
     * @return pv - pv
     */
    public Integer getPv() {
        return pv;
    }

    /**
     * 设置pv
     *
     * @param pv pv
     */
    public void setPv(Integer pv) {
        this.pv = pv;
    }

    /**
     * 获取uv
     *
     * @return uv - uv
     */
    public Integer getUv() {
        return uv;
    }

    /**
     * 设置uv
     *
     * @param uv uv
     */
    public void setUv(Integer uv) {
        this.uv = uv;
    }


    /**
     * 获取掉货量
     *
     * @return shipment - 掉货量
     */
    public Integer getShipment() {
        return shipment;
    }

    /**
     * 设置掉货量
     *
     * @param shipment 掉货量
     */
    public void setShipment(Integer shipment) {
        this.shipment = shipment;
    }

    /**
     * 获取关注数
     *
     * @return follow - 关注数
     */
    public Integer getFollow() {
        return follow;
    }

    /**
     * 设置关注数
     *
     * @param follow 关注数
     */
    public void setFollow(Integer follow) {
        this.follow = follow;
    }

    /**
     * 获取店铺名称
     *
     * @return shop_name - 店铺名称
     */
    public String getShopName() {
        return shopName;
    }

    /**
     * 设置店铺名称
     *
     * @param shopName 店铺名称
     */
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

	public String getHandleTime() {
		return handleTime;
	}

	public void setHandleTime(String handleTime) {
		this.handleTime = handleTime;
	}

	public Integer getOrders() {
		return orders;
	}

	public void setOrders(Integer orders) {
		this.orders = orders;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}