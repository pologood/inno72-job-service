package com.inno72.job.executer.task;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.executer.config.Constants;
import com.inno72.job.executer.service.ExportStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@JobHandler("ExportStoreTask")
public class ExportStoreTask extends BaseTask{
    @Autowired
    ExportStoreService exportStoreService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportStoreTask.class);

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        exportStoreService.executeInteract();
        exportStoreService.executeActivity();
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
    }
}
