package com.inno72.job.executer.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.inno72.job.executer.model.OrderModel;

@Mapper
public interface OrderMapper {
	
	public List<OrderModel> queryUnpayOrder(@Param("startTime")Date startTime);
	
	public int updateUnpayOrderStatus(
			@Param("id")String id, 
			@Param("payStatus")int payStatus, 
			@Param("payTime")Date payTime);
	
}
