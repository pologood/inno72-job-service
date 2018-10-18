package com.inno72.job.core.biz;

import java.util.List;

import com.inno72.job.core.biz.model.HandleCallbackParam;
import com.inno72.job.core.biz.model.RegistryParam;
import com.inno72.job.core.biz.model.ReturnT;


public interface AdminBiz {

    public static final String MAPPING = "/api";

    /**
     * callback
     *
     * @param callbackParamList
     * @return
     */
    public ReturnT<Void> callback(List<HandleCallbackParam> callbackParamList);

    /**
     * registry
     *
     * @param registryParam
     * @return
     */
    public ReturnT<Void> registry(RegistryParam registryParam);

    /**
     * registry remove
     *
     * @param registryParam
     * @return
     */
    public ReturnT<Void> registryRemove(RegistryParam registryParam);


    /**
     * trigger job for once
     *
     * @param jobId
     * @return
     */
    public ReturnT<Void> triggerJob(int jobId);
    
    
    
    public ReturnT<String>  getJarChecksum(int jobId);

}
