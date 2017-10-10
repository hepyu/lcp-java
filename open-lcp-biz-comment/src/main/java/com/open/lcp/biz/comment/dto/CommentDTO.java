package com.open.lcp.biz.comment.dto;

public class CommentDTO {

	private long cid;

	private String idColumn;

	private String extColumn;

	private String userColumn;

	private String contentColumn;

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public String getIdColumn() {
		return idColumn;
	}

	public void setIdColumn(String idColumn) {
		this.idColumn = idColumn;
	}

	public String getExtColumn() {
		return extColumn;
	}

	public void setExtColumn(String extColumn) {
		this.extColumn = extColumn;
	}

	public String getUserColumn() {
		return userColumn;
	}

	public void setUserColumn(String userColumn) {
		this.userColumn = userColumn;
	}

	public String getContentColumn() {
		return contentColumn;
	}

	public void setContentColumn(String contentColumn) {
		this.contentColumn = contentColumn;
	}
}
