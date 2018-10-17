package com.inno72.job.admin.scheduler;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inno72.job.admin.common.ExecutorFailStrategyEnum;
import com.inno72.job.admin.common.ExecutorRouteStrategyEnum;
import com.inno72.job.admin.model.JobGroup;
import com.inno72.job.admin.model.JobInfo;
import com.inno72.job.admin.model.JobLog;
import com.inno72.job.core.biz.ExecutorBiz;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.biz.model.TriggerParam;
import com.inno72.job.core.enums.ExecutorBlockStrategyEnum;
import com.inno72.job.core.util.IpUtil;


public class JobTrigger {
    private static Logger logger = LoggerFactory.getLogger(JobTrigger.class);

    /**
     * trigger job
     *
     * @param jobId
     */
    public static void trigger(int jobId) {

        // load data
        JobInfo jobInfo = JobDynamicScheduler.jobInfoDao.loadById(jobId);              // job info
        if (jobInfo == null) {
            logger.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return;
        }
        JobGroup group = JobDynamicScheduler.jobGroupDao.load(jobInfo.getJobGroup());  // group info

        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorBlockStrategyEnum.SERIAL_EXECUTION);  // block strategy
        ExecutorFailStrategyEnum failStrategy = ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(), ExecutorFailStrategyEnum.FAIL_ALARM);    // fail strategy
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);    // route strategy
        ArrayList<String> addressList = (ArrayList<String>) group.getRegistryList();

        // broadcast
        if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum && CollectionUtils.isNotEmpty(addressList)) {
            for (int i = 0; i < addressList.size(); i++) {
                String address = addressList.get(i);

                // 1、save log-id
                JobLog jobLog = new JobLog();
                jobLog.setJobGroup(jobInfo.getJobGroup());
                jobLog.setJobId(jobInfo.getId());
                JobDynamicScheduler.jobLogDao.save(jobLog);
                logger.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());

                // 2、prepare trigger-info
                //jobLog.setExecutorAddress(executorAddress);
                jobLog.setGlueType(jobInfo.getGlueType());
                jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
                jobLog.setExecutorParam(jobInfo.getExecutorParam());
                jobLog.setTriggerTime(new Date());

                ReturnT<String> triggerResult = new ReturnT<String>(null);
                StringBuffer triggerMsgSb = new StringBuffer();
                triggerMsgSb.append("调度机器").append("：").append(IpUtil.getIp());
                triggerMsgSb.append("<br>").append("执行器-注册方式").append("：")
                        .append( (group.getAddressType() == 0)?"自动注册": "手动录入" );
                triggerMsgSb.append("<br>").append("执行器-地址列表").append("：").append(group.getRegistryList());
                triggerMsgSb.append("<br>").append("路由策略").append("：").append(executorRouteStrategyEnum.getTitle()).append("("+i+"/"+addressList.size()+")"); // update01
                triggerMsgSb.append("<br>").append("阻塞处理策略").append("：").append(blockStrategy.getTitle());
                triggerMsgSb.append("<br>").append("失败处理策略").append("：").append(failStrategy.getTitle());

                // 3、trigger-valid
                if (triggerResult.getCode()==ReturnT.SUCCESS_CODE && CollectionUtils.isEmpty(addressList)) {
                    triggerResult.setCode(ReturnT.FAIL_CODE);
                    triggerMsgSb.append("<br>----------------------<br>").append("调度失败：执行器地址为空");
                }

                if (triggerResult.getCode() == ReturnT.SUCCESS_CODE) {
                    // 4.1、trigger-param
                    TriggerParam triggerParam = new TriggerParam();
                    triggerParam.setJobId(jobInfo.getId());
                    triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
                    triggerParam.setExecutorParams(jobInfo.getExecutorParam());
                    triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
                    triggerParam.setLogId(jobLog.getId());
                    triggerParam.setLogDateTim(jobLog.getTriggerTime().getTime());
                    triggerParam.setGlueType(jobInfo.getGlueType());
                    triggerParam.setGlueSource(jobInfo.getGlueSource());
                    triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());
                    triggerParam.setBroadcastIndex(i);
                    triggerParam.setBroadcastTotal(addressList.size()); // update02

                    // 4.2、trigger-run (route run / trigger remote executor)
                    triggerResult = runExecutor(triggerParam, address);     // update03
                    triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>").append(triggerResult.getMsg());

                    // 4.3、trigger (fail retry)
                    if (triggerResult.getCode()!=ReturnT.SUCCESS_CODE && failStrategy == ExecutorFailStrategyEnum.FAIL_RETRY) {
                        triggerResult = runExecutor(triggerParam, address);  // update04
                        triggerMsgSb.append("<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>调度失败重试<<<<<<<<<<< </span><br>").append(triggerResult.getMsg());
                    }
                }

                // 5、save trigger-info
                jobLog.setExecutorAddress(triggerResult.getData());
                jobLog.setTriggerCode(triggerResult.getCode());
                jobLog.setTriggerMsg(triggerMsgSb.toString());
                JobDynamicScheduler.jobLogDao.updateTriggerInfo(jobLog);

                // 6、monitor trigger
                JobFailMonitorHelper.monitor(jobLog.getId());
                logger.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());

            }
        } else {
            // 1、save log-id
            JobLog jobLog = new JobLog();
            jobLog.setJobGroup(jobInfo.getJobGroup());
            jobLog.setJobId(jobInfo.getId());
            JobDynamicScheduler.jobLogDao.save(jobLog);
            logger.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());

            // 2、prepare trigger-info
            //jobLog.setExecutorAddress(executorAddress);
            jobLog.setGlueType(jobInfo.getGlueType());
            jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
            jobLog.setExecutorParam(jobInfo.getExecutorParam());
            jobLog.setTriggerTime(new Date());

            ReturnT<String> triggerResult = new ReturnT<String>(null);
            StringBuffer triggerMsgSb = new StringBuffer();
            triggerMsgSb.append("调度机器：").append(IpUtil.getIp());
            triggerMsgSb.append("<br>").append("执行器-注册方式：")
                    .append( (group.getAddressType() == 0)?"自动注册":"手动录入");
            triggerMsgSb.append("<br>").append("执行器-地址列表：").append(group.getRegistryList());
            triggerMsgSb.append("<br>").append("路由策略：").append(executorRouteStrategyEnum.getTitle());
            triggerMsgSb.append("<br>").append("阻塞处理策略：").append(blockStrategy.getTitle());
            triggerMsgSb.append("<br>").append("失败处理策略：").append(failStrategy.getTitle());

            // 3、trigger-valid
            if (triggerResult.getCode()==ReturnT.SUCCESS_CODE && CollectionUtils.isEmpty(addressList)) {
                triggerResult.setCode(ReturnT.FAIL_CODE);
                triggerMsgSb.append("<br>----------------------<br>调度失败：执行器地址为空");
            }

            if (triggerResult.getCode() == ReturnT.SUCCESS_CODE) {
                // 4.1、trigger-param
                TriggerParam triggerParam = new TriggerParam();
                triggerParam.setJobId(jobInfo.getId());
                triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
                triggerParam.setExecutorParams(jobInfo.getExecutorParam());
                triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
                triggerParam.setLogId(jobLog.getId());
                triggerParam.setLogDateTim(jobLog.getTriggerTime().getTime());
                triggerParam.setGlueType(jobInfo.getGlueType());
                triggerParam.setGlueSource(jobInfo.getGlueSource());
                triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());
                triggerParam.setBroadcastIndex(0);
                triggerParam.setBroadcastTotal(1);

                // 4.2、trigger-run (route run / trigger remote executor)
                triggerResult = executorRouteStrategyEnum.getRouter().routeRun(triggerParam, addressList);
                triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>").append(triggerResult.getMsg());

                // 4.3、trigger (fail retry)
                if (triggerResult.getCode()!=ReturnT.SUCCESS_CODE && failStrategy == ExecutorFailStrategyEnum.FAIL_RETRY) {
                    triggerResult = executorRouteStrategyEnum.getRouter().routeRun(triggerParam, addressList);
                    triggerMsgSb.append("<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>调度失败重试<<<<<<<<<<< </span><br>").append(triggerResult.getMsg());
                }
            }

            // 5、save trigger-info
            jobLog.setExecutorAddress(triggerResult.getData());
            jobLog.setTriggerCode(triggerResult.getCode());
            jobLog.setTriggerMsg(triggerMsgSb.toString());
            JobDynamicScheduler.jobLogDao.updateTriggerInfo(jobLog);

            // 6、monitor trigger
            JobFailMonitorHelper.monitor(jobLog.getId());
            logger.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
        }

    }

    /**
     * run executor
     * @param triggerParam
     * @param address
     * @return  ReturnT.content: final address
     */
    public static ReturnT<String> runExecutor(TriggerParam triggerParam, String address){
        ReturnT<Void> runResult = null;
        try {
            ExecutorBiz executorBiz = JobDynamicScheduler.getExecutorBiz(address);
            runResult = executorBiz.run(triggerParam);
        } catch (Exception e) {
            logger.error(">>>>>>>>>>> xxl-job trigger error, please check if the executor[{}] is running.", address, e);
            runResult = new ReturnT<Void>(ReturnT.FAIL_CODE, ""+e );
        }

        StringBuffer runResultSB = new StringBuffer("触发调度"+ "：");
        runResultSB.append("<br>address：").append(address);
        runResultSB.append("<br>code：").append(runResult.getCode());
        runResultSB.append("<br>msg：").append(runResult.getMsg());

        ReturnT<String> result = new ReturnT<String>();
        result.setMsg(runResultSB.toString());
        result.setData(address);
        return result;
    }

}
