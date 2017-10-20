package com.open.lcp.biz.comment.service.dao.entity;

public class CommentConfig {

	private int appId;
	
	private int appCommentId;
	
	private int type;
	
	private int level;
	
	private int floorLevel;
	
	private long addTime;

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public int getAppCommentId() {
		return appCommentId;
	}

	public void setAppCommentId(int appCommentId) {
		this.appCommentId = appCommentId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getFloorLevel() {
		return floorLevel;
	}

	public void setFloorLevel(int floorLevel) {
		this.floorLevel = floorLevel;
	}

	public long getAddTime() {
		return addTime;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}
	
}
