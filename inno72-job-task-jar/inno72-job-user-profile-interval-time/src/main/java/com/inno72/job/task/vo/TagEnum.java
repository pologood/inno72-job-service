package com.inno72.job.task.vo;

/**
 * 标签枚举
 */
public enum TagEnum {

	GENDER("gender", "性别"),
	AGE("age", "年龄"),
	PAYMENT("payment", "支付"),
	INTERACTION("interaction", "互动控"),
	ATTEMPT("attempt", "尝鲜族"),
	CITY("city", "城市"),
	POS("pos", "点位"),
	GAMETIME("gametime", "游戏时长"),
	BUYPOWER("buypower", "购买力");

	String value;
	String name;

	TagEnum(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public String getValue() {
		return this.value;
	}

	public String getName() {
		return this.name;
	}
}
