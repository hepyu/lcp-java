package com.open.lcp.biz.passport.dto;

import com.open.lcp.core.base.info.BaseUserAccountTicketInfo;

public class UserAccountTicket implements BaseUserAccountTicketInfo {

	private Long userId;

	private String userSecretKey;

	public String getUserSecretKey() {
		return userSecretKey;
	}

	public void setUserSecretKey(String userSecretKey) {
		this.userSecretKey = userSecretKey;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
