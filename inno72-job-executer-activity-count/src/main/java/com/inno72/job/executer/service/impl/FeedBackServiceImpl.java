package com.inno72.job.executer.service.impl;

import com.inno72.job.executer.common.util.DateUtil;
import com.inno72.job.executer.config.Constants;
import com.inno72.job.executer.feign.Inno72GameService;
import com.inno72.job.executer.mapper.Inno72MachineDeviceMapper;
import com.inno72.job.executer.mapper.OrderMapper;
import com.inno72.job.executer.model.Inno72MachineDevice;
import com.inno72.job.executer.service.FeedBackService;
import com.inno72.job.executer.vo.OrderOrderGoodsVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeedBackServiceImpl implements FeedBackService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedBackServiceImpl.class);
    @Autowired
    Inno72GameService inno72GameService;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    Inno72MachineDeviceMapper inno72MachineDeviceMapper;
    public static final Integer EXECUTE_TYPE_ALL=1;
    private static Map<String,String> deviceCodeMap = new HashMap<String,String>();
    @Override
    public void feedBackOrder(Integer type) {
        Long startTime = System.currentTimeMillis();
        LOGGER.info("feedBackOrder started");
        int currentPage = 1;
        boolean runflag = true;
        while(runflag){
            LOGGER.info("feedBackOrder currentPage ={}",currentPage);
            int size = doFeedBackOrder(type,currentPage,Constants.PAGE_SIZE);
            if(size < Constants.PAGE_SIZE){
                runflag = false;
            }
            currentPage++;
        }
        LOGGER.info("feedBackOrder end use time = {}",System.currentTimeMillis()-startTime);
    }
    @Transactional
    public int doFeedBackOrder(Integer type,Integer currentPage, Integer pageSize) {
        currentPage =(currentPage-1)*pageSize;
        //查找需要的order
        List<OrderOrderGoodsVo> list = null;
        if(EXECUTE_TYPE_ALL == type){
            list = findSuccessOrder(currentPage,pageSize);
        }else{
            list = findByFeedBackErrorLog(currentPage,pageSize);
        }
        if(list.size()>0){
            for(OrderOrderGoodsVo orderOrderGoodsVo:list){
                //查找deviceCode
                String deviceCode = findDeviceCode(orderOrderGoodsVo.getMerchantCode(),orderOrderGoodsVo.getMachineCode());
                if(StringUtils.isEmpty(deviceCode)){
                    LOGGER.error("无法找到deviceCode, sellerId = {},machineCode = {}",orderOrderGoodsVo.getMerchantCode(),orderOrderGoodsVo.getMachineCode());
                }else{
                    if(!StringUtils.isEmpty(orderOrderGoodsVo.getTaobaoUserId())){
                        Long stime = 0L;
                        try {
                            String orderTime = DateUtil.format(orderOrderGoodsVo.getOrderTime(),DateUtil.getDatePattern());
                            //调用淘宝回流
                            stime = System.currentTimeMillis();
                            inno72GameService.deviceVendorFeedback(orderOrderGoodsVo.getTaobaoOrderNum(), deviceCode, orderOrderGoodsVo.getTaobaoGoodsId(), orderTime, orderOrderGoodsVo.getTaobaoUserId(), orderOrderGoodsVo.getMerchantName(), orderOrderGoodsVo.getMerchantCode());
//                            //成功删除错误日志
//                            orderMapper.deleteFeedBackErrorLogByOrderId(orderOrderGoodsVo.getTaobaoOrderNum());
                        }catch (Exception e){
                            System.out.println(System.currentTimeMillis()-stime);
                            LOGGER.error("淘宝回流失败",e);
                        }
                    }else{
                        LOGGER.error("userNick is null 不回流接口 taobaoOrderId = {}",orderOrderGoodsVo.getTaobaoOrderNum());
                    }
                }

            }
        }
        return list.size();
    }

    private List<OrderOrderGoodsVo> findByFeedBackErrorLog(Integer currentPage, Integer pageSize) {
        return orderMapper.findByFeedBackErrorLog(currentPage,pageSize);
    }

    private String findDeviceCode(String sellerId, String machineCode) {
        LOGGER.debug("findDeviceCode sellerId = {},machineCode = {}",sellerId,machineCode);
        String key = sellerId+machineCode;
        String deviceCode = deviceCodeMap.get(key);
        if(deviceCode == null){
            Inno72MachineDevice device = new Inno72MachineDevice();
            device.setSellerId(sellerId);
            device.setMachineCode(machineCode);
            List<Inno72MachineDevice> list  = inno72MachineDeviceMapper.select(device);
            if(list.size() == 0) return null;
            deviceCode = list.get(0).getDeviceCode();
            deviceCodeMap.put(key,deviceCode);
        }
        return deviceCode;
    }

    public List<OrderOrderGoodsVo> findSuccessOrder(Integer currentPage, Integer pageSize) {
        return orderMapper.findSuccessOrder(currentPage,pageSize);
    }
}
