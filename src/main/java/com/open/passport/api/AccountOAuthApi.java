package com.open.passport.api;

import com.open.passport.UserAccountType;
import com.open.passport.dto.BindAccountResultDTO;
import com.open.passport.dto.LoginByOAuthResultDTO;
import com.open.passport.service.dao.PassportOAuthAccountDAO;

public interface AccountOAuthApi {

	public LoginByOAuthResultDTO login(String prefix, int appId, String oauthAppId, String openId, String accessToken,
			String deviceId, String ip, UserAccountType accountType, String ua, String bisType);

	public BindAccountResultDTO bindAccount(String prefix, int appId, String oauthAppId, String openId,
			String accessToken, String deviceId, String t, UserAccountType accountType, String ip);

	public PassportOAuthAccountDAO getOAuthAccountInfoByUserIdAndType(Long xlUserId, UserAccountType accountType);

}
