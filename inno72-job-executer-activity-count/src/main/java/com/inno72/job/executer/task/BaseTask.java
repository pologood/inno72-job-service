package com.inno72.job.executer.task;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;

public abstract class BaseTask implements IJobHandler {

    public abstract ReturnT<String> execute(String param) throws Exception;

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }
}
