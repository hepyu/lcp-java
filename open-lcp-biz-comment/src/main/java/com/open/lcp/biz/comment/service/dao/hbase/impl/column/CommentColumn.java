package com.open.lcp.biz.comment.service.dao.hbase.impl.column;

public class CommentColumn {

	private long commentId;
	
	private String IdColumnValue;
	
	private String commentColumnValue;
	
	private String userColumnValue;
	
	private String countColumnValue;
	
	private String extColumnValue;

	public long getCommentId() {
		return commentId;
	}

	public void setCommentId(long commentId) {
		this.commentId = commentId;
	}

	public String getIdColumnValue() {
		return IdColumnValue;
	}

	public void setIdColumnValue(String idColumnValue) {
		IdColumnValue = idColumnValue;
	}

	public String getCommentColumnValue() {
		return commentColumnValue;
	}

	public void setCommentColumnValue(String commentColumnValue) {
		this.commentColumnValue = commentColumnValue;
	}

	public String getUserColumnValue() {
		return userColumnValue;
	}

	public void setUserColumnValue(String userColumnValue) {
		this.userColumnValue = userColumnValue;
	}

	public String getCountColumnValue() {
		return countColumnValue;
	}

	public void setCountColumnValue(String countColumnValue) {
		this.countColumnValue = countColumnValue;
	}

	public String getExtColumnValue() {
		return extColumnValue;
	}

	public void setExtColumnValue(String extColumnValue) {
		this.extColumnValue = extColumnValue;
	}
	
	
}
