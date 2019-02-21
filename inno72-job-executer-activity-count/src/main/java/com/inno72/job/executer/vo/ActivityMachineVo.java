package com.inno72.job.executer.vo;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityMachineVo that = (ActivityMachineVo) o;
        return Objects.equals(machineCode, that.machineCode) &&
                Objects.equals(activityId, that.activityId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(machineCode, activityId);
    }
}
