package com.inno72.job.executer.task;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.executer.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@JobHandler("DeviceAddTask")
public class DeviceAddTask extends BaseTask {

    @Autowired
    DeviceService deviceService;
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        deviceService.executeInteract();
        deviceService.executeActivity();
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
    }
}
