package com.inno72.job.executer.task;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.executer.service.ConnectionMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@JobHandler("ConnectionScanQrCodeMsgTask")
public class ConnectionScanQrCodeMsgTask extends BaseTask{
    @Autowired
    private ConnectionMsgService service;
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        service.execute(1);
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
    }
}
