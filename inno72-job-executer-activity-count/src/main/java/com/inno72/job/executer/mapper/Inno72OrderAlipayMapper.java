package com.inno72.job.executer.mapper;

import com.inno72.job.executer.model.Inno72OrderAlipay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface Inno72OrderAlipayMapper extends tk.mybatis.mapper.common.Mapper<Inno72OrderAlipay> {
    List<Inno72OrderAlipay> findByStatus(@Param("status") Integer status);
}