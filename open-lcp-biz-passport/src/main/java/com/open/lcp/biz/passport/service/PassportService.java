package com.open.lcp.biz.passport.service;

import com.open.lcp.biz.passport.dto.LoginResultDTO;
import com.open.lcp.biz.passport.dto.ObtainMobileCodeResultDTO;
import com.open.lcp.biz.passport.dto.RequestUploadAvatarResultDTO;
import com.open.lcp.core.feature.user.api.MobileCodeType;
import com.open.lcp.core.feature.user.api.UserType;
import com.open.lcp.core.feature.user.api.dto.BindThirdAccountResultDTO;

public interface PassportService {

	public LoginResultDTO login(int appId, String oauthAppId, String openId, String accessToken, String deviceId,
			String ip, UserType accountType, String ua, String mobileCode);

	public BindThirdAccountResultDTO bindThirdAccount(int appId, String oauthAppId, String openId, String accessToken,
			String deviceId, String t, UserType accountType, String ip, String mobileCode);

	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId);

	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId, UserType accountType);

	public ObtainMobileCodeResultDTO obtainMobileCode(String ip, String deviceId, int appId, String mobile,
			MobileCodeType type);

}
