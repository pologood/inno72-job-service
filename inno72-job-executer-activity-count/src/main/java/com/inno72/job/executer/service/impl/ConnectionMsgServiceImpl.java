package com.inno72.job.executer.service.impl;

import com.inno72.job.core.util.HttpFormConnector;
import com.inno72.job.executer.mapper.Inno72MachineConnectionMsgMapper;
import com.inno72.job.executer.model.Inno72MachineConnectionMsg;
import com.inno72.job.executer.service.ConnectionMsgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ConnectionMsgServiceImpl implements ConnectionMsgService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionMsgServiceImpl.class);

    @Autowired
    private Inno72MachineConnectionMsgMapper mapper;

    @Autowired
    private ApplicationContext context;

    private final static String pushServerUrlTest="http://pusher.36solo.com";
    private final static String pushServerUrlStage="https://pusher.32solo.com";
    private final static String pushServerUrlProd="https://pusher.inno72.com";

    @Value("${env}")
    private String env;

    @Override
    public void execute(Integer type){
        //查询msg
        List<Inno72MachineConnectionMsg> list = mapper.findUnManageMsgByType(type);
        if(list.size()>0){
            for(Inno72MachineConnectionMsg msg:list){
                try{
                    if(msg.getTimes()<5){
                        //发送长连接
                        sendMsg(msg);
                        //更新次数
                        mapper.updateTimesById(msg.getId(),msg.getTimes()+1);
                    }else{
                        mapper.updateStatusById(msg.getId(),Inno72MachineConnectionMsg.STATUS_ENUM.FAIL.getKey());
                    }
                }catch(Exception e){
                    LOGGER.error(e.getMessage(),e);
                }
            }
        }
    }
    @Override
    public void sendMsg(Inno72MachineConnectionMsg msg) throws IOException {
        String pushServerUrl = getPushServerUrl();
        LOGGER.info("send msg url = {},data={}",pushServerUrl,msg.getMsg());
        byte[] ret = HttpFormConnector.doPost(pushServerUrl,msg.getMsg().getBytes("utf-8"),"application/json; charset=utf-8",10000);
        String response = new String(ret, "utf-8");
        LOGGER.info("send msg response={}",response);
    }

    private String getPushServerUrl() throws IOException {
        String state = context.getEnvironment().getActiveProfiles()[0];
        String method = "/pusher/push/one";
        switch(env) {

            case "test": return pushServerUrlTest+method;
            case "stage": return pushServerUrlStage+method;
            case "prod": return pushServerUrlProd+method;
            default:
                throw new IOException("pushServerUrl:" + state);

        }
    }
}
