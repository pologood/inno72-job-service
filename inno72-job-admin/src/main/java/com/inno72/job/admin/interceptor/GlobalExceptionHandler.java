package com.inno72.job.admin.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.inno72.job.core.biz.model.ReturnT;

/**
 * 全局异常处理
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@SuppressWarnings("rawtypes")
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(Exception.class)
	public ReturnT handleServiceException(Exception ex) {
		logger.error(ex.getMessage(), ex);
		int retCode = -100;
		String msg = "系统错误";
		ReturnT result = new ReturnT(retCode, msg);
		return result;
	}
}
