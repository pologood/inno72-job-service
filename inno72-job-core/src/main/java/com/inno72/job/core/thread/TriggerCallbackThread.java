package com.inno72.job.core.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inno72.job.core.biz.AdminBiz;
import com.inno72.job.core.biz.model.HandleCallbackParam;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.executor.JobExecutor;


public class TriggerCallbackThread {
    private static Logger logger = LoggerFactory.getLogger(TriggerCallbackThread.class);

    private static TriggerCallbackThread instance = new TriggerCallbackThread();
    public static TriggerCallbackThread getInstance(){
        return instance;
    }

    /**
     * job results callback queue
     */
    private LinkedBlockingQueue<HandleCallbackParam> callBackQueue = new LinkedBlockingQueue<HandleCallbackParam>();
    public static void pushCallBack(HandleCallbackParam callback){
        getInstance().callBackQueue.add(callback);
        logger.debug(">>>>>>>>>>> job, push callback request, logId:{}", callback.getLogId());
    }

    /**
     * callback thread
     */
    private Thread triggerCallbackThread;
    private volatile boolean toStop = false;
    public void start() {

        // valid
        if (JobExecutor.getAdminBizList() == null) {
            logger.warn(">>>>>>>>>>> job, executor callback config fail, adminAddresses is null.");
            return;
        }

        triggerCallbackThread = new Thread(new Runnable() {

            @Override
            public void run() {

                // normal callback
                while(!toStop){
                    try {
                        HandleCallbackParam callback = getInstance().callBackQueue.take();
                        if (callback != null) {

                            // callback list param
                            List<HandleCallbackParam> callbackParamList = new ArrayList<HandleCallbackParam>();
                            int drainToNum = getInstance().callBackQueue.drainTo(callbackParamList);
                            callbackParamList.add(callback);

                            // callback, will retry if error
                            if (callbackParamList!=null && callbackParamList.size()>0) {
                                doCallback(callbackParamList);
                            }
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }

                // last callback
                try {
                    List<HandleCallbackParam> callbackParamList = new ArrayList<HandleCallbackParam>();
                    int drainToNum = getInstance().callBackQueue.drainTo(callbackParamList);
                    if (callbackParamList!=null && callbackParamList.size()>0) {
                        doCallback(callbackParamList);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                logger.info(">>>>>>>>>>> job, executor callback thread destory.");

            }
        });
        triggerCallbackThread.setDaemon(true);
        triggerCallbackThread.start();
    }
    public void toStop(){
        toStop = true;
        // interrupt and wait
        triggerCallbackThread.interrupt();
        try {
            triggerCallbackThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * do callback, will retry if error
     * @param callbackParamList
     */
    private void doCallback(List<HandleCallbackParam> callbackParamList){
        // callback, will retry if error
        for (AdminBiz adminBiz: JobExecutor.getAdminBizList()) {
            try {
                ReturnT<Void> callbackResult = adminBiz.callback(callbackParamList);
                if (callbackResult!=null && ReturnT.SUCCESS_CODE == callbackResult.getCode()) {
                    callbackResult = ReturnT.SUCCESS;
                    logger.info(">>>>>>>>>>> job callback success, callbackParamList:{}, callbackResult:{}", new Object[]{callbackParamList, callbackResult});
                    break;
                } else {
                    logger.info(">>>>>>>>>>> job callback fail, callbackParamList:{}, callbackResult:{}", new Object[]{callbackParamList, callbackResult});
                }
            } catch (Exception e) {
                logger.error(">>>>>>>>>>> job callback error, callbackParamList：{}", callbackParamList, e);
                //getInstance().callBackQueue.addAll(callbackParamList);
            }
        }
    }

}
