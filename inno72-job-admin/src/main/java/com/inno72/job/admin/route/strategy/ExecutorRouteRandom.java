package com.inno72.job.admin.route.strategy;



import java.util.ArrayList;
import java.util.Random;

import com.inno72.job.admin.route.ExecutorRouter;
import com.inno72.job.admin.scheduler.JobTrigger;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.biz.model.TriggerParam;


public class ExecutorRouteRandom extends ExecutorRouter {

    private static Random localRandom = new Random();

    public String route(int jobId, ArrayList<String> addressList) {
        // Collections.shuffle(addressList);
        return addressList.get(localRandom.nextInt(addressList.size()));
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
