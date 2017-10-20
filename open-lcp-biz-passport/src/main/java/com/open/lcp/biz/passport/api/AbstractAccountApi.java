package com.open.lcp.biz.passport.api;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.open.lcp.biz.passport.PassportConfig;
import com.open.lcp.biz.passport.PassportException;
import com.open.lcp.biz.passport.UserAccountType;
import com.open.lcp.biz.passport.cache.PassportCache;
import com.open.lcp.biz.passport.dto.BindAccountResultDTO;
import com.open.lcp.biz.passport.dto.PassportUserAccountDTO;
import com.open.lcp.biz.passport.sdk.ThirdAccountSDK;
import com.open.lcp.biz.passport.sdk.ThirdAccountSDKPortrait;
import com.open.lcp.biz.passport.service.AccountInfoService;
import com.open.lcp.biz.passport.service.dao.PassportOAuthAccountDAO;
import com.open.lcp.biz.passport.service.dao.PassportUserAccountDAO;
import com.open.lcp.biz.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.open.lcp.biz.passport.service.dao.entity.PassportUserAccountEntity;
import com.open.lcp.biz.passport.storage.AccountAvatarStorage;
import com.open.lcp.biz.passport.storage.impl.QiniuConfig;
import com.open.lcp.biz.passport.ticket.Ticket;
import com.open.lcp.biz.passport.ticket.TicketManager;
import com.open.lcp.biz.passport.util.PlaceholderAvatarUtil;
import com.open.lcp.common.enums.Gender;
import com.open.lcp.common.util.IPUtil;
import com.open.lcp.core.base.info.BaseUserAccountInfo;

public class AbstractAccountApi {

	protected final Log logger = LogFactory.getLog(AbstractAccountApi.class);

	@Autowired
	protected PassportCache passportCache;

	@Autowired
	protected TicketManager ticketManager;

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

	@Autowired
	protected AccountInfoService accountInfoService;

	@Autowired
	protected PassportOAuthAccountDAO passportOAuthAccountDAO;

	@Autowired
	protected PassportUserAccountDAO passportUserAccountDAO;

	@Autowired
	protected QiniuConfig qiniuConfig;

	@Autowired
	protected PassportConfig passportConfig;

	protected boolean checkMobileCode(String mobile, String code) {
		// TODO
		return true;
	}

	private static final List<Integer> passportCodeNotErrorList;

	static {
		passportCodeNotErrorList = new ArrayList<Integer>();
		passportCodeNotErrorList.add(PassportException.EXCEPTION_BIND_ACCOUNT_HAS_EXIST_OR_SAME_TYPE_HAS_EXIST);
		passportCodeNotErrorList.add(PassportException.EXCEPTION_MOBILE_CODE_INVALID);
		passportCodeNotErrorList.add(PassportException.EXCEPTION_MOBILE_CODE_TYPE_INVALID);
	}

	// index0:userHeadIconUrl, index1:oauthHeadIconUrl
	protected String[] storeHeadIcon(long userId, String headIconUrl, UserAccountType accountType) {
		if (StringUtils.isEmpty(headIconUrl)) {
			String url = PlaceholderAvatarUtil.getPlaceholderAvatarByMod(userId);
			return new String[] { url, url };
		} else {
			String oauthKey = accountAvatarStorage.getOAuthAvatarKey(userId, accountType);
			String oauthUrl = "";
			try {
				// byte[] image = obtainHeadIconImg(headIconUrl, oauthAppId,
				// openId, accessToken, accountType);
				oauthUrl = accountAvatarStorage.fetchResouce(headIconUrl, oauthKey);
				if (oauthUrl == null) {
					oauthUrl = PlaceholderAvatarUtil.getPlaceholderAvatarByMod(userId);
				}
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}

			return new String[] { headIconUrl, oauthUrl };
		}
	}

