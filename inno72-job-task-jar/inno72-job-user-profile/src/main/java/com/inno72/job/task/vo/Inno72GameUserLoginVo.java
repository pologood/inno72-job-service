package com.inno72.job.task.vo;

import com.inno72.job.task.model.Inno72GameUserLogin;

import lombok.Data;

@Data
public class Inno72GameUserLoginVo extends Inno72GameUserLogin {
	private String loginDate;
	private String activityName;
}
