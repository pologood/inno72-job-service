package com.inno72.job.executer.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "inno72_machine_device")
public class Inno72MachineDevice {
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;

    @Column(name = "machine_code")
    private String machineCode;

    /**
     * 设备code
     */
    @Column(name = "device_code")
    private String deviceCode;

    @Column(name = "create_time")
    private Date createTime;

    /**
     * 淘宝门店id
     */
    @Column(name = "store_id")
    private Long storeId;
    /**
     * 商户id
     */
    @Column(name = "seller_id")
    private String sellerId;

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
     * 获取设备code
     *
     * @return device_code - 设备code
     */
    public String getDeviceCode() {
        return deviceCode;
    }

    /**
     * 设置设备code
     *
     * @param deviceCode 设备code
     */
    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
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

    /**
     * 获取淘宝门店id
     *
     * @return store_id - 淘宝门店id
     */
    public Long getStoreId() {
        return storeId;
    }

    /**
     * 设置淘宝门店id
     *
     * @param storeId 淘宝门店id
     */
    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}