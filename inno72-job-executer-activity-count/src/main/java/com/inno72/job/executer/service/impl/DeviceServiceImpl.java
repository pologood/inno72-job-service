package com.inno72.job.executer.service.impl;

import com.inno72.job.executer.config.Constants;
import com.inno72.job.executer.feign.Inno72GameService;
import com.inno72.job.executer.mapper.ActivityMapper;
import com.inno72.job.executer.service.DeviceService;
import com.inno72.job.executer.vo.MachineSellerVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceServiceImpl implements DeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceServiceImpl.class);

    @Autowired
    ActivityMapper activityMapper;

    @Autowired
    Inno72GameService inno72GameService;

    @Override
    public void executeActivity() {
        Long startTime = System.currentTimeMillis();
        LOGGER.info("executeActivity started");
        int currentPage = 1;
        boolean runflag = true;
        int totalsize = 0;
        while(runflag){
            LOGGER.info("executeActivity currentPage ={}",currentPage);
            int size = activityDeviceAdd(currentPage,Constants.PAGE_SIZE);
            if(size < Constants.PAGE_SIZE){
                runflag = false;
            }
            totalsize += size;
            currentPage++;
        }
        LOGGER.info("executeActivity end totalsize={} use time = {}",totalsize,System.currentTimeMillis()-startTime);
    }

    @Override
    public void executeInteract() {
        Long startTime = System.currentTimeMillis();
        LOGGER.info("executeInteract started");
        int currentPage = 1;
        boolean runflag = true;
        int totalsize = 0;
        while(runflag){
            LOGGER.info("exportInteract currentPage ={}",currentPage);
            int size = interactDeviceAdd(currentPage,Constants.PAGE_SIZE);
            if(size < Constants.PAGE_SIZE){
                runflag = false;
            }
            totalsize += size;
            currentPage++;
        }
        LOGGER.info("executeInteract end totalsize={} use time = {}",totalsize,System.currentTimeMillis()-startTime);
    }

    private int interactDeviceAdd(int currentPage, Integer pageSize) {
        List<MachineSellerVo> list = activityMapper.findAddDeviceInteractMachineIdAndSellerId((currentPage-1)*pageSize,pageSize);
        handInno72NeedExportStore(list);
        return list.size();
    }
    private int activityDeviceAdd(int currentPage, Integer pageSize) {
        List<MachineSellerVo> list = activityMapper.findAddDeviceActivityMachineIdAndSellerId((currentPage-1)*pageSize,pageSize);
        handInno72NeedExportStore(list);
        return list.size();
    }
    private void handInno72NeedExportStore(List<MachineSellerVo> list) {
        if(list.size()>0){
            for(MachineSellerVo vo : list){
                if(!StringUtils.isEmpty(vo.getSellerId())&&!StringUtils.isEmpty(vo.getMachineCode())){
                    inno72GameService.saveMachine4Task(vo.getSellerId(),vo.getMachineCode());
                }else{
                    LOGGER.error("数据异常sellerId={},machineCode={}",vo.getSellerId(),vo.getMachineCode());
                }
            }
        }
    }
}
