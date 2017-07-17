package com.open.lcp.biz.comment.facade.req;

import com.open.lcp.core.base.annotation.LcpParamRequired;

public class CommentBaseReq {

	@LcpParamRequired(desc = "评论资源 id,由应用接入方自己计算,不重复即可, 如果是短视频统一采用gcid")
	private String tid;

	@LcpParamRequired(desc = "评论类型id,可以理解为业务类型. 目前接入的有 1:短视频 ,2:图文,3:收藏站点,4:迅雷9下载资源,5:url解析")
	private int typeId;

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
}
