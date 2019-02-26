package com.inno72.job.executer.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "inno72_machine_activity_image")
public class Inno72MachineActivityImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;

    /**
     * 机器code
     */
    @Column(name = "machine_code")
    private String machineCode;

    /**
     * 活动id
     */
    @Column(name = "activity_id")
    private String activityId;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 版本号（时间戳）
     */
    private Long version;

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
     * 获取机器code
     *
     * @return machine_code - 机器code
     */
    public String getMachineCode() {
        return machineCode;
    }

    /**
     * 设置机器code
     *
     * @param machineCode 机器code
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
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取版本号（时间戳）
     *
     * @return version - 版本号（时间戳）
     */
    public Long getVersion() {
        return version;
    }

    /**
     * 设置版本号（时间戳）
     *
     * @param version 版本号（时间戳）
     */
    public void setVersion(Long version) {
        this.version = version;
    }
}