package com.inno72.job.core.thread;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inno72.job.core.biz.model.HandleCallbackParam;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.biz.model.TriggerParam;
import com.inno72.job.core.executor.JobExecutor;
import com.inno72.job.core.glue.GlueTypeEnum;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.log.JobFileAppender;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.core.util.ExeJarManager;
import com.inno72.job.core.util.ShardingUtil;


public class JobThread extends Thread{
	private static Logger logger = LoggerFactory.getLogger(JobThread.class);

	private int jobId;
	private LinkedBlockingQueue<TriggerParam> triggerQueue;
	private ConcurrentHashSet<Integer> triggerLogIdSet;		// avoid repeat trigger for the same TRIGGER_LOG_ID

	private boolean toStop = false;
	private String stopReason;

    private boolean running = false;    // if running job
	private int idleTimes = 0;			// idel times


	public JobThread(int jobId) {
		this.jobId = jobId;
		this.triggerQueue = new LinkedBlockingQueue<TriggerParam>();
		this.triggerLogIdSet = new ConcurrentHashSet<Integer>();
	}

    /**
     * new trigger to queue
     *
     * @param triggerParam
     * @return
     */
	public ReturnT<Void> pushTriggerQueue(TriggerParam triggerParam) {
		// avoid repeat
		if (triggerLogIdSet.contains(triggerParam.getLogId())) {
			logger.info(">>>>>>>>>>> repeate trigger job, logId:{}", triggerParam.getLogId());
			return new ReturnT<Void>(ReturnT.FAIL_CODE, "repeate trigger job, logId:" + triggerParam.getLogId());
		}

		triggerLogIdSet.add(triggerParam.getLogId());
		triggerQueue.add(triggerParam);
        return ReturnT.SUCCESS;
	}

    /**
     * kill job thread
     *
     * @param stopReason
     */
	public void toStop(String stopReason) {
		/**
		 * Thread.interrupt只支持终止线程的阻塞状态(wait、join、sleep)，
		 * 在阻塞出抛出InterruptedException异常,但是并不会终止运行的线程本身；
		 * 所以需要注意，此处彻底销毁本线程，需要通过共享变量方式；
		 */
		this.toStop = true;
		this.stopReason = stopReason;
	}

    /**
     * is running job
     * @return
     */
    public boolean isRunningOrHasQueue() {
        return running || triggerQueue.size()>0;
    }

    @Override
	public void run() {

		// execute
		while(!toStop){
			running = false;
			idleTimes++;

            TriggerParam triggerParam = null;
            ReturnT<String> executeResult = null;
            try {
				// to check toStop signal, we need cycle, so wo cannot use queue.take(), instand of poll(timeout)
				triggerParam = triggerQueue.poll(3L, TimeUnit.SECONDS);
				if (triggerParam!=null) {
					running = true;
					idleTimes = 0;
					triggerLogIdSet.remove(triggerParam.getLogId());

					// log filename, like "logPath/yyyy-MM-dd/9999.log"
					String logFileName = JobFileAppender.makeLogFileName(new Date(triggerParam.getLogDateTim()), triggerParam.getLogId());
					JobFileAppender.contextHolder.set(logFileName);
					ShardingUtil.setShardingVo(new ShardingUtil.ShardingVO(triggerParam.getBroadcastIndex(), triggerParam.getBroadcastTotal()));
					
					ReturnT<String> handleResult = null;
					
					if(GlueTypeEnum.JAVA_JAR_INTERNAL.name().equals(triggerParam.getGlueType())) {
						ExeJarManager exeJarManager = JobExecutor.getApplicationContext().getBean(ExeJarManager.class);
						File jarFile = new File(exeJarManager.getJarFileName(jobId, triggerParam.getGlueUpdatetime()));
						exeJarManager.saveJarFile(jarFile, triggerParam.getJarFile());
						IJobHandler handle = exeJarManager.loadJar(jobId, triggerParam.getExecutorHandler(), triggerParam.getGlueUpdatetime(), jarFile);
						
						JobLogger.log("<br>-----------job execute start -----------<br>----------- Param:" + triggerParam.getExecutorParams());
						try {
							triggerParam.getHandler().init();
							handleResult = handle.execute(triggerParam.getExecutorParams());
							triggerParam.getHandler().destroy();
						}catch(Exception e) {
							JobLogger.log(e);
						}
						triggerParam.setHandler(null);
						exeJarManager.unloadJar(jobId);
						jarFile.delete();
					}else {
						try {
							triggerParam.getHandler().init();
							handleResult = triggerParam.getHandler().execute(triggerParam.getExecutorParams());
							triggerParam.getHandler().destroy();
							triggerParam.setHandler(null);
						}catch(Exception e) {
							JobLogger.log(e);
						}
					}
				
					if (handleResult == null) {
						executeResult = new ReturnT<String>(-1, "返回值为空");
					}else {
						executeResult = new ReturnT<String>();
						executeResult.setCode(handleResult.getCode());
						executeResult.setMsg(handleResult.getMsg());
						executeResult.setData(handleResult.getData());
					}
					JobLogger.log("<br>-----------job execute end(finish) -----------<br>----------- ReturnT:" + executeResult);

				} else {
					if (idleTimes > 30) {
						JobExecutor.removeJobThread(jobId, "excutor idel times over limit.");
					}
				}
			} catch (Throwable e) {
				if (toStop) {
					JobLogger.log("<br>----------- JobThread toStop, stopReason:" + stopReason);
				}

				StringWriter stringWriter = new StringWriter();
				e.printStackTrace(new PrintWriter(stringWriter));
				String errorMsg = stringWriter.toString();
				executeResult = new ReturnT<String>(ReturnT.FAIL_CODE, errorMsg);

				JobLogger.log("<br>----------- JobThread Exception:" + errorMsg + "<br>----------- job job execute end(error) -----------");
			} finally {
                if(triggerParam != null) {
                    // callback handler info
                    if (!toStop) {
                        // commonm
                        TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), executeResult));
                    } else {
                        // is killed
                        ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, stopReason + " [业务运行中，被强制终止]");
                        TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), stopResult));
                    }
                }
            }
        }

		// callback trigger request in queue
		while(triggerQueue !=null && triggerQueue.size()>0){
			TriggerParam triggerParam = triggerQueue.poll();
			if (triggerParam!=null) {
				// is killed
				ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, stopReason + " [任务尚未执行，在调度队列中被终止]");
				TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), stopResult));
			}
		}

		logger.info(">>>>>>>>>>> xxl-job JobThread stoped, hashCode:{}", Thread.currentThread());
	}
}
