package com.open.lcp.passport.api;

import com.open.lcp.passport.UserAccountType;
import com.open.lcp.passport.dto.BindAccountResultDTO;
import com.open.lcp.passport.dto.LoginByOAuthResultDTO;
import com.open.lcp.passport.dto.PassportOAuthAccountDTO;

public interface AccountOAuthApi {

	public LoginByOAuthResultDTO login(String prefix, int appId, String oauthAppId, String openId, String accessToken,
			String deviceId, String ip, UserAccountType accountType, String ua, String bisType);

	public BindAccountResultDTO bindAccount(String prefix, int appId, String oauthAppId, String openId,
			String accessToken, String deviceId, String t, UserAccountType accountType, String ip);

	public PassportOAuthAccountDTO getOAuthAccountInfoByXlUserIdAndType(Long xlUserId, UserAccountType accountType);

}
