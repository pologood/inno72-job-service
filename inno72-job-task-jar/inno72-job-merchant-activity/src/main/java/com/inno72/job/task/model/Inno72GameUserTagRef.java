package com.inno72.job.task.model;

import java.time.LocalDateTime;

public class Inno72GameUserTagRef {

    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 标签ID
     */
    private String tagId;

    private String tagCode;

    private String content;

    private LocalDateTime createTime;

    private String activityId;

	public Inno72GameUserTagRef(String id, String userId, String tagId, String content, LocalDateTime createTime) {
		this.id = id;
		this.userId = userId;
		this.tagId = tagId;
		this.content = content;
		this.createTime = createTime;
	}

	public Inno72GameUserTagRef() {
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
     * 获取用户ID
     *
     * @return user_id - 用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取标签ID
     *
     * @return tag_id - 标签ID
     */
    public String getTagId() {
        return tagId;
    }

    /**
     * 设置标签ID
     *
     * @param tagId 标签ID
     */
    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    /**
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getTagCode() {
		return tagCode;
	}

	public void setTagCode(String tagCode) {
		this.tagCode = tagCode;
	}
}