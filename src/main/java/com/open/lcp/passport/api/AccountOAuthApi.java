package com.open.lcp.passport.api;

import com.open.lcp.passport.UserAccountType;
import com.open.lcp.passport.dto.BindAccountResultDTO;
import com.open.lcp.passport.dto.LoginByOAuthResultDTO;

public interface AccountOAuthApi {

	public LoginByOAuthResultDTO login(int appId, String oauthAppId, String openId, String accessToken, String deviceId,
			String ip, UserAccountType accountType, String ua);

	public BindAccountResultDTO bindAccount(int appId, String oauthAppId, String openId, String accessToken,
			String deviceId, String t, UserAccountType accountType, String ip);

	// public PassportOAuthAccountDTO getOAuthAccountInfoByUserIdAndType(Long
	// userId, UserAccountType accountType);

}
