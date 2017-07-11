package com.open.lcp.plugin.passport.sdk.impl;

import org.springframework.stereotype.Component;

import com.open.lcp.plugin.passport.PassportException;
import com.open.lcp.plugin.passport.sdk.ThirdAccountSDKPortrait;

@Component("mobileThirdAccountSDK")
public class MobileThirdAccountSDK extends AbstractThirdAccountSDK {

	@Override
	public ThirdAccountSDKPortrait validateAndObtainUserPortrait(String oauthAppId, String openId, String accessToken)
			throws PassportException {
		throw new PassportException(PassportException.EXCEPTION_NO_SUPPORT_METHOD, "no suppoprt.", null);
	}

}
