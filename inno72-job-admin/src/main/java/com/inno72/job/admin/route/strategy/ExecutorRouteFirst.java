package com.inno72.job.admin.route.strategy;

import java.util.ArrayList;

import com.inno72.job.admin.route.ExecutorRouter;
import com.inno72.job.admin.scheduler.JobTrigger;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.biz.model.TriggerParam;


public class ExecutorRouteFirst extends ExecutorRouter {

    public String route(int jobId, ArrayList<String> addressList) {
        return addressList.get(0);
    }

    @Override
    public ReturnT<String> routeRun(TriggerParam triggerParam, ArrayList<String> addressList) {

        // address
        String address = route(triggerParam.getJobId(), addressList);

        // run executor
        ReturnT<String> runResult = JobTrigger.runExecutor(triggerParam, address);
        runResult.setData(address);
        return runResult;
    }
}
