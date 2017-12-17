package com.open.lcp.biz.comment.facade.resp;

import com.open.lcp.core.api.annotation.LcpParamRequired;

public class CommentReplyResp {

	@LcpParamRequired(value = true, desc = "被回复的评论id")
	private Long cid;

	@LcpParamRequired(value = true, desc = "被回复的内容")
	private String content;

	@LcpParamRequired(value = true, desc = "被回复的用户id")
	private long uid;

	@LcpParamRequired(value = true, desc = "被回复的用户名")
	private String user;

	@LcpParamRequired(value = true, desc = "被回复的用户头像")
	private String userImg;

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUserImg() {
		return userImg;
	}

	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}

}
