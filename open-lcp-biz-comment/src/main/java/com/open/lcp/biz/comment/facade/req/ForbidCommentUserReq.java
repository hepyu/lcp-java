package com.open.lcp.biz.comment.facade.req;

import com.open.lcp.core.base.annotation.LcpClassDesc;
import com.open.lcp.core.base.annotation.LcpParamRequired;

@LcpClassDesc("禁言用户")
public class ForbidCommentUserReq {

	@LcpParamRequired(desc = "被禁言的用户", min = 1)
	private long userId;

	private transient String nickName;

	@LcpParamRequired(value = false, desc = "禁言原因")
	private String reason;

	@LcpParamRequired(desc = "禁言天数，正整数，-1是永久禁言，0是取消禁言，每次设置禁言天数会覆盖之前的设定")
	private int silencedDays;

	@LcpParamRequired(desc = "当前操作员名称")
	private String operator;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getSilencedDays() {
		return silencedDays;
	}

	public void setSilencedDays(int silencedDays) {
		this.silencedDays = silencedDays;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	// public long getCtime() {
	// return ctime;
	// }
	//
	// public void setCtime(long ctime) {
	// this.ctime = ctime;
	// }

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	// public long getStart() {
	// return start;
	// }
	//
	// public void setStart(long start) {
	// this.start = start;
	// }
	//
	// public long getEnd() {
	// return end;
	// }
	//
	// public void setEnd(long end) {
	// this.end = end;
	// }
}
