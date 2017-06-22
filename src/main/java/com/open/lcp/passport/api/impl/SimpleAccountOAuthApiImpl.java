package com.open.lcp.passport.api.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.open.lcp.passport.ticket.Ticket;
import com.xunlei.mcp.model.ApiException;
import com.xunlei.xlmc.passport.SexEnum;
import com.xunlei.xlmc.passport.UserAccountTypeEnum;
import com.xunlei.xlmc.passport.api.AccountOAuthApi;
import com.xunlei.xlmc.passport.api.PassportApiException;
import com.xunlei.xlmc.passport.bean.BindAccountResult;
import com.xunlei.xlmc.passport.bean.LoginByOAuthResult;
import com.xunlei.xlmc.passport.bean.PassportOAuthAccount;
import com.xunlei.xlmc.passport.sdk.UserPortrait;
import com.xunlei.xlmc.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.xunlei.xlmc.passport.service.dao.entity.PassportUserAccountEntity;
import com.xunlei.xlmc.passport.util.PlaceholderHeadIconUtil;
import com.xunlei.xlmc.passport.util.UserTicketMaker;

@Component
public class SimpleAccountOAuthApiImpl extends AbstractAccount implements AccountOAuthApi {

	private static final Log logger = LogFactory.getLog(SimpleAccountOAuthApiImpl.class);

	@Override
	public LoginByOAuthResultDTO login(String prefix, int appId, String oauthAppId, String openId, String accessToken,
			String deviceId, String ip, UserAccountTypeEnum accountType, String ua, String bisType) {
		try {
			// 1.调用安全中心接口，验证是否允许登陆
			// SafeCheckResult safeCheckResult = SafeCheckResult.SUCCESS;
			// getSafeChecker().checkLogin(ip, null, ua);
			// if (!safeCheckResult.isSuccess()) {
			// resultDTO.setPassportCode(safeCheckResult.getCode());
			// return resultDTO;
			// }

			// 2.获取用户肖像
			UserPortrait userPortrait = obtainUserPortrait(oauthAppId, openId, accessToken, accountType, bisType);
			if (userPortrait == null) {
				// TODO
				throw new ApiException(PassportApiException.EXCEPTION_OBTAIN_PORTRAIT_FAILED);
			}

			// 3.registOrLogin 迅雷账号中心
			Long xlUserIdInPassport = getXlUserId(openId, accountType);
			Long passportUserId = null;
			boolean forbidRegist = true;
			boolean isNewUser = true;
			if (xlUserIdInPassport == null) {
				passportUserId = newPassportUserId();
			} else {
				forbidRegist = false;
				passportUserId = getPassportAccountService().getUserInfoByXlUserId(xlUserIdInPassport)
						.getPassportUserId();
				isNewUser = passportUserId == null ? true : false;
			}

			if (passportUserId == null) {
				throw new PassportApiException(PassportApiException.EXCEPTION_LOGIN_FAILED, null);
			}
			String userIdentify = UserTicketMaker.toKey(passportUserId);
			getPassportCache().setUserIdentify(userIdentify, true);

			// 如果是小米第三方，需要验证头像是否是默认头像
			if (getPassportAccountService().isDefaultHeadIcon(userPortrait.getHeadIconURL())) {
				userPortrait.setHeadIconURL(PlaceholderHeadIconUtil.getPlaceholderHeadIconUrlByMod(passportUserId));
			}

			// store head icon
			String headIconUrl = userPortrait.getHeadIconURL();
			// 这里必须用passportUserId作为headiconurl一部分，因为现在还不知道xluserId
			String[] urls = storeHeadIcon(prefix, passportUserId, headIconUrl, accountType);
			userPortrait.setHeadIconURL(urls[0]);
			userPortrait.setOauthHeadIconURL(urls[1]);

			Long xlUserId = null;

			if (accountType == UserAccountTypeEnum.mobileThunderSubscription) {
				xlUserId = Long.valueOf(openId);
			} else if (accountType == UserAccountTypeEnum.weichat || accountType == UserAccountTypeEnum.xiaomi
					|| accountType == UserAccountTypeEnum.qq || accountType == UserAccountTypeEnum.weibo) {
				xlUserId = getXunleiUserCenterSDK().registOrUpdateUserInfo(passportUserId, userIdentify,
						userPortrait.getNickname(), userPortrait.getHeadIconURL(), userPortrait.getSex(), "",
						forbidRegist);
			} else {
				throw new PassportApiException(PassportApiException.EXCEPTION_INVALID_ACCOUNT_TYPE, "type error.",
						null);
			}

			if (forbidRegist) {
				if (xlUserIdInPassport != null && xlUserIdInPassport.longValue() != xlUserId.longValue()) {
					throw new PassportApiException(PassportApiException.EXCEPTION_XUNLEI_USERID_ERROR, null);
				}
			} else {
				if (xlUserId == null) {
					xlUserId = xlUserIdInPassport;
				}
			}

			if (xlUserId == null) {
				throw new PassportApiException(PassportApiException.EXCEPTION_XL_RETURN_USERID_NULL, null);
			}

			// 4.mysql中创建用户记录,每次都需要更新，因为headIconURL可能会变，同时更新update_ip,
			// update_time
			createOrUpdateAccount(prefix, userPortrait, openId, xlUserId, passportUserId, ip, accountType);

			String description = getPassportUserAccountDao().getUserInfoByXlUserId(xlUserId).get(0).getDescription();

			// 5.处理相同用户不同设备之间,或者相同用户相同设备的多次登陆的登陆互踢逻辑
			multiDeviceProcess(xlUserId, deviceId);

			// 6.生成sk,uk
			Ticket couple = getTicketManager().createSecretKeyCouple(appId, xlUserId);
			PassportOAuthAccountEntity mobileAccount = getPassportAccountService()
					.getOAuthAccountInfoByXlUserIdAndType(xlUserId, UserAccountTypeEnum.mobile);
			PassportUserAccountEntity userAccount = getPassportAccountService().getUserInfoByXlUserId(xlUserId);

			LoginByOAuthResultDTO resultDTO = new LoginByOAuthResultDTO();
			resultDTO.setHeadIconUrl(userAccount.getHeadIconUrl());
			resultDTO.setGender(SexEnum.valueOf(userAccount.getSexEnum()));
			resultDTO.setUserId(xlUserId);
			resultDTO.setUserName(userAccount.getNickName());
			resultDTO.setUserSecretKey(couple.getUserSecretKey());
			resultDTO.setT(couple.getT());
			resultDTO.setBindMobile(mobileAccount == null ? "" : mobileAccount.getOpenId());
			resultDTO.setDescription(description);
			resultDTO.setNewUser(isNewUser);
			return resultDTO;
		} catch (PassportApiException pae) {
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
		}
	}

