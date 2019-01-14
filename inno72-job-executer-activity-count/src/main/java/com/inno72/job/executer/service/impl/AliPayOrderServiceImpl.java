package com.inno72.job.executer.service.impl;

import com.inno72.common.json.JsonUtil;
import com.inno72.job.core.util.HttpClientUtil;
import com.inno72.job.core.util.HttpFormConnector;
import com.inno72.job.executer.common.util.UrlUtil;
import com.inno72.job.executer.common.util.UuidUtil;
import com.inno72.job.executer.mapper.Inno72MachineConnectionMsgMapper;
import com.inno72.job.executer.mapper.Inno72OrderAlipayMapper;
import com.inno72.job.executer.model.Inno72MachineConnectionMsg;
import com.inno72.job.executer.model.Inno72OrderAlipay;
import com.inno72.job.executer.service.AliPayOrderService;
import com.inno72.job.executer.service.ConnectionMsgService;
import com.inno72.job.executer.util.FastJsonUtils;
import com.inno72.job.executer.vo.Inno72ConnectionPayVo;
import com.inno72.job.executer.vo.PushRequestVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class AliPayOrderServiceImpl implements AliPayOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliPayOrderServiceImpl.class);
    @Autowired
    private Inno72OrderAlipayMapper mapper;
    @Autowired
    private ConnectionMsgService connectionMsgService;
    @Autowired
    private Inno72MachineConnectionMsgMapper inno72MachineConnectionMsgMapper;

    @Value("${env}")
    private String env;
    @Override
    public void execute() throws IOException {
        List<Inno72OrderAlipay> list = mapper.findByStatus(Inno72OrderAlipay.STATUS_ENUM.CREATE.getKey());
        if(list.size()>0){
            for(Inno72OrderAlipay order : list){
                Date date = order.getCreateTime();
                if(System.currentTimeMillis()-date.getTime()<5*60*1000){
                    Boolean flag = getAliOrderPayStatus(order);
                    if(flag){
                        //发送消息
                        sendMsg(order);
                        //修改order状态
                        order.setStatus(Inno72OrderAlipay.STATUS_ENUM.SUCCESS.getKey());
                        mapper.updateByPrimaryKeySelective(order);
                    }
                }
            }
        }
    }

    private Boolean getAliOrderPayStatus(Inno72OrderAlipay order) throws IOException {
//        String url = UrlUtil.getGameServerUrl(env)+"api/standard/orderPolling";
////        LOGGER.info("getAliOrderPayStatus url = {},machineCode={}",url,order.getMachineCode());
////        Map<String, String> form = new HashMap<String, String>(1);
////        form.put("sessionUuid",order.getMachineCode());
////        byte[] ret = HttpFormConnector.doPost(url,form,10000);
////        String response = new String(ret, "utf-8");
////        LOGGER.info("send msg response={}",response);
////        return Boolean.parseBoolean(FastJsonUtils.getString(response,"model"));
        return true;
    }

    private void sendMsg(Inno72OrderAlipay order) throws IOException {
        Inno72ConnectionPayVo payvo = new Inno72ConnectionPayVo();
        PushRequestVo vo = new PushRequestVo();
        vo.setData(JsonUtil.toJson(payvo));
        vo.setTargetCode(order.getMachineCode());
        String request = JsonUtil.toJson(vo);
        Inno72MachineConnectionMsg msg = new Inno72MachineConnectionMsg();
        msg.setMachineCode(order.getMachineCode());
        msg.setStatus(Inno72MachineConnectionMsg.STATUS_ENUM.COMMIT.getKey());
        List<Inno72MachineConnectionMsg> list = inno72MachineConnectionMsgMapper.select(msg);
        if(list.size()>0){
            for(Inno72MachineConnectionMsg inno72MachineConnectionMsg:list){
                inno72MachineConnectionMsgMapper.updateStatusById(inno72MachineConnectionMsg.getId(),Inno72MachineConnectionMsg.STATUS_ENUM.EXPIRE.getKey());
            }
        }
        msg.setMachineCode(order.getMachineCode());
        msg.setStatus(Inno72MachineConnectionMsg.STATUS_ENUM.COMMIT.getKey());
        msg.setId(UuidUtil.getUUID32());
        msg.setActivityId(order.getActivityId());
        msg.setCreateTime(new Date());
        msg.setUpdateTime(msg.getCreateTime());
        msg.setMsg(request);
        msg.setTimes(1);
        msg.setType(Inno72MachineConnectionMsg.TYPE_ENUM.PAY.getKey());
        msg.setVersion(System.currentTimeMillis());
        inno72MachineConnectionMsgMapper.insert(msg);
        connectionMsgService.sendMsg(msg);
    }
}
