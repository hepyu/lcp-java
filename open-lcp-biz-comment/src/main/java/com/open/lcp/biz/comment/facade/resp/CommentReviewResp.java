package com.open.lcp.biz.comment.facade.resp;

public class CommentReviewResp extends CommentResp {

	private int typeId;

	private String tid;

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

}
