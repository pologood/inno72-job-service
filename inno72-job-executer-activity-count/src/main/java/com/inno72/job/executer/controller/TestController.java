package com.inno72.job.executer.controller;

import javax.annotation.Resource;

import com.inno72.common.Result;
import com.inno72.common.Results;
import com.inno72.job.executer.mapper.ActivityMapper;
import com.inno72.job.executer.service.DeviceService;
import com.inno72.job.executer.service.ExportStoreService;
import com.inno72.job.executer.service.FeedBackService;
import com.inno72.job.executer.task.MerchantCountByDayTask;
import com.inno72.job.executer.task.MerchantCountTask;

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
    @Autowired
    DeviceService deviceService;

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

    /**
     *
     * @param type 0只执行错误的，1执行所有的
     * @return
     */
    @RequestMapping(value = "/feedback", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Object> feedback(Integer type) {
        feedBackService.feedBackOrder(type);
        return Results.success();
    }
    @Autowired
    private MerchantCountByDayTask merchantCountByDayTask;

	@RequestMapping(value = "/merchantCountByDayTask", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Object> merchantCountByDayTask() throws Exception {
		merchantCountByDayTask.execute("");
        return Results.success();
    }
    @Autowired
    private MerchantCountTask merchantCountTask;
    @RequestMapping(value = "/merchantCountTask", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Object> merchantCountTask() throws Exception {
		merchantCountTask.execute("");
        return Results.success();
    }

    /**
     *
     * @param type 0执行互派，1执行普通活动
     * @return
     */
    @RequestMapping(value = "/adddevice", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Object> adddevice(Integer type) {
        if(type == 0){
            deviceService.executeInteract();
        }else{
            deviceService.executeActivity();
        }
        return Results.success();
    }
}
