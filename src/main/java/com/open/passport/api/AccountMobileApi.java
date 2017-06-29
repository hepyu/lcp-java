package com.open.passport.api;

import com.open.passport.MobileCodeType;
import com.open.passport.dto.LoginByMobileResultDTO;
import com.open.passport.dto.ObtainMobileCodeDTO;

public interface AccountMobileApi {

	public LoginByMobileResultDTO login(String prefix, int appId, String mobile, String mobileCode, String deviceId,
			String ip, String ua);

	public boolean bindMobile(String prefix, int appId, String mobile, String mobileCode, String deviceId, String t,
			String ip);

	public ObtainMobileCodeDTO obtainMobileCode(String ip, String deviceId, int appId, String mobile,
			MobileCodeType type);

}
