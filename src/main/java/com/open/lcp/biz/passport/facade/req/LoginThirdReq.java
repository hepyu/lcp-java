package com.open.lcp.biz.passport.facade.req;

import com.open.lcp.biz.passport.UserAccountType;
import com.open.lcp.core.framework.annotation.LcpRequired;

public class LoginThirdReq {

	@LcpRequired(value = true, desc = "thirdAppId")
	private String thirdAppId;

	@LcpRequired(value = true, desc = "openId")
	private String openId;
	
	@LcpRequired(value = true, desc = "accessToken")
	private String accessToken;
	
	@LcpRequired(value = true, desc = "accountType")
	private UserAccountType accountType;
	
	@LcpRequired(value = true, desc = "deviceId")
	private String deviceId;

	@LcpRequired(value = true, desc = "ip")
	private String ip;

	@LcpRequired(value = true, desc = "ua")
	private String ua;

	public String getThirdAppId() {
		return thirdAppId;
	}

	public void setThirdAppId(String thirdAppId) {
		this.thirdAppId = thirdAppId;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public UserAccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(UserAccountType accountType) {
		this.accountType = accountType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUa() {
		return ua;
	}

	public void setUa(String ua) {
		this.ua = ua;
	}

}
