package com.open.lcp.biz.passport.api.impl;

import org.springframework.stereotype.Component;

import com.open.lcp.biz.passport.PassportException;
import com.open.lcp.biz.passport.UserAccountType;
import com.open.lcp.biz.passport.api.AbstractAccountApi;
import com.open.lcp.biz.passport.api.AccountOAuthApi;
import com.open.lcp.biz.passport.dto.BindAccountResultDTO;
import com.open.lcp.biz.passport.dto.LoginByOAuthResultDTO;
import com.open.lcp.biz.passport.dto.PassportUserAccountDTO;
import com.open.lcp.biz.passport.sdk.ThirdAccountSDKPortrait;
import com.open.lcp.biz.passport.ticket.Ticket;

@Component
public class SimpleAccountOAuthApiImpl extends AbstractAccountApi implements AccountOAuthApi {

	@Override
	public LoginByOAuthResultDTO login(int appId, String oauthAppId, String openId, String accessToken, String deviceId,
			String ip, UserAccountType accountType, String ua) {
		// 1.调用安全中心接口，验证是否允许登陆
		// SafeCheckResult safeCheckResult = SafeCheckResult.SUCCESS;
		// getSafeChecker().checkLogin(ip, null, ua);
		// if (!safeCheckResult.isSuccess()) {
		// resultDTO.setPassportCode(safeCheckResult.getCode());
		// return resultDTO;
		// }

		// 2.获取用户肖像
		ThirdAccountSDKPortrait userPortrait = obtainThirdAccountSDK(oauthAppId, openId, accessToken, accountType);
		if (userPortrait == null) {
			// TODO
			throw new PassportException(PassportException.EXCEPTION_OBTAIN_PORTRAIT_FAILED,
					"EXCEPTION_OBTAIN_PORTRAIT_FAILED", null);
		}

		// 3.registOrLogin

		// store head icon
		String headIconUrl = userPortrait.getAvatar();
		// 这里必须用passportUserId作为headiconurl一部分，因为现在还不知道userId

		Long userId = getUserId(openId, accountType);
		if (userId == null) {
			super.createAccount(userPortrait, openId, userId, ip, accountType, headIconUrl);
		} else {
			super.login(userPortrait, openId, userId, ip, accountType);
		}

		// 5.处理相同用户不同设备之间,或者相同用户相同设备的多次登陆的登陆互踢逻辑
		multiDeviceProcess(userId, deviceId);

		// 6.生成sk,uk
		Ticket couple = ticketManager.createSecretKeyCouple(appId, userId);

		PassportUserAccountDTO userAccount = accountInfoService.getUserInfo(userId);

		LoginByOAuthResultDTO resultDTO = new LoginByOAuthResultDTO();
		resultDTO.setAvatar(userAccount.getAvatar());
		resultDTO.setGender(userAccount.getGender());
		resultDTO.setUserId(userId);
		resultDTO.setUserName(userAccount.getNickName());
		resultDTO.setUserSecretKey(couple.getUserSecretKey());
		resultDTO.setT(couple.getT());
		resultDTO.setDescription(userAccount.getDescription());
		return resultDTO;
	}

	@Override
	public BindAccountResultDTO bindAccount(int appId, String oauthAppId, String openId, String accessToken,
			String deviceId, String t, UserAccountType accountType, String ip) {
		Ticket couple = super.checkTicket(t);

		ThirdAccountSDKPortrait userPortrait = obtainThirdAccountSDK(oauthAppId, openId, accessToken, accountType);
		BindAccountResultDTO bindResult = super.bindAccount(userPortrait, openId, couple.getUserId(), ip, accountType);

		return bindResult;
	}

	// @Override
	// public PassportOAuthAccountDTO getOAuthAccountInfoByUserIdAndType(Long
	// userId, UserAccountType type) {
	// PassportOAuthAccountEntity entity =
	// accountInfoService.getOAuthAccountInfoByUserIdAndType(userId, type);
	// if (entity == null) {
	// return null;
	// } else {
	// PassportOAuthAccountDTO oauthAccount = new PassportOAuthAccountDTO();
	// oauthAccount.setBindIp(entity.getBindIp());
	// oauthAccount.setBindTime(entity.getBindTime());
	// oauthAccount.setAvatar(entity.getAvatar());
	// oauthAccount.setNickName(entity.getNickName());
	// oauthAccount.setOpenId(entity.getOpenId());
	// oauthAccount.setGender(Gender.get(entity.getGender()));
	// oauthAccount.setType(UserAccountType.valueOf(entity.getType()));
	// oauthAccount.setUpdateIp(entity.getUpdateIp());
	// oauthAccount.setUpdateTime(entity.getUpdateTime());
	// oauthAccount.setUserName(entity.getUserName());
	// oauthAccount.setUserId(entity.getUserId());
	// return oauthAccount;
	// }
	// }

}
