package com.inno72.job.executer.vo;

import java.io.Serializable;
import java.util.Date;

public class OrderOrderGoodsVo implements Serializable {
    private static final long serialVersionUID = -7727561029355440027L;
    private String taobaoOrderNum;
    private String taobaoGoodsId;
    private String machineCode;
    private String taobaoUserId;
    private String merchantCode;
    private String merchantName;
    private Date orderTime;
    public String getTaobaoOrderNum() {
        return taobaoOrderNum;
    }

    public void setTaobaoOrderNum(String taobaoOrderNum) {
        this.taobaoOrderNum = taobaoOrderNum;
    }

    public String getTaobaoGoodsId() {
        return taobaoGoodsId;
    }

    public void setTaobaoGoodsId(String taobaoGoodsId) {
        this.taobaoGoodsId = taobaoGoodsId;
    }


    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getTaobaoUserId() {
        return taobaoUserId;
    }

    public void setTaobaoUserId(String taobaoUserId) {
        this.taobaoUserId = taobaoUserId;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }
}
