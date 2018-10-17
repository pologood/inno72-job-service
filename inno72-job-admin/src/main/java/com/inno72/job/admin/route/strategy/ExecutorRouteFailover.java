package com.inno72.job.admin.route.strategy;



import java.util.ArrayList;

import com.inno72.job.admin.route.ExecutorRouter;
import com.inno72.job.admin.scheduler.JobDynamicScheduler;
import com.inno72.job.admin.scheduler.JobTrigger;
import com.inno72.job.core.biz.ExecutorBiz;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.biz.model.TriggerParam;


public class ExecutorRouteFailover extends ExecutorRouter {

    public String route(int jobId, ArrayList<String> addressList) {
        return addressList.get(0);
    }

    @Override
    public ReturnT<String> routeRun(TriggerParam triggerParam, ArrayList<String> addressList) {

        StringBuffer beatResultSB = new StringBuffer();
        for (String address : addressList) {
            // beat
            ReturnT<Void> beatResult = null;
            try {
                ExecutorBiz executorBiz = JobDynamicScheduler.getExecutorBiz(address);
                beatResult = executorBiz.beat();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                beatResult = new ReturnT<Void>(ReturnT.FAIL_CODE, ""+e );
            }
            beatResultSB.append( (beatResultSB.length()>0)?"<br><br>":"")
                    .append( "心跳检测：")
                    .append("<br>address：").append(address)
                    .append("<br>code：").append(beatResult.getCode())
                    .append("<br>msg：").append(beatResult.getMsg());

            // beat success
            if (beatResult.getCode() == ReturnT.SUCCESS_CODE) {

                ReturnT<String> runResult = JobTrigger.runExecutor(triggerParam, address);
                beatResultSB.append("<br><br>").append(runResult.getMsg());

                // result
                runResult.setMsg(beatResultSB.toString());
                runResult.setData(address);
                return runResult;
            }
        }
        return new ReturnT<String>(ReturnT.FAIL_CODE, beatResultSB.toString());

    }
}
