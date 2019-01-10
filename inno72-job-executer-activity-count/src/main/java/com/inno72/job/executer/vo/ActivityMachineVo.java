package com.inno72.job.executer.vo;

import java.io.Serializable;

public class ActivityMachineVo implements Serializable {
    private String machineCode;
    private String activityId;
    private Long activityType;

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

    public Long getActivityType() {
        return activityType;
    }

    public void setActivityType(Long activityType) {
        this.activityType = activityType;
    }
}
