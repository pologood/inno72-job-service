package com.inno72.job.executer.vo;

import java.io.Serializable;

public class PushRequestVo implements Serializable {
    private static final long serialVersionUID = -3048749944096467444L;
    private String targetCode;
    private String targetType="1";
    private Integer isEncrypt=0;
    private String msgType="h5";
    private String data;
    private Integer isQueue=0;

    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getIsQueue() {
        return isQueue;
    }

    public void setIsQueue(Integer isQueue) {
        this.isQueue = isQueue;
    }

    public Integer getIsEncrypt() {
        return isEncrypt;
    }

    public void setIsEncrypt(Integer isEncrypt) {
        this.isEncrypt = isEncrypt;
    }
}
