package com.inno72.job.executer.controller;

import com.inno72.common.Result;
import com.inno72.common.Results;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.executer.config.Constants;
import com.inno72.job.executer.mapper.ActivityMapper;
import com.inno72.job.executer.service.ExportStoreService;
import com.inno72.job.executer.task.ExportStoreTask;
import com.inno72.job.executer.vo.MachineSellerVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TestController {
    @Autowired
    ActivityMapper activityMapper;

    @Autowired
    ExportStoreService exportStoreService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportStoreTask.class);


    @RequestMapping(value = "/executeInteract", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Object> executeInteract() {
        exportStoreService.executeInteract();
        return Results.success();
    }

    @RequestMapping(value = "/executeActivity", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Object> executeActivity() {
        exportStoreService.executeActivity();
        return Results.success();
    }

}
