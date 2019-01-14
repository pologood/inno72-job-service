package com.inno72.job.task.vo;

import java.util.Map;

import lombok.Data;

@Data
public class ActivityPointCount {
	private String activityId;
	private String activityName;
	private String lastUpdateTime;

	/**
	 * 010001 扫码登录页
	 * 010002 机器无货浮层
	 * 010003 已领取赠品浮层
	 * 004001 浮层页面， 点击退出
	 * 004002 浮层页面， 点击继续游戏
	 * 010002 游戏规则页面
	 * 010003 选择游戏人数页面
	 * 010004 玩家2 扫码页面
	 * 010005 游戏页面
	 * 010006 挑战成功出货页面
	 * 010007 挑战成功无货页面
	 *
	 * 020001 输入手机号
	 * 020002 获取验证码
	 * 020003 用户信息提交
	 * 020004 扫码进入天猫超市
	 */
	private Map<String, Integer> pointCount;
}
