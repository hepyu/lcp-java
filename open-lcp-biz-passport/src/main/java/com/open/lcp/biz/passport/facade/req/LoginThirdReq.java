package com.open.lcp.biz.passport.facade.req;

import com.open.lcp.biz.passport.UserAccountType;
import com.open.lcp.core.base.annotation.LcpParamRequired;

public class LoginThirdReq {

	@LcpParamRequired(value = true, desc = "thirdAppId")
	private String thirdAppId;

	@LcpParamRequired(value = true, desc = "openId")
	private String openId;
	
	@LcpParamRequired(value = true, desc = "accessToken")
	private String accessToken;
	
	@LcpParamRequired(value = true, desc = "accountType")
	private UserAccountType accountType;
	
	@LcpParamRequired(value = true, desc = "deviceId")
	private String deviceId;

	@LcpParamRequired(value = true, desc = "ip")
	private String ip;

	@LcpParamRequired(value = true, desc = "ua")
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
