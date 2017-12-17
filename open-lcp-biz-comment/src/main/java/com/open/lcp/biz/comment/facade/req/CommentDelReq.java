package com.open.lcp.biz.comment.facade.req;

import com.open.lcp.core.api.annotation.LcpParamRequired;

public class CommentDelReq extends CommentBaseReq {

	@LcpParamRequired(value = true, desc = "评论id")
	private long cid;

	@LcpParamRequired(value = false, desc = "给后台在未登录的情况下指定userId来删除")
	private long userId;

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
}