	// create new record, or update if exists.
	protected Long createAccount(ThirdAccountSDKPortrait userPortrait, String openId, Long userId, String ip,
			UserAccountType accountType, String avatar) throws PassportException {
		long now = System.currentTimeMillis();
		PassportOAuthAccountEntity passportOAuthAccountEntity = newOAuthAccountInstance(userPortrait, openId, userId,
				ip, now, accountType);
		PassportUserAccountEntity passportUserAccountEntity = newPassportUserAccountInstance(userPortrait, userId, ip,
				now, accountType);

		accountInfoService.createAccount(passportUserAccountEntity, passportOAuthAccountEntity);

		if (!StringUtils.isEmpty(avatar)) {
			userId = passportUserAccountEntity.getUserId();
			String[] urls = storeHeadIcon(userId, avatar, accountType);
			userPortrait.setAvatar(urls[0]);
		}
		return passportUserAccountEntity.getUserId();
	}

	protected void login(ThirdAccountSDKPortrait userPortrait, String openId, Long userId, String ip,
			UserAccountType accountType) throws PassportException {
		long now = System.currentTimeMillis();
		PassportOAuthAccountEntity passportOAuthAccountEntity = newOAuthAccountInstance(userPortrait, openId, userId,
				ip, now, accountType);
		PassportUserAccountEntity passportUserAccountEntity = newPassportUserAccountInstance(userPortrait, userId, ip,
				now, accountType);

		accountInfoService.login(passportUserAccountEntity, passportOAuthAccountEntity);
	}

	// insert new record into mysql.
	protected BindAccountResultDTO bindAccount(ThirdAccountSDKPortrait userPortrait, String openId, Long userId,
			String ip, UserAccountType accountType) throws PassportException {

		// store head icon
		String headIconUrl = userPortrait.getAvatar();
		String[] urls = storeHeadIcon(userId, headIconUrl, accountType);
		userPortrait.setAvatar(urls[0]);

		long now = System.currentTimeMillis();
		PassportOAuthAccountEntity passportOAuthAccountEntity = newOAuthAccountInstance(userPortrait, openId, userId,
				ip, now, accountType);

		accountInfoService.bindAccount(passportOAuthAccountEntity);

		BindAccountResultDTO dto = new BindAccountResultDTO();
		dto.setBindSuccess(true);

		PassportUserAccountEntity userAccount = passportUserAccountDAO.getUserInfoByUserId(userId);

		dto.setAvatar(userAccount.getAvatar());
		dto.setGender(Gender.get(userAccount.getGender()));
		dto.setUserName(passportOAuthAccountEntity.getNickName());

		return dto;
	}

	protected Long getUserId(String openId, UserAccountType accountType) throws PassportException {
		return passportOAuthAccountDAO.getUserId(openId, accountType.type());
	}

	/**
	 * @return
	 */
	protected ThirdAccountSDKPortrait obtainThirdAccountSDK(String appId, String openId, String accessToken,
			UserAccountType accountType) throws PassportException {
		if (accountType.name().equalsIgnoreCase(UserAccountType.weichat.toString())) {
			return weichatUserSDK.validateAndObtainUserPortrait(appId, openId, accessToken);
		} else if (accountType.name().equalsIgnoreCase(UserAccountType.xiaomi.toString())) {
			return xiaomiUserSDK.validateAndObtainUserPortrait(appId, openId, accessToken);
		} else if (accountType.name().equalsIgnoreCase(UserAccountType.mobile.toString())) {
			return mobilAccountSDK.validateAndObtainUserPortrait(appId, openId, accessToken);
		} else if (accountType.name().equals(UserAccountType.weibo.name())) {
			return weiboUserSDK.validateAndObtainUserPortrait(appId, openId, accessToken);
		} else if (accountType.name().equals(UserAccountType.qq.name())) {
			return qqUserSDK.validateAndObtainUserPortrait(appId, openId, accessToken);
		} else {
			throw new PassportException(PassportException.EXCEPTION_INVALID_ACCOUNT_TYPE,
					"EXCEPTION_INVALID_ACCOUNT_TYPE", null);
		}
	}

