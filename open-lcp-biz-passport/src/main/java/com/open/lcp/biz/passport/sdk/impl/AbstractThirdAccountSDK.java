package com.open.lcp.biz.passport.sdk.impl;

import com.open.lcp.biz.passport.PassportException;
import com.open.lcp.biz.passport.sdk.ThirdAccountSDK;
import com.open.lcp.biz.passport.sdk.ThirdAccountSDKPortrait;
import com.open.lcp.common.enums.Gender;
import com.open.lcp.common.util.ImageUtil;

public abstract class AbstractThirdAccountSDK implements ThirdAccountSDK {

	@Override
	public byte[] obtainAvatar(String url, String appId, String openId, String accessToken) throws PassportException {
		return ImageUtil.getImage(url);
	}

	@Override
	public Long registOrUpdateUserInfo(String userName, String avatar, Gender gender, String description)
			throws PassportException {
		throw new PassportException(PassportException.EXCEPTION_NO_SUPPORT_METHOD, "EXCEPTION_NO_SUPPORT_METHOD", null);
	}

	@Override
	public ThirdAccountSDKPortrait validateAndObtainUserPortrait(String oauthAppId, String openId, String accessToken)
			throws PassportException {
		throw new PassportException(PassportException.EXCEPTION_NO_SUPPORT_METHOD, "EXCEPTION_NO_SUPPORT_METHOD", null);
	}

	@Override
	public Long updateUserInfo(String userName, String avatar, Gender gender, String description)
			throws PassportException {
		throw new PassportException(PassportException.EXCEPTION_NO_SUPPORT_METHOD, "EXCEPTION_NO_SUPPORT_METHOD", null);
	}

	@Override
	public Long validate(String oauthAppId, String openId, String accessToken) throws PassportException {
		throw new PassportException(PassportException.EXCEPTION_NO_SUPPORT_METHOD, "EXCEPTION_NO_SUPPORT_METHOD", null);
	}
}
