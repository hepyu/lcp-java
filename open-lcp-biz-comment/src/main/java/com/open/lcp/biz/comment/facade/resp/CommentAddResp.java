package com.open.lcp.biz.comment.facade.resp;

import com.open.lcp.core.base.annotation.LcpParamRequired;

public class CommentAddResp extends PointResultResp {

	@LcpParamRequired(value = false, desc = "评论id")
	private Long cid;

	@LcpParamRequired(value = false, desc = "是否待审核")
	private Boolean isPdRiew = true;

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public Boolean getIsPdRiew() {
		return isPdRiew;
	}

	public void setIsPdRiew(Boolean isPdRiew) {
		this.isPdRiew = isPdRiew;
	}
}
