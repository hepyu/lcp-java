package com.open.lcp.biz.passport.api;

import com.open.lcp.biz.passport.MobileCodeType;
import com.open.lcp.biz.passport.dto.LoginByMobileResultDTO;
import com.open.lcp.biz.passport.dto.ObtainMobileCodeDTO;

public interface AccountMobileApi {

	public LoginByMobileResultDTO login(int appId, String mobile, String mobileCode, String deviceId, String ip,
			String ua);

	public boolean bindMobile(int appId, String mobile, String mobileCode, String deviceId, String t, String ip);

	public ObtainMobileCodeDTO obtainMobileCode(String ip, String deviceId, int appId, String mobile,
			MobileCodeType type);

}
