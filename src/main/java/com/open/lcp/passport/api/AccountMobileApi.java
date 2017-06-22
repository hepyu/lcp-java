package com.open.lcp.passport.api;

import com.open.lcp.passport.MobileCodeType;
import com.open.lcp.passport.dto.LoginByMobileResultDTO;
import com.open.lcp.passport.dto.ObtainMobileCodeDTO;

public interface AccountMobileApi {

	public LoginByMobileResultDTO login(String prefix, int appId, String mobile, String mobileCode, String deviceId,
			String ip, String ua);

	public boolean bindMobile(String prefix, int appId, String mobile, String mobileCode, String deviceId, String t,
			String ip);

	public ObtainMobileCodeDTO obtainMobileCode(String ip, String deviceId, int appId, String mobile,
			MobileCodeType type);

}
