package com.open.lcp.passport.sdk;

import java.util.List;

import com.open.common.enums.Gender;
import com.open.lcp.passport.PassportException;

public interface PassportAccountSDK {

	public PassportAccountPortrait validateAndObtainUserPortrait(String oauthAppId,
			String openId, String accessToken, String bisType)
			throws PassportException;

	public byte[] obtainHeadIconImg(String url, String oauthAppId,
			String openId, String accessToken) throws PassportException;

	public Long registOrUpdateUserInfo(Long passportUserId,
			String userIdentify, String uname, String headIconURL, Gender gender,
			String description, boolean forbidRegist)
			throws PassportException;
	
	public Long updateUserInfo(Long passportUserId,
			String userIdentify, String uname, String headIconURL, Gender gender,
			String description, boolean forbidRegist)
			throws PassportException;

	public Long validate(String oauthAppId, String openId, String accessToken)
			throws PassportException;

	public PassportAccountPortrait getUserInfoWithoutValidation(String oauthAppId,
			String openId, String accessToken) throws PassportException;

	public List<PassportAccountPortrait> getUserInfoListWithoutValidation(
			List<Long> xlUserIdList) throws PassportException;

}
