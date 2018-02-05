package com.open.lcp.core.feature.user.api.dto;

import com.open.lcp.core.api.info.CoreFeatureUserAccountTicketInfo;

public class UserTicketDTO implements CoreFeatureUserAccountTicketInfo {

	private Long userId;

	private String userSecretKey;
	
	private String t;

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

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}
	
}
