package com.open.lcp.biz.comment.facade.req;

import com.open.lcp.core.api.annotation.LcpParamRequired;

public class CommentUserSilencedLogReq {

	@LcpParamRequired(desc = "被禁言用户编号", min = 1)
	private long userId;

	@LcpParamRequired(value = false, desc = "页码", min = 0)
	private int pageIndex;

	@LcpParamRequired(value = false, desc = "页面大小", max = 100)
	private int pageSize;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
