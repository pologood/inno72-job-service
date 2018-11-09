package com.inno72.job.executer.service.impl;

import com.inno72.common.Result;
import com.inno72.common.Results;
import com.inno72.common.json.JsonUtil;
import com.inno72.common.utils.StringUtil;
import com.inno72.job.executer.config.Constants;
import com.inno72.job.executer.feign.Inno72GameService;
import com.inno72.job.executer.mapper.ActivityMapper;
import com.inno72.job.executer.mapper.Inno72NeedExportStoreMapper;
import com.inno72.job.executer.model.Inno72NeedExportStore;
import com.inno72.job.executer.service.ExportStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ExportStoreServiceImpl implements ExportStoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportStoreServiceImpl.class);
    @Autowired
    ActivityMapper activityMapper;
    @Autowired
    Inno72NeedExportStoreMapper inno72NeedExportStoreMapper;
    @Autowired
    Inno72GameService inno72GameService;
    @Transactional
    @Override
    public int exportInteract(int currentPage, Integer pageSize) {
        List<Inno72NeedExportStore> list = activityMapper.findAllInteractMachineIdAndSellerId((currentPage-1)*pageSize,pageSize);
        handInno72NeedExportStore(list);
        return list.size();
    }

    @Override
    public synchronized void executeInteract() {
        Long startTime = System.currentTimeMillis();
        LOGGER.info("executeInteract started");
        int currentPage = 1;
        boolean runflag = true;
        int totalsize = 0;
        while(runflag){
            LOGGER.info("exportInteract currentPage ={}",currentPage);
            int size = exportInteract(currentPage,Constants.PAGE_SIZE);
            if(size < Constants.PAGE_SIZE){
                runflag = false;
            }
            totalsize += size;
            currentPage++;
        }
        LOGGER.info("executeInteract end totalsize={} use time = {}",totalsize,System.currentTimeMillis()-startTime);
    }

    @Override
    public void executeActivity() {
        Long startTime = System.currentTimeMillis();
        LOGGER.info("executeActivity started");
        //普通活动
        int currentPage = 1;
        boolean runflag = true;
        int totalsize = 0;
        while(runflag){
            LOGGER.info("exportActivity currentPage ={}",currentPage);
            int size = exportActivity(currentPage,Constants.PAGE_SIZE);
            if(size < Constants.PAGE_SIZE){
                runflag = false;
            }
            totalsize += size;
            currentPage++;
        }
        LOGGER.info("executeActivity end totalsize={} use time = {}",totalsize,System.currentTimeMillis()-startTime);
    }

    @Transactional
    public int exportActivity(int currentPage, Integer pageSize) {
        List<Inno72NeedExportStore> list = activityMapper.findAllActivityMachineIdAndSellerId((currentPage-1)*pageSize,pageSize);
        handInno72NeedExportStore(list);
        return list.size();
    }

    public void handInno72NeedExportStore(List<Inno72NeedExportStore> list) {
        if(list.size()>0){
            for(Inno72NeedExportStore store : list){
                //查看是否已经添加
                String storeName = store.getSellerId()+"-"+store.getMachineCode();
                //查看本地库是否存在
                Long size = inno72NeedExportStoreMapper.findDeviceSize(store.getSellerId(),store.getMachineCode());
                if(size == 0){
                    Result result = inno72GameService.findStores(storeName);
                    LOGGER.info("查找门店storeName = {}返回= {}",storeName,JsonUtil.toJson(result));
                    if(null == result.getData()){
                        Inno72NeedExportStore param = new Inno72NeedExportStore();
                        param.setMachineCode(store.getMachineCode());
                        param.setSellerId(store.getSellerId());
                        param.setActivityId(store.getActivityId());
                        List<Inno72NeedExportStore> tmplist = inno72NeedExportStoreMapper.select(param);
                        if(tmplist.size()==0){
                            store.setPhone(getTel());
                            store.setId(StringUtil.getUUID());
                            store.setCreateTime(new Date());
                            inno72NeedExportStoreMapper.insert(store);
                        }
                    }else{
                        Inno72NeedExportStore param = new Inno72NeedExportStore();
                        param.setMachineCode(store.getMachineCode());
                        param.setSellerId(store.getSellerId());
                        inno72NeedExportStoreMapper.delete(store);
                    }
                }else{
                    LOGGER.info("handInno72NeedExportStore storeName 已经存在");
                }
            }
        }
    }

    /**
     * 返回手机号码
     */
    private static String[] telFirst = "134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153"
            .split(",");

    private static String getTel() {
        int index = getNum(0, telFirst.length - 1);
        String first = telFirst[index];
        String second = String.valueOf(getNum(1, 888) + 10000).substring(1);
        String third = String.valueOf(getNum(1, 9100) + 10000).substring(1);
        return first + second + third;
    }

    public static int getNum(int start, int end) {
        return (int) (Math.random() * (end - start + 1) + start);
    }
}
