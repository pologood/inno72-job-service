package com.inno72.job.core.biz.model;

import java.io.Serializable;


public class ReturnT<T> implements Serializable {
	public static final long serialVersionUID = 42L;

	public static final int SUCCESS_CODE = 0;
	public static final int FAIL_CODE = -1;
	public static final int FAIL_RETRY_CODE = 500;
	public static final ReturnT<Void> SUCCESS = new ReturnT<Void>(SUCCESS_CODE, "ok");
	public static final ReturnT<Void> FAIL = new ReturnT<Void>(FAIL_CODE, "fail");
	public static final ReturnT<Void> FAIL_RETRY = new ReturnT<Void>(FAIL_RETRY_CODE, "retry");

	private int code;
	private String msg = "ok";
	private T data;

	public ReturnT() {
	}

	public ReturnT(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public ReturnT(T data) {
		this.code = SUCCESS_CODE;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ReturnT [code=" + code + ", msg=" + msg + ", data=" + data + "]";
	}

}
