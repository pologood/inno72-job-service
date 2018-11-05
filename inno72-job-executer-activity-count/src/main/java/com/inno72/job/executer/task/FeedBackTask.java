package com.inno72.job.executer.task;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.executer.service.FeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@JobHandler("FeedBackTask")
public class FeedBackTask extends BaseTask{
    @Autowired
    FeedBackService service;
    public static final Integer EXECUTE_TYPE_ERROR=0;
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        service.feedBackOrder(EXECUTE_TYPE_ERROR);
        return null;
    }
}
