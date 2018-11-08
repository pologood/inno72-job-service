package com.inno72.job.executer.service;

public interface DeviceService {
    /**
     * 保存普通活动的设备
     */
    void executeActivity();
    /**
     * 保存互派活动的设备
     */
    void executeInteract();
}
