package com.open.lcp.biz.comment.service.dao.db.entity;

import com.open.lcp.biz.comment.service.dao.hbase.impl.column.CommentPraiser;

public class CommentPraiserDeviceEntity extends CommentPraiser {

	private String deviceId;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
}
