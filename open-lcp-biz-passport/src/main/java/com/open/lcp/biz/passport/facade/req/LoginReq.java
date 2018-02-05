package com.open.lcp.biz.passport.facade.req;

import com.open.lcp.core.api.annotation.LcpParamRequired;
import com.open.lcp.core.feature.user.api.UserType;

public class LoginReq {

	@LcpParamRequired(value = true, desc = "thirdAppId")
	private String thirdAppId;

	@LcpParamRequired(value = true, desc = "openId")
	private String openId;

	@LcpParamRequired(value = true, desc = "accessToken")
	private String accessToken;

	@LcpParamRequired(value = true, desc = "accountType")
	private UserType accountType;

	private String mobileCode;

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

	public UserType getAccountType() {
		return accountType;
	}

	public void setAccountType(UserType accountType) {
		this.accountType = accountType;
	}

	public String getMobileCode() {
		return mobileCode;
	}

	public void setMobileCode(String mobileCode) {
		this.mobileCode = mobileCode;
	}

}
