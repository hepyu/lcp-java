package com.open.lcp.biz.passport.service;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import com.open.lcp.biz.passport.PassportException;
import com.open.lcp.biz.passport.service.sdk.ThirdAccountSDK;
import com.open.lcp.biz.passport.service.sdk.ThirdAccountSDKPortrait;
import com.open.lcp.biz.passport.storage.AccountAvatarStorage;
import com.open.lcp.biz.passport.util.PlaceholderAvatarUtil;
import com.open.lcp.core.feature.user.api.UserType;

public abstract class AbstractAccountService {

	private final Log logger = LogFactory.getLog(AbstractAccountService.class);

	@Resource(name = "weichatThirdAccountSDK")
	protected ThirdAccountSDK weichatUserSDK;

	@Resource(name = "xiaomiThirdAccountSDK")
	protected ThirdAccountSDK xiaomiUserSDK;

	@Resource(name = "mobileThirdAccountSDK")
	protected ThirdAccountSDK mobilAccountSDK;

	@Resource(name = "weiboThirdAccountSDK")
	private ThirdAccountSDK weiboUserSDK;

	@Resource(name = "qqThirdAccountSDK")
	protected ThirdAccountSDK qqUserSDK;

	@Autowired
	protected AccountAvatarStorage accountAvatarStorage;

	protected void multiDeviceProcess(long xlUserId, String deviceId) {
		// TODO
	}

	/**
	 * @return
	 */
	protected ThirdAccountSDKPortrait obtainThirdAccountSDK(String appId, String openId, String accessToken,
			UserType accountType) throws PassportException {
		if (accountType.name().equalsIgnoreCase(UserType.weichat.toString())) {
			return weichatUserSDK.validateAndObtainUserPortrait(appId, openId, accessToken);
		} else if (accountType.name().equalsIgnoreCase(UserType.xiaomi.toString())) {
			return xiaomiUserSDK.validateAndObtainUserPortrait(appId, openId, accessToken);
		} else if (accountType.name().equalsIgnoreCase(UserType.mobile.toString())) {
			return mobilAccountSDK.validateAndObtainUserPortrait(appId, openId, accessToken);
		} else if (accountType.name().equals(UserType.weibo.name())) {
			return weiboUserSDK.validateAndObtainUserPortrait(appId, openId, accessToken);
		} else if (accountType.name().equals(UserType.qq.name())) {
			return qqUserSDK.validateAndObtainUserPortrait(appId, openId, accessToken);
		} else {
			throw new PassportException(PassportException.EXCEPTION_INVALID_ACCOUNT_TYPE,
					"EXCEPTION_INVALID_ACCOUNT_TYPE", null);
		}
	}

	protected byte[] obtainHeadIconImg(String url, String appId, String openId, String accessToken,
			UserType accountType) throws PassportException {
		if (accountType.name().equalsIgnoreCase(UserType.weichat.toString())) {
			return weichatUserSDK.obtainAvatar(url, appId, openId, accessToken);
		} else if (accountType.name().equalsIgnoreCase(UserType.xiaomi.toString())) {
			return xiaomiUserSDK.obtainAvatar(url, appId, openId, accessToken);
		} else if (accountType.name().equalsIgnoreCase(UserType.mobile.toString())) {
			return mobilAccountSDK.obtainAvatar(url, appId, openId, accessToken);
		} else if (accountType.name().equals(UserType.weibo.name())) {
			return weiboUserSDK.obtainAvatar(url, appId, openId, accessToken);
		} else if (accountType.name().equals(UserType.qq.name())) {
			return qqUserSDK.obtainAvatar(url, appId, openId, accessToken);
		}
		return null;
	}

	// index0:userHeadIconUrl, index1:oauthHeadIconUrl
	protected String[] storeHeadIcon(long userId, String avatar, UserType accountType) {
		if (StringUtils.isEmpty(avatar)) {
			String url = PlaceholderAvatarUtil.getPlaceholderAvatarByMod(userId);
			return new String[] { url, url };
		} else {
			String oauthKey = accountAvatarStorage.getOAuthAvatarKey(userId, accountType);
			String oauthUrl = "";
			try {
				// byte[] image = obtainHeadIconImg(headIconUrl, oauthAppId,
				// openId, accessToken, accountType);
				oauthUrl = accountAvatarStorage.fetchResouce(avatar, oauthKey);
				if (oauthUrl == null) {
					oauthUrl = PlaceholderAvatarUtil.getPlaceholderAvatarByMod(userId);
				}
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}

			return new String[] { avatar, oauthUrl };
		}
	}

}
