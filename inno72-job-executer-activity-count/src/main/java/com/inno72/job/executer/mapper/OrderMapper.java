package com.inno72.job.executer.mapper;

import com.inno72.job.executer.model.Inno72NeedExportStore;
import com.inno72.job.executer.vo.OrderOrderGoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import java.util.Date;

import com.inno72.job.executer.model.OrderModel;

@Mapper
public interface OrderMapper {
    List<OrderOrderGoodsVo> findSuccessOrder(@Param("currIndex") Integer currentPage,@Param("pageSize") Integer pageSize);

    List<OrderOrderGoodsVo> findByFeedBackErrorLog(@Param("currIndex") Integer currentPage, Integer pageSize);

    void deleteFeedBackErrorLogByOrderId(@Param("orderId") String orderId);
    public List<OrderModel> queryUnpayOrder(@Param("startTime")Date startTime);

    public int updateUnpayOrderStatus(
            @Param("id")String id,
            @Param("payStatus")int payStatus,
            @Param("payTime")Date payTime);
}