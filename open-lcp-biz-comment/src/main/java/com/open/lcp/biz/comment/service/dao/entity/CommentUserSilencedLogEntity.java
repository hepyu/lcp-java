package com.open.lcp.biz.comment.service.dao.entity;

public class CommentUserSilencedLogEntity {

	// 禁言日志编号
	private long commentUserSilencedLogId;

	// 禁言用户编号
	private long userId;

	// 禁言天数 -1是永久
	private int silencedDays;

	// 禁言原因
	private String reason;

	// 禁言开始时间
	private long start;

	// 禁言结束时间
	private long end;

	// 操作人
	private String operator;

	// 创建时间
	private long ctime;

	public long getCommentUserSilencedLogId() {
		return commentUserSilencedLogId;
	}

	public void setCommentUserSilencedLogId(long commentUserSilencedLogId) {
		this.commentUserSilencedLogId = commentUserSilencedLogId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getSilencedDays() {
		return silencedDays;
	}

	public void setSilencedDays(int silencedDays) {
		this.silencedDays = silencedDays;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public long getCtime() {
		return ctime;
	}

	public void setCtime(long ctime) {
		this.ctime = ctime;
	}
}
