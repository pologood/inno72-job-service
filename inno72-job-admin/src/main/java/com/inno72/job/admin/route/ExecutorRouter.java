package com.inno72.job.admin.route;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.biz.model.TriggerParam;

public abstract class ExecutorRouter {
    protected static Logger logger = LoggerFactory.getLogger(ExecutorRouter.class);

    /**
     * route run executor
     *
     * @param triggerParam
     * @param addressList
     * @return  ReturnT.content: final address
     */
    public abstract ReturnT<String> routeRun(TriggerParam triggerParam, ArrayList<String> addressList);

}
