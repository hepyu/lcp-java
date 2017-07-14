package com.open.lcp.biz.passport.facade.req;

import com.open.lcp.core.framework.annotation.LcpRequired;

public class LoginMobileReq {

	@LcpRequired(value = true, desc = "mobile")
	private String mobile;

	@LcpRequired(value = true, desc = "mobileCode")
	private String mobileCode;

	@LcpRequired(value = true, desc = "deviceId")
	private String deviceId;

	@LcpRequired(value = true, desc = "ip")
	private String ip;

	@LcpRequired(value = true, desc = "ua")
	private String ua;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobileCode() {
		return mobileCode;
	}

	public void setMobileCode(String mobileCode) {
		this.mobileCode = mobileCode;
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
