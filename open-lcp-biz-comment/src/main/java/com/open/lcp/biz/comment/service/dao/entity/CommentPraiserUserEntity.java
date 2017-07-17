package com.open.lcp.biz.comment.service.dao.entity;

import com.open.lcp.biz.comment.service.dao.other.CommentPraiser;

public class CommentPraiserUserEntity extends CommentPraiser {

	private long userId;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
}
