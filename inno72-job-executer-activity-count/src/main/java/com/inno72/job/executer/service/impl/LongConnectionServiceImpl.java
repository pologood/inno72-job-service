package com.inno72.job.executer.service.impl;

import com.inno72.common.json.JsonUtil;
import com.inno72.job.core.util.HttpClientUtil;
import com.inno72.job.core.util.HttpFormConnector;
import com.inno72.job.executer.common.util.UrlUtil;
import com.inno72.job.executer.mapper.Inno72MachineActivityImageMapper;
import com.inno72.job.executer.mapper.Inno72MachineConnectionMsgMapper;
import com.inno72.job.executer.mapper.ViewActivityMachineMapper;
import com.inno72.job.executer.model.Inno72MachineActivityImage;
import com.inno72.job.executer.model.Inno72MachineConnectionMsg;
import com.inno72.job.executer.model.ViewActivityMachine;
import com.inno72.job.executer.service.LongConnectionService;
import com.inno72.job.executer.vo.ActivityMachineVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Service
public class LongConnectionServiceImpl implements LongConnectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LongConnectionService.class);

    @Value("${env}")
    private String env;

    @Autowired
    private ViewActivityMachineMapper viewActivityMachineMapper;

    @Autowired
    private Inno72MachineActivityImageMapper inno72MachineActivityImageMapper;
    @Autowired
    private Inno72MachineConnectionMsgMapper inno72MachineConnectionMsgMapper;

    @Resource
    private ApplicationContext context;

    @Override
    public void execute() {
        Long version = createVersion();
        Set<ActivityMachineVo> activityMachineSet = new HashSet<ActivityMachineVo>();
        //查找在做的活动
        List<ViewActivityMachine> list = viewActivityMachineMapper.findAll();
        List<String> machineList = viewActivityMachineMapper.findAllMachine();
        if(list.size()>0){
            for(ViewActivityMachine viewActivityMachine:list){
                ActivityMachineVo vo = new ActivityMachineVo();
                vo.setMachineCode(viewActivityMachine.getMachineCode());
                vo.setActivityId(viewActivityMachine.getActivityId());
                vo.setActivityType(viewActivityMachine.getActivityType());
                activityMachineSet.add(vo);
                machineList.remove(viewActivityMachine.getMachineCode());
            }
        }
        if(machineList.size()>0){
            for(String machineCode:machineList){
                ActivityMachineVo vo = new ActivityMachineVo();
                vo.setMachineCode(machineCode);
                vo.setActivityId("0");
                vo.setActivityType(0L);
                activityMachineSet.add(vo);
            }
        }

        //查找machineImage
        List<Inno72MachineActivityImage> inno72MachineActivityImageList = inno72MachineActivityImageMapper.findAll();
        Map<String,Inno72MachineActivityImage> inno72MachineActivityImageMap = transferInno72MachineActivityImage(inno72MachineActivityImageList);
        List<Inno72MachineConnectionMsg> msgList =  inno72MachineConnectionMsgMapper.findUnManageMsg();
        Map<String,Inno72MachineConnectionMsg> msgMap = transferInno72MachineConnectionMsg(msgList);
        //对比
        for(ActivityMachineVo activityMachineVo:activityMachineSet){
            Inno72MachineActivityImage imgae = inno72MachineActivityImageMap.get(activityMachineVo.getMachineCode());
            if(imgae == null || !imgae.getActivityId().equals(activityMachineVo.getActivityId())){
                //查看消息表是否正在发送
                Inno72MachineConnectionMsg msg = msgMap.get(activityMachineVo.getMachineCode());
                if(msg==null){
                    sendMsg(activityMachineVo,version);
                }else if(!msg.getActivityId().equals(activityMachineVo.getActivityId())){
                    inno72MachineConnectionMsgMapper.updateStatusById(msg.getId(),Inno72MachineConnectionMsg.STATUS_ENUM.EXPIRE.getKey());
                    sendMsg(activityMachineVo,version);
                }
            }
        }

    }

    /**
     * 发送长连接消息
     * @param activityMachineVo
     * @param version
     */
    private void sendMsg(ActivityMachineVo activityMachineVo, Long version) {
        Map<String, String> form = new HashMap<String, String>();
        form.put("machineCode",activityMachineVo.getMachineCode());
        form.put("activityId",activityMachineVo.getActivityId());
        form.put("activityType",activityMachineVo.getActivityType()+"");
        try {
            LOGGER.info("changeActivity request={}",JsonUtil.toJson(form));
            byte[] ret = HttpFormConnector.doPost(getGameUrl(),form,10000);
            String retStr = new String(ret, "utf-8");
            LOGGER.info("changeActivity response={}",retStr);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
        }
    }

    private Long createVersion() {
        return System.currentTimeMillis();
    }

    private Map<String,Inno72MachineConnectionMsg> transferInno72MachineConnectionMsg(List<Inno72MachineConnectionMsg> msgList) {
        Map<String,Inno72MachineConnectionMsg> map = new HashMap<String,Inno72MachineConnectionMsg>(msgList.size());
        if(msgList.size()>0){
            for(Inno72MachineConnectionMsg msg : msgList){
                map.put(msg.getMachineCode(),msg);
            }
        }
        return map;
    }

    private Map<String,Inno72MachineActivityImage> transferInno72MachineActivityImage(List<Inno72MachineActivityImage> inno72MachineActivityImageList) {
        Map<String,Inno72MachineActivityImage> inno72MachineActivityImageMap = new HashMap<String,Inno72MachineActivityImage>(inno72MachineActivityImageList.size());
        if(inno72MachineActivityImageList.size()>0){
            for(Inno72MachineActivityImage image:inno72MachineActivityImageList){
                inno72MachineActivityImageMap.put(image.getMachineCode(),image);
            }
        }
        return inno72MachineActivityImageMap;
    }

    private String getGameUrl() throws IOException {

        String method = "/api/connection/changeActivity";
        return UrlUtil.getGameServerUrl(env)+method;
    }
}
