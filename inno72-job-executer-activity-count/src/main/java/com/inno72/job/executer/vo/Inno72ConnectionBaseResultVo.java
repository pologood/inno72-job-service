package com.inno72.job.executer.vo;

import java.io.Serializable;

public class Inno72ConnectionBaseResultVo implements Serializable {
    private static final long serialVersionUID = -768316005758397260L;
    private Long version;
    private String machineCode;
    private String activityId;
    private Integer type;//消息类型：0推送活动，1推送是否有人扫描二维码，2推送登陆信息，3推送关注信息，4.推送订单支付信息

    public enum TYPE_ENUM {

        FINDACTIVITY(0, "推送活动"),SCANQRCODE(1, "推送是否有人扫描二维码"),LOGIN(2, "推送登陆信息"),FOLLOW(3, "推送关注信息"),PAY(4, "推送订单支付信息");

        private Integer key;
        private String desc;

        TYPE_ENUM(Integer key, String desc) {
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
