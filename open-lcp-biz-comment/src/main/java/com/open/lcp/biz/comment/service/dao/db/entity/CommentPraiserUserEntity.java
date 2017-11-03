package com.open.lcp.biz.comment.service.dao.db.entity;

import com.open.lcp.biz.comment.service.dao.hbase.impl.column.CommentPraiser;

public class CommentPraiserUserEntity extends CommentPraiser {

	private long userId;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
}
