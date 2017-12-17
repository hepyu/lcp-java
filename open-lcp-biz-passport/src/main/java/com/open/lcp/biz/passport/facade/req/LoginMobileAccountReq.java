package com.open.lcp.biz.passport.facade.req;

import com.open.lcp.core.api.annotation.LcpParamRequired;

public class LoginMobileAccountReq {

	@LcpParamRequired(value = true, desc = "mobile")
	private String mobile;

	@LcpParamRequired(value = true, desc = "mobileCode")
	private String mobileCode;

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

}
