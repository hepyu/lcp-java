package com.open.lcp.passport.sdk.impl;

import java.util.List;

import com.open.common.enums.Gender;
import com.open.common.util.ImageUtil;
import com.open.lcp.passport.PassportException;
import com.open.lcp.passport.sdk.PassportAccountPortrait;
import com.open.lcp.passport.sdk.PassportAccountSDK;

public abstract class AbstractPassportAccountSDK implements PassportAccountSDK {

	@Override
	public byte[] obtainHeadIconImg(String url, String appId, String openId,
			String accessToken) throws PassportException {
		return ImageUtil.getImage(url);
	}

	@Override
	public Long registOrUpdateUserInfo(Long passportUserId,
			String userIdentify, String uname, String headIconURL, Gender gender,
			String description, boolean forbidRegist)
			throws PassportException {
		throw new PassportException(
				PassportException.EXCEPTION_NO_SUPPORT_METHOD, "EXCEPTION_NO_SUPPORT_METHOD",null);
	}
	
	@Override
	public Long updateUserInfo(Long passportUserId,
			String userIdentify, String uname, String headIconURL, Gender gender,
			String description, boolean forbidRegist)
			throws PassportException {
		throw new PassportException(
				PassportException.EXCEPTION_NO_SUPPORT_METHOD, "EXCEPTION_NO_SUPPORT_METHOD",null);
	}

	@Override
	public Long validate(String oauthAppId, String openId, String accessToken)
			throws PassportException {
		throw new PassportException(
				PassportException.EXCEPTION_NO_SUPPORT_METHOD, "EXCEPTION_NO_SUPPORT_METHOD",null);
	}

	@Override
	public PassportAccountPortrait getUserInfoWithoutValidation(String oauthAppId,
			String openId, String accessToken) throws PassportException {
		throw new PassportException(
				PassportException.EXCEPTION_NO_SUPPORT_METHOD, "EXCEPTION_NO_SUPPORT_METHOD",null);
	}

	@Override
	public List<PassportAccountPortrait> getUserInfoListWithoutValidation(
			List<Long> xlUserIdList) throws PassportException {
		throw new PassportException(
				PassportException.EXCEPTION_NO_SUPPORT_METHOD, "EXCEPTION_NO_SUPPORT_METHOD",null);
	}

}
