package com.open.lcp.passport.dto;

public class CheckTicket {

	private Long xlUserId;

	private String userSecretKey;

	public Long getXlUserId() {
		return xlUserId;
	}

	public void setXlUserId(Long xlUserId) {
		this.xlUserId = xlUserId;
	}

	public String getUserSecretKey() {
		return userSecretKey;
	}

	public void setUserSecretKey(String userSecretKey) {
		this.userSecretKey = userSecretKey;
	}

}