	protected byte[] obtainHeadIconImg(String url, String appId, String openId, String accessToken,
			UserAccountType accountType) throws PassportException {
		if (accountType.name().equalsIgnoreCase(UserAccountType.weichat.toString())) {
			return weichatUserSDK.obtainAvatar(url, appId, openId, accessToken);
		} else if (accountType.name().equalsIgnoreCase(UserAccountType.xiaomi.toString())) {
			return xiaomiUserSDK.obtainAvatar(url, appId, openId, accessToken);
		} else if (accountType.name().equalsIgnoreCase(UserAccountType.mobile.toString())) {
			return mobilAccountSDK.obtainAvatar(url, appId, openId, accessToken);
		} else if (accountType.name().equals(UserAccountType.weibo.name())) {
			return weiboUserSDK.obtainAvatar(url, appId, openId, accessToken);
		} else if (accountType.name().equals(UserAccountType.qq.name())) {
			return qqUserSDK.obtainAvatar(url, appId, openId, accessToken);
		}
		return null;
	}

	protected Ticket checkTicket(String t) throws PassportException {
		Ticket ticketFromClient = ticketManager.decodeTicket(t);
		ticketFromClient.setT(t);

		// TODO need review code
		Ticket ticketInSSDB = null;
		try {
			ticketInSSDB = passportCache.getTicket(t);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			return ticketFromClient;
		}
		if (ticketInSSDB == null) {
			throw new PassportException(PassportException.EXCEPTION_TICKET_INVALID, null);
		} else if (ticketInSSDB.equals(ticketFromClient)) {
			return ticketInSSDB;
		} else {
			throw new PassportException(PassportException.EXCEPTION_TICKET_INVALID, null);
		}
	}

	protected BaseUserAccountInfo obtainPassportUserAccount(Long userId) throws PassportException {
		return accountInfoService.getUserAccountInfo(userId);
	}

	protected void multiDeviceProcess(long xlUserId, String deviceId) {
		// TODO
	}

	private PassportUserAccountEntity newPassportUserAccountInstance(ThirdAccountSDKPortrait userPortrait, Long userId,
			String ip, long time, UserAccountType nickNameType) {
		PassportUserAccountEntity userAccount = new PassportUserAccountEntity();
		userAccount.setAvatar(userPortrait.getAvatar());
		userAccount.setNickName(userPortrait.getNickname());
		userAccount.setRegistIp(IPUtil.Ip2Int(ip));
		userAccount.setRegistTime(time);
		userAccount.setGender(userPortrait.getGender().gender());
		userAccount.setUpdateIp(IPUtil.Ip2Int(ip));
		userAccount.setUpdateTime(time);
		userAccount.setUserName(userPortrait.getUsername());
		userAccount.setUserId(userId);
		return userAccount;
	}

	private PassportOAuthAccountEntity newOAuthAccountInstance(ThirdAccountSDKPortrait userPortrait, String openId,
			Long userId, String ip, long time, UserAccountType accountType) {
		PassportOAuthAccountEntity ssoAccount = new PassportOAuthAccountEntity();
		ssoAccount.setBindIp(IPUtil.Ip2Int(ip));
		ssoAccount.setBindTime(time);
		ssoAccount.setAvatar(userPortrait.getAvatar());
		ssoAccount.setNickName(userPortrait.getNickname());
		ssoAccount.setOpenId(openId);
		ssoAccount.setGender(userPortrait.getGender().gender());
		ssoAccount.setType(accountType.type());
		ssoAccount.setUpdateIp(IPUtil.Ip2Int(ip));
		ssoAccount.setUpdateTime(time);
		ssoAccount.setUserName(userPortrait.getUsername());
		ssoAccount.setUserId(userId);
		return ssoAccount;
	}

}
