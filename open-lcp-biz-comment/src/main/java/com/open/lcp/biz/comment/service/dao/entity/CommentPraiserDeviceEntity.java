package com.open.lcp.biz.comment.service.dao.entity;

import com.open.lcp.biz.comment.service.dao.other.CommentPraiser;

public class CommentPraiserDeviceEntity extends CommentPraiser {

	private String deviceId;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
}
