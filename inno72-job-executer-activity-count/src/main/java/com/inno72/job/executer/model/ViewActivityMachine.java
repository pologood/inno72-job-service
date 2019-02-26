package com.inno72.job.executer.model;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "view_activity_machine")
public class ViewActivityMachine {
    @Column(name = "activity_name")
    private String activityName;

    @Column(name = "machine_code")
    private String machineCode;

    @Column(name = "activity_id")
    private String activityId;

    @Column(name = "machine_id")
    private String machineId;

    @Column(name = "activity_type")
    private Long activityType;

    /**
     * @return activity_name
     */
    public String getActivityName() {
        return activityName;
    }

    /**
     * @param activityName
     */
    public void setActivityName(String activityName) {
        this.activityName = activityName;
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
     * @return machine_id
     */
    public String getMachineId() {
        return machineId;
    }

    /**
     * @param machineId
     */
    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    /**
     * @return activity_type
     */
    public Long getActivityType() {
        return activityType;
    }

    /**
     * @param activityType
     */
    public void setActivityType(Long activityType) {
        this.activityType = activityType;
    }
}