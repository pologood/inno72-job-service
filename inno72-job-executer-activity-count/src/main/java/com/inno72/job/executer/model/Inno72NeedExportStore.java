package com.inno72.job.executer.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "inno72_need_export_store")
public class Inno72NeedExportStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;

    @Column(name = "activity_id")
    private String activityId;

    /**
     * 0普通活动，1派样
     */
    @Column(name = "activity_type")
    private Integer activityType;

    @Column(name = "machine_code")
    private String machineCode;

    @Column(name = "seller_id")
    private String sellerId;

    private String province;

    private String city;

    private String locale;

    private String phone;

    @Column(name = "create_time")
    private Date createTime;

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
     * 获取0普通活动，1派样
     *
     * @return activity_type - 0普通活动，1派样
     */
    public Integer getActivityType() {
        return activityType;
    }

    /**
     * 设置0普通活动，1派样
     *
     * @param activityType 0普通活动，1派样
     */
    public void setActivityType(Integer activityType) {
        this.activityType = activityType;
    }

    /**
     * @return machine_code
     */
    public String getMachineCode() {
        return machineCode;
    }

    /**
     * @param machineCode
     */
    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    /**
     * @return seller_id
     */
    public String getSellerId() {
        return sellerId;
    }

    /**
     * @param sellerId
     */
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    /**
     * @return province
     */
    public String getProvince() {
        return province;
    }

    /**
     * @param province
     */
    public void setProvince(String province) {
        this.province = province;
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
     * @return locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * @param locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * @return phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}