package com.open.lcp.biz.passport.sdk.impl;

import org.springframework.stereotype.Component;

import com.open.lcp.biz.passport.PassportException;
import com.open.lcp.biz.passport.sdk.ThirdAccountSDKPortrait;
import com.open.lcp.core.common.enums.Gender;
import com.open.lcp.core.common.enums.UserType;

@Component("testThirdAccountSDK")
public class TestThirdAccountSDK extends AbstractThirdAccountSDK {

	@Override
	public ThirdAccountSDKPortrait validateAndObtainUserPortrait(String oauthAppId, String openId, String accessToken)
			throws PassportException {

		ThirdAccountSDKPortrait portrait = new ThirdAccountSDKPortrait();

		portrait.setAvatar("http://www.baidu.com");
		portrait.setGender(Gender.female);
		portrait.setNickname("test-nickname");
		portrait.setAvatar(portrait.getAvatar());
		portrait.setUsername("test-username");
		portrait.setUserType(UserType.test);
		return portrait;

	}
}
