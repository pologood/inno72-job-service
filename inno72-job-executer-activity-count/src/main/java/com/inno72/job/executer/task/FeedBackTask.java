package com.inno72.job.executer.task;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.annotation.JobHandler;
import org.springframework.stereotype.Component;

@Component
@JobHandler("FeedBackTask")
public class FeedBackTask extends BaseTask{
    @Override
    public ReturnT<String> execute(String param) throws Exception {

        return null;
    }
}
