package com.open.lcp.biz.passport.facade.req;

import com.open.lcp.core.base.annotation.LcpParamRequired;

public class LoginMobileReq {

	@LcpParamRequired(value = true, desc = "mobile")
	private String mobile;

	@LcpParamRequired(value = true, desc = "mobileCode")
	private String mobileCode;

	@LcpParamRequired(value = true, desc = "deviceId")
	private String deviceId;

	@LcpParamRequired(value = true, desc = "ip")
	private String ip;

	@LcpParamRequired(value = true, desc = "ua")
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
