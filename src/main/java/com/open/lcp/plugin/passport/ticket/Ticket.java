package com.open.lcp.plugin.passport.ticket;

public class Ticket {

	private String t;

	private String userSecretKey;

	private Integer appId;

	private Long userId;

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

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

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	@Override
	public boolean equals(Object obj) {
		Ticket ticket = (Ticket) obj;
		if (this.t.equals(ticket.getT()) && this.appId.intValue() == ticket.getAppId().intValue()
				&& this.userId.longValue() == ticket.getUserId().longValue()) {
			return true;
		}
		return false;
	}
}
