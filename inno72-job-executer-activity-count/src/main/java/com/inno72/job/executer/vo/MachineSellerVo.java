package com.inno72.job.executer.vo;

import java.io.Serializable;

/**
 *
 */
public class MachineSellerVo implements Serializable {
    private static final long serialVersionUID = 175479512480858039L;
    private String machineCode;
    private String sellerId;

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}
