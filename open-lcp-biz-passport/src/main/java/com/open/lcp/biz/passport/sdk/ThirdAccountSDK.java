package com.open.lcp.biz.passport.sdk;

import com.open.lcp.biz.passport.PassportException;
import com.open.lcp.core.common.enums.Gender;

public interface ThirdAccountSDK {

	public ThirdAccountSDKPortrait validateAndObtainUserPortrait(String oauthAppId, String openId, String accessToken)
			throws PassportException;

	public byte[] obtainAvatar(String url, String oauthAppId, String openId, String accessToken)
			throws PassportException;

	public Long registOrUpdateUserInfo(String userName, String avatar, Gender gender, String description)
			throws PassportException;

	public Long updateUserInfo(String userName, String avatar, Gender gender, String description)
			throws PassportException;

	public Long validate(String oauthAppId, String openId, String accessToken) throws PassportException;

}
