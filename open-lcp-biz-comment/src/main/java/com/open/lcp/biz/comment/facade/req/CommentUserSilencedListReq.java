package com.open.lcp.biz.comment.facade.req;

import com.open.lcp.core.base.annotation.LcpParamRequired;

public class CommentUserSilencedListReq {

	@LcpParamRequired(desc = "禁言类型 1评论")
	private int userSilencedType;

	@LcpParamRequired(value = false, desc = "操作者名称")
	private String operator;

	@LcpParamRequired(value = false, desc = "被禁言用户的昵称")
	private String nickName;

	@LcpParamRequired(value = false, desc = "被禁言用户的id")
	private long userId;

	@LcpParamRequired(value = false, desc = "当前页码，第一页传1")
	private int pageIndex;

	@LcpParamRequired(desc = "每页大小")
	private int pageSize;

	public int getUserSilencedType() {
		return userSilencedType;
	}

	public void setUserSilencedType(int userSilencedType) {
		this.userSilencedType = userSilencedType;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

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
