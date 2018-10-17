package com.inno72.job.core.biz;

import com.inno72.job.core.biz.model.LogResult;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.biz.model.TriggerParam;


public interface ExecutorBiz {

    /**
     * beat
     * @return
     */
    public ReturnT<Void> beat();

    /**
     * idle beat
     *
     * @param jobId
     * @return
     */
    public ReturnT<Void> idleBeat(int jobId);

    /**
     * kill
     * @param jobId
     * @return
     */
    public ReturnT<Void> kill(int jobId);

    /**
     * log
     * @param logDateTim
     * @param logId
     * @param fromLineNum
     * @return
     */
    public ReturnT<LogResult> log(long logDateTim, int logId, int fromLineNum);

    /**
     * run
     * @param triggerParam
     * @return
     */
    public ReturnT<Void> run(TriggerParam triggerParam);

}
