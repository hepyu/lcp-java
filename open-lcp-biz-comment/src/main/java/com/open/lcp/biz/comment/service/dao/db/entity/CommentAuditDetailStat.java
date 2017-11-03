package com.open.lcp.biz.comment.service.dao.db.entity;

public class CommentAuditDetailStat {

	private String commentChecker;
	
	private long checkCount;
	
	private long passCount;
	
	private long nopassCount;
	
	private long onlineCount;
	
	private long offlineCount;

	public String getCommentChecker() {
		return commentChecker;
	}

	public void setCommentChecker(String commentChecker) {
		this.commentChecker = commentChecker;
	}

	public long getCheckCount() {
		return checkCount;
	}

	public void setCheckCount(long checkCount) {
		this.checkCount = checkCount;
	}

	public long getPassCount() {
		return passCount;
	}

	public void setPassCount(long passCount) {
		this.passCount = passCount;
	}

	public long getNopassCount() {
		return nopassCount;
	}

	public void setNopassCount(long nopassCount) {
		this.nopassCount = nopassCount;
	}

	public long getOnlineCount() {
		return onlineCount;
	}

	public void setOnlineCount(long onlineCount) {
		this.onlineCount = onlineCount;
	}

	public long getOfflineCount() {
		return offlineCount;
	}

	public void setOfflineCount(long offlineCount) {
		this.offlineCount = offlineCount;
	}
}
