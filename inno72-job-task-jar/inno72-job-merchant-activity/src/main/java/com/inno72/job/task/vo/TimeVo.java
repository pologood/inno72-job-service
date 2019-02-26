package com.inno72.job.task.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TimeVo {
	private LocalDateTime startTime;
	private LocalDateTime endTime;
}
