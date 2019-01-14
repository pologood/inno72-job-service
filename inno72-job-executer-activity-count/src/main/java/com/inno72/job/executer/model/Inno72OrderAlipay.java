package com.inno72.job.executer.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "inno72_order_alipay")
public class Inno72OrderAlipay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;

    @Column(name = "machine_code")
    private String machineCode;

    @Column(name = "order_id")
    private String orderId;

    /**
     * 状态 0初始化状态，1成功，2失败（超时）
     */
    private Integer status;

    public enum STATUS_ENUM {

        CREATE(0, "初始化"),SUCCESS(1, "成功"),FAIL(2, "失败");

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

    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "activity_id")
    private String activityId;

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
     * @return order_id
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * @param orderId
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取状态 0初始化状态，1成功，2失败（超时）
     *
     * @return status - 状态 0初始化状态，1成功，2失败（超时）
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态 0初始化状态，1成功，2失败（超时）
     *
     * @param status 状态 0初始化状态，1成功，2失败（超时）
     */
    public void setStatus(Integer status) {
        this.status = status;
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

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }
}