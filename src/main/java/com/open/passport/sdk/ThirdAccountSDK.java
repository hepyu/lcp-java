package com.open.passport.sdk;

import com.open.common.enums.Gender;
import com.open.passport.PassportException;

public interface ThirdAccountSDK {

	public ThirdAccountSDKPortrait validateAndObtainUserPortrait(String oauthAppId, String openId, String accessToken,
			String bisType) throws PassportException;

	public byte[] obtainAvatar(String url, String oauthAppId, String openId, String accessToken)
			throws PassportException;

	public Long registOrUpdateUserInfo(String userName, String avatar, Gender gender, String description)
			throws PassportException;

	public Long updateUserInfo(String userName, String avatar, Gender gender, String description)
			throws PassportException;

	public Long validate(String oauthAppId, String openId, String accessToken) throws PassportException;

}
