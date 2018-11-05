package com.inno72.job.executer.controller;

import com.inno72.common.Result;
import com.inno72.common.Results;
import com.inno72.job.executer.mapper.ActivityMapper;
import com.inno72.job.executer.service.ExportStoreService;
import com.inno72.job.executer.service.FeedBackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    ActivityMapper activityMapper;

    @Autowired
    FeedBackService feedBackService;

    @Autowired
    ExportStoreService exportStoreService;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);


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

    @RequestMapping(value = "/feedback", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Object> feedback(Integer type) {
        feedBackService.feedBackOrder(type);
        return Results.success();
    }

}
