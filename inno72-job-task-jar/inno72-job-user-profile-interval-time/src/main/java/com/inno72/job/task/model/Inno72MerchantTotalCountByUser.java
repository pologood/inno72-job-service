package com.inno72.job.task.model;

import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.*;

public class Inno72MerchantTotalCountByUser {
    private String id;

    /**
     * 活动ID
     */
    private String activityId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 商户ID inno72_merchant_user.merchant_id
     */
    private String merchantId;

    /**
     * 日期
     */
    private String date;

    /**
     * 商家code
     */
    private String sellerId;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 0:女， 1: 男， 2其他
     */
    private Integer sex;

    /**
     * 用户分类标签
     */
    private String userTag;

    /**
     * 点位标签
     */
    private String pointTag;

    /**
     * 城市
     */
    private String city;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;

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
     * 获取活动ID
     *
     * @return activity_id - 活动ID
     */
    public String getActivityId() {
        return activityId;
    }

    /**
     * 设置活动ID
     *
     * @param activityId 活动ID
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
     * 获取商户ID inno72_merchant_user.merchant_id
     *
     * @return merchant_id - 商户ID inno72_merchant_user.merchant_id
     */
    public String getMerchantId() {
        return merchantId;
    }

    /**
     * 设置商户ID inno72_merchant_user.merchant_id
     *
     * @param merchantId 商户ID inno72_merchant_user.merchant_id
     */
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    /**
     * 获取日期
     *
     * @return date - 日期
     */
    public String getDate() {
        return date;
    }

    /**
     * 设置日期
     *
     * @param date 日期
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 获取商家code
     *
     * @return seller_id - 商家code
     */
    public String getSellerId() {
        return sellerId;
    }

    /**
     * 设置商家code
     *
     * @param sellerId 商家code
     */
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    /**
     * 获取年龄
     *
     * @return age - 年龄
     */
    public Integer getAge() {
        return age;
    }

    /**
     * 设置年龄
     *
     * @param age 年龄
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * 获取0:女， 1: 男， 2其他
     *
     * @return sex - 0:女， 1: 男， 2其他
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * 设置0:女， 1: 男， 2其他
     *
     * @param sex 0:女， 1: 男， 2其他
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * 获取用户分类标签
     *
     * @return user_tag - 用户分类标签
     */
    public String getUserTag() {
        return userTag;
    }

    /**
     * 设置用户分类标签
     *
     * @param userTag 用户分类标签
     */
    public void setUserTag(String userTag) {
        this.userTag = userTag;
    }

    /**
     * 获取点位标签
     *
     * @return point_tag - 点位标签
     */
    public String getPointTag() {
        return pointTag;
    }

    /**
     * 设置点位标签
     *
     * @param pointTag 点位标签
     */
    public void setPointTag(String pointTag) {
        this.pointTag = pointTag;
    }

    /**
     * 获取城市
     *
     * @return city - 城市
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置城市
     *
     * @param city 城市
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取最后更新时间
     *
     * @return last_update_time - 最后更新时间
     */
    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * 设置最后更新时间
     *
     * @param lastUpdateTime 最后更新时间
     */
    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}