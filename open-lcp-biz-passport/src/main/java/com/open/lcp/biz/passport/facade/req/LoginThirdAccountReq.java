package com.open.lcp.biz.passport.facade.req;

import com.open.lcp.biz.passport.UserAccountType;
import com.open.lcp.core.api.annotation.LcpParamRequired;

public class LoginThirdAccountReq {

	@LcpParamRequired(value = true, desc = "thirdAppId")
	private String thirdAppId;

	@LcpParamRequired(value = true, desc = "openId")
	private String openId;
	
	@LcpParamRequired(value = true, desc = "accessToken")
	private String accessToken;
	
	@LcpParamRequired(value = true, desc = "accountType")
	private UserAccountType accountType;
	
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

}
