package com.inno72.job.task.model;

import java.time.LocalDateTime;

public class Inno72GameUser {

	private String id;

	private LocalDateTime createTime;

	public Inno72GameUser() {
		this.createTime = LocalDateTime.now();
	}

	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return create_time
	 */
	public LocalDateTime getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 */
	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

}