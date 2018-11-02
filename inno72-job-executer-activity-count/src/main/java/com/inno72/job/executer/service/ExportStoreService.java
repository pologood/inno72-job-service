package com.inno72.job.executer.service;

public interface ExportStoreService {
    /**
     * 导出派样活动
     * @param currentPage
     * @param pageSize
     * @return
     */
    int exportInteract(int currentPage, Integer pageSize);

    /**
     * 执行互派
     */
    void executeInteract();

    /**
     * 执行普通活动
     */
    void executeActivity();
}
