package com.inno72.job.executer.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "inno72_machine_connection_msg")
public class Inno72MachineConnectionMsg {
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
     * 发送次数
     */
    private Integer times;

    /**
     * 状态：0未处理，1已处理，2处理失败，3过期
     */
    private Integer status;

    public enum STATUS_ENUM {

        COMMIT(0, "提交未处理"),SUCCESS(1, "完成"),FAIL(2, "失败"),EXPIRE(3, "过期");

        private Integer key;
        private String desc;

        STATUS_ENUM(Integer key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public Integer getKey() {
            return key;
        }

        public void setKey(Integer key) {
            this.key = key;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    /**
     * 消息类型：0推送活动，1推送是否有人扫描二维码，2推送登陆信息，3推送关注信息，4.推送订单支付信息
     */
    private Integer type;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 版本号（时间戳）
     */
    private Long version;

    /**
     * 消息内容
     */
    private String msg;

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
     * 获取发送次数
     *
     * @return times - 发送次数
     */
    public Integer getTimes() {
        return times;
    }

    /**
     * 设置发送次数
     *
     * @param times 发送次数
     */
    public void setTimes(Integer times) {
        this.times = times;
    }

    /**
     * 获取状态：0未处理，1已处理，2处理失败
     *
     * @return status - 状态：0未处理，1已处理，2处理失败
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态：0未处理，1已处理，2处理失败
     *
     * @param status 状态：0未处理，1已处理，2处理失败
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取修改时间
     *
     * @return update_time - 修改时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置修改时间
     *
     * @param updateTime 修改时间
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

    /**
     * 获取消息内容
     *
     * @return msg - 消息内容
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置消息内容
     *
     * @param msg 消息内容
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}