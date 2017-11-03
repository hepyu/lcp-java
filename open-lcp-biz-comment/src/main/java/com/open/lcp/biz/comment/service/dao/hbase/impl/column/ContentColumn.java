package com.open.lcp.biz.comment.service.dao.hbase.impl.column;

import java.util.List;

import com.open.lcp.biz.comment.facade.resp.CommentReplyResp;

public class ContentColumn {

	private String content;

	private List<CommentReplyResp> reply;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<CommentReplyResp> getReply() {
		return reply;
	}

	public void setReply(List<CommentReplyResp> reply) {
		this.reply = reply;
	}

}