	@Override
	public BindAccountResultDTO bindAccount(String prefix, int appId, String oauthAppId, String openId, String accessToken,
			String deviceId, String t, UserAccountTypeEnum accountType, String ip) {
		try {
			Ticket couple = super.checkTicket(t);

			UserPortrait userPortrait = obtainUserPortrait(oauthAppId, openId, accessToken, accountType, null);
			BindAccountResultDTO bindResult = super.bindAccount(prefix, userPortrait, openId, couple.getXlUserId(), ip,
					accountType);

			return bindResult;
		} catch (PassportApiException pae) {
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
		}
	}

	@Override
	public PassportOAuthAccountDTO getOAuthAccountInfoByXlUserIdAndType(Long xlUserId, UserAccountTypeEnum type) {
		try {
			PassportOAuthAccountEntity entity = getPassportAccountService()
					.getOAuthAccountInfoByXlUserIdAndType(xlUserId, type);
			if (entity == null) {
				return null;
			} else {
				PassportOAuthAccountDTO oauthAccount = new PassportOAuthAccountDTO();
				oauthAccount.setBindIp(entity.getBindIp());
				oauthAccount.setBindTime(entity.getBindTime());
				oauthAccount.setHeadIconUrl(entity.getHeadIconUrl());
				oauthAccount.setNickName(entity.getNickName());
				oauthAccount.setOpenId(entity.getOpenId());
				oauthAccount.setGender(SexEnum.valueOf(SexEnum.valueOf(entity.getSex())));
				oauthAccount.setType(UserAccountTypeEnum.valueOf(entity.getType()));
				oauthAccount.setUpdateIp(entity.getUpdateIp());
				oauthAccount.setUpdateTime(entity.getUpdateTime());
				oauthAccount.setUserName(entity.getUserName());
				oauthAccount.setXlUserId(entity.getXlUserId());
				return oauthAccount;
			}
		} catch (PassportApiException pae) {
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
		}
	}

	// @Override
	// public boolean createMobileThunderSubscriptionUser(Long xlUserId, String
	// ip) {
	// try {
	// UserPortrait userPortrait = getMobileThunderSDK()
	// .getUserInfoWithoutValidation(null, xlUserId + "", null);
	//
	// if (userPortrait == null) {
	// // TODO
	// throw new ApiException(
	// PassportApiException.EXCEPTION_OBTAIN_PORTRAIT_FAILED);
	// }
	//
	// Long xlUserIdInPassport = getXlUserId(xlUserId + "",
	// UserAccountTypeEnum.mobileThunderSubscription);
	// Long passportUserId = null;
	// if (xlUserIdInPassport == null) {
	// passportUserId = newPassportUserId();
	// } else {
	// passportUserId = getPassportAccountService()
	// .getUserInfoByXlUserId(xlUserIdInPassport)
	// .getPassportUserId();
	// }
	//
	// if (passportUserId == null) {
	// throw new PassportApiException(
	// PassportApiException.EXCEPTION_LOGIN_FAILED, null);
	// }
	// String userIdentify = UserTicketMaker.toKey(passportUserId);
	// getPassportCache().setUserIdentify(userIdentify, true);
	//
	// // store head icon
	// String headIconUrl = userPortrait.getHeadIconURL();
	// // 这里必须用passportUserId作为headiconurl一部分，因为现在还不知道xluserId
	// userPortrait.setHeadIconURL(storeHeadIcon(passportUserId,
	// headIconUrl));
	//
	// String openId = xlUserId + "";
	// createOrUpdateAccount(userPortrait, openId, xlUserId,
	// passportUserId, ip,
	// UserAccountTypeEnum.mobileThunderSubscription);
	//
	// return true;
	// } catch (PassportApiException pae) {
	// log(pae, logger);
	// throw new ApiException(pae.getPassportCode(), pae.getMessage());
	// }
	// }
}
