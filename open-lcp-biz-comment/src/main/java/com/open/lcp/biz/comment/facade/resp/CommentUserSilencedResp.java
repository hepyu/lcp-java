package com.open.lcp.biz.comment.facade.resp;

import com.open.lcp.core.base.annotation.LcpParamRequired;

public class CommentUserSilencedResp {

	@LcpParamRequired(desc = "被禁言用户编号")
	private long userId;

	@LcpParamRequired(value = false, desc = "被禁言用户名称")
	private String nickName;

	@LcpParamRequired(value = false, desc = "被禁言用户头像")
	private String portrait;

	private transient long end;

	@LcpParamRequired(desc = "禁言状态 -1永久禁言 1禁言3天")
	private int silencedType;

	@LcpParamRequired(value = false, desc = "操作者名称")
	private String operator;

	@LcpParamRequired(value = false, desc = "审核时间")
	private long operateTime;

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

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public int getSilencedType() {
		return silencedType;
	}

	public void setSilencedType(int silencedType) {
		this.silencedType = silencedType;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public long getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(long operateTime) {
		this.operateTime = operateTime;
	}
}
