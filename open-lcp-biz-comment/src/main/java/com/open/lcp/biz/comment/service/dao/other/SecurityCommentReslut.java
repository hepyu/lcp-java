package com.open.lcp.biz.comment.service.dao.other;

import java.util.List;

public class SecurityCommentReslut {

	private List<SecurityComment> securityComment;
	
	private int count;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<SecurityComment> getSecurityComment() {
		return securityComment;
	}

	public void setSecurityComment(List<SecurityComment> securityComment) {
		this.securityComment = securityComment;
	}
	
	
}
