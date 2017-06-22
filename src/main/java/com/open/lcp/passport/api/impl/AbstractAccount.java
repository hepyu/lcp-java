package com.open.lcp.passport.api.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.open.lcp.passport.ticket.Ticket;
import com.open.lcp.passport.ticket.TicketManager;
import com.xunlei.xlmc.passport.PassportConfig;
import com.xunlei.xlmc.passport.SexEnum;
import com.xunlei.xlmc.passport.UserAccountTypeEnum;
import com.xunlei.xlmc.passport.api.PassportApiException;
import com.xunlei.xlmc.passport.bean.BindAccountResult;
import com.xunlei.xlmc.passport.cache.PassportCache;
import com.xunlei.xlmc.passport.common.http.CommonHttpClient;
import com.xunlei.xlmc.passport.component.safecenter.SafeChecker;
import com.xunlei.xlmc.passport.component.storage.PassportStorage;
import com.xunlei.xlmc.passport.component.storage.impl.QiniuConfig;
import com.xunlei.xlmc.passport.sdk.UserAccountSDK;
import com.xunlei.xlmc.passport.sdk.UserPortrait;
import com.xunlei.xlmc.passport.service.PassportAccountService;
import com.xunlei.xlmc.passport.service.dao.PassportOAuthAccountDao;
import com.xunlei.xlmc.passport.service.dao.PassportUserAccountDao;
import com.xunlei.xlmc.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.xunlei.xlmc.passport.service.dao.entity.PassportUserAccountEntity;
import com.xunlei.xlmc.passport.util.IPUtil;
import com.xunlei.xlmc.passport.util.NickNameUtil;
import com.xunlei.xlmc.passport.util.PlaceholderHeadIconUtil;

public abstract class AbstractAccount {

	private final Log logger = LogFactory.getLog(AbstractAccount.class);

	@Autowired
	private PassportCache passportCache;

	@Autowired
	private TicketManager ticketManager;

	@Autowired
	private SafeChecker safeChecker;

	@Resource(name = "xunleiUserCenterSDK")
	private UserAccountSDK xunleiUserCenterSDK;

	@Resource(name = "weichatUserSDK")
	private UserAccountSDK weichatUserSDK;

	@Resource(name = "xiaomiUserSDK")
	private UserAccountSDK xiaomiUserSDK;

	@Resource(name = "mobileUserAccountSDK")
	private UserAccountSDK mobilAccountSDK;

	@Resource(name = "mobileThunderSDK")
	private UserAccountSDK mobileThunderSDK;

	@Resource(name = "weiboUserSDK")
	private UserAccountSDK weiboUserSDK;

	@Resource(name = "qqUserSDK")
	private UserAccountSDK qqUserSDK;

	@Autowired
	private PassportStorage passportStorage;

	// service层有可能有缓存和业务逻辑
	@Autowired
	private PassportAccountService passportAccountService;

	// dao层都是简单的CRUD，并且没有缓存
	@Autowired
	private PassportOAuthAccountDao passportOAuthAccountDao;

	@Autowired
	private PassportUserAccountDao passportUserAccountDao;

	@Autowired
	private QiniuConfig qiniuConfig;

	@Autowired
	private PassportConfig passportConfig;

	@Resource(name = "OKHttpClientImpl")
	private CommonHttpClient commonHttpClient;

	protected boolean checkMobileCode(String mobile, String code) {
		// TODO
		return true;
	}

	private static final List<Integer> passportCodeNotErrorList;

	static {
		passportCodeNotErrorList = new ArrayList<Integer>();
		passportCodeNotErrorList.add(PassportApiException.EXCEPTION_BIND_ACCOUNT_HAS_EXIST_OR_SAME_TYPE_HAS_EXIST);
		passportCodeNotErrorList.add(PassportApiException.EXCEPTION_MOBILE_CODE_INVALID);
		passportCodeNotErrorList.add(PassportApiException.EXCEPTION_MOBILE_CODE_TYPE_INVALID);
	}

	protected void log(PassportApiException pae, Log logger) {
		logger.warn(pae.getMessage());
	}

	// index0:userHeadIconUrl, index1:oauthHeadIconUrl
	protected String[] storeHeadIcon(String prefix, long passportUserId, String headIconUrl,
			UserAccountTypeEnum accountType) {
		if (StringUtils.isEmpty(headIconUrl)) {
			String url = PlaceholderHeadIconUtil.getPlaceholderHeadIconUrlByMod(passportUserId);
			return new String[] { url, url };
		} else {
			String oauthKey = getPassportStorage().getOAuthHeadIconKey(prefix, passportUserId, accountType);
			String oauthUrl = "";
			try {
				// byte[] image = obtainHeadIconImg(headIconUrl, oauthAppId,
				// openId, accessToken, accountType);
				oauthUrl = getPassportStorage().fetchResouce(headIconUrl, oauthKey);
				if (oauthUrl == null) {
					oauthUrl = PlaceholderHeadIconUtil.getPlaceholderHeadIconUrlByMod(passportUserId);
				}
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}

			return new String[] { headIconUrl, oauthUrl };
		}
	}

	/**
	 * 创建或者更新用户账号信息
	 * 
	 * @param userPortrait
	 * @param xlUserId
	 * @param passportUserId
	 * @param ip
	 * @param accountType
	 */
	// create new record, or update if exists.
	protected void createOrUpdateAccount(String prefix, UserPortrait userPortrait, String openId, Long xlUserId,
			Long passportUserId, String ip, UserAccountTypeEnum accountType) throws PassportApiException {
		long now = System.currentTimeMillis();
		PassportOAuthAccountEntity passportOAuthAccountEntity = newOAuthAccountInstance(userPortrait, openId, xlUserId,
				ip, now, accountType);
		PassportUserAccountEntity passportUserAccountEntity = newPassportUserAccountInstance(userPortrait, xlUserId,
				passportUserId, ip, now, accountType);

		passportAccountService.createOrUpdateAccount(prefix, passportUserAccountEntity, passportOAuthAccountEntity);
	}

	// insert new record into mysql.
	protected BindAccountResultDTO bindAccount(String prefix, UserPortrait userPortrait, String openId, Long xlUserId,
			String ip, UserAccountTypeEnum accountType) throws PassportApiException {

		Long passportUserId = getPassportAccountService().getUserInfoByXlUserId(xlUserId).getPassportUserId();

		// store head icon
		String headIconUrl = userPortrait.getHeadIconURL();
		// 这里必须用passportUserId作为headiconurl一部分，因为现在还不知道xluserId
		String[] urls = storeHeadIcon(prefix, passportUserId, headIconUrl, accountType);
		userPortrait.setHeadIconURL(urls[0]);
		userPortrait.setOauthHeadIconURL(urls[1]);

		long now = System.currentTimeMillis();
		PassportOAuthAccountEntity passportOAuthAccountEntity = newOAuthAccountInstance(userPortrait, openId, xlUserId,
				ip, now, accountType);

		PassportUserAccountEntity passportUserAccountEntity = newPassportUserAccountInstance(userPortrait, xlUserId,
				passportUserId, ip, now, accountType);

		passportAccountService.createOrUpdateAccount(prefix, passportUserAccountEntity, passportOAuthAccountEntity);

		BindAccountResultDTO dto = new BindAccountResultDTO();
		dto.setBindSuccess(true);

		PassportUserAccountEntity userAccount = getPassportAccountService().getUserInfoByXlUserId(xlUserId);

		dto.setHeadIconUrl(userAccount.getHeadIconUrl());
		dto.setGender(SexEnum.valueOf(SexEnum.valueOf(userAccount.getSex())));
		dto.setUserName(passportOAuthAccountEntity.getNickName());

		return dto;
	}

	// protected boolean validate(String appId, String openId, String
	// accessToken,
	// String accountType) {
	// if (accountType
	// .equalsIgnoreCase(UserAccountTypeEnum.weichat.toString())) {
	// return weichatUserSDK.validate(appId, openId, accessToken);
	// } else if (accountType.equalsIgnoreCase(UserAccountTypeEnum.xiaomi
	// .toString())) {
	// return xiaomiUserSDK.validate(appId, openId, accessToken);
	// } else if (accountType.equalsIgnoreCase(UserAccountTypeEnum.mobile
	// .toString())) {
	// return mobilAccountSDK.validate(appId, openId, accessToken);
	// } else if (accountType
	// .equalsIgnoreCase(UserAccountTypeEnum.mobileThunder.name())) {
	// return mobileThunderSDK.validate(appId, openId, accessToken);
	// } else {
	// throw new PassportApiException(
	// PassportApiException.EXCEPTION_INVALID_ACCOUNT_TYPE);
	// }
	// }

	/**
	 * 需要先判断openId,appId是否已经在账号系统中存在，如果存在，返回已有的passportUserId；不存在则生成。
	 * 这里暂时不用缓存，直接查库。
	 * 
	 * @param appId
	 * @param openId
	 * @return
	 */
	protected Long getXlUserId(String openId, UserAccountTypeEnum accountType) throws PassportApiException {
		return passportAccountService.getXlUserId(openId, accountType);
	}

	protected Long newPassportUserId() throws PassportApiException {
		return passportAccountService.nextPassportUserIdSeq();
	}

	/**
	 * 根据不同用户类型(weichat,weibo等)实现不同的用户肖像获取接口，同时根据token验证是否合法
	 * 
	 * @return
	 */
	protected UserPortrait obtainUserPortrait(String appId, String openId, String accessToken,
			UserAccountTypeEnum accountType, String bisType) throws PassportApiException {
		if (accountType.name().equalsIgnoreCase(UserAccountTypeEnum.weichat.toString())) {
			return weichatUserSDK.validateAndObtainUserPortrait(appId, openId, accessToken, bisType);
		} else if (accountType.name().equalsIgnoreCase(UserAccountTypeEnum.xiaomi.toString())) {
			return xiaomiUserSDK.validateAndObtainUserPortrait(appId, openId, accessToken, bisType);
		} else if (accountType.name().equalsIgnoreCase(UserAccountTypeEnum.mobile.toString())) {
			return mobilAccountSDK.validateAndObtainUserPortrait(appId, openId, accessToken, bisType);
		} else if (accountType.name().equalsIgnoreCase(UserAccountTypeEnum.mobileThunder.name())) {
			throw new PassportApiException(PassportApiException.EXCEPTION_NO_SUPPORT_METHOD, null);
			// return mobileThunderSDK.obtainUserPortrait(appId, openId,
			// accessToken);
		} else if (accountType.name().equalsIgnoreCase(UserAccountTypeEnum.mobileThunderSubscription.name())) {
			return mobileThunderSDK.validateAndObtainUserPortrait(appId, openId, accessToken, bisType);
		} else if (accountType.name().equals(UserAccountTypeEnum.weibo.name())) {
			return weiboUserSDK.validateAndObtainUserPortrait(appId, openId, accessToken, bisType);
		} else if (accountType.name().equals(UserAccountTypeEnum.qq.name())) {
			return qqUserSDK.validateAndObtainUserPortrait(appId, openId, accessToken, bisType);
		} else {
			throw new PassportApiException(PassportApiException.EXCEPTION_INVALID_ACCOUNT_TYPE,
					"EXCEPTION_INVALID_ACCOUNT_TYPE", null);
		}
	}

	protected byte[] obtainHeadIconImg(String url, String appId, String openId, String accessToken,
			UserAccountTypeEnum accountType) throws PassportApiException {
		if (accountType.name().equalsIgnoreCase(UserAccountTypeEnum.weichat.toString())) {
			return weichatUserSDK.obtainHeadIconImg(url, appId, openId, accessToken);
		} else if (accountType.name().equalsIgnoreCase(UserAccountTypeEnum.xiaomi.toString())) {
			return xiaomiUserSDK.obtainHeadIconImg(url, appId, openId, accessToken);
		} else if (accountType.name().equalsIgnoreCase(UserAccountTypeEnum.mobile.toString())) {
			return mobilAccountSDK.obtainHeadIconImg(url, appId, openId, accessToken);
		} else if (accountType.name().equals(UserAccountTypeEnum.weibo.name())) {
			return weiboUserSDK.obtainHeadIconImg(url, appId, openId, accessToken);
		} else if (accountType.name().equals(UserAccountTypeEnum.qq.name())) {
			return qqUserSDK.obtainHeadIconImg(url, appId, openId, accessToken);
		}
		return null;
	}

	protected Ticket checkTicket(String t) throws PassportApiException {
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
			throw new PassportApiException(PassportApiException.EXCEPTION_TICKET_INVALID, null);
		} else if (ticketInSSDB.equals(ticketFromClient)) {
			return ticketInSSDB;
		} else {
			throw new PassportApiException(PassportApiException.EXCEPTION_TICKET_INVALID, null);
		}
	}

	protected PassportUserAccountEntity obtainPassportUserAccount(Long xlUserId) throws PassportApiException {
		PassportUserAccountEntity userAccount = getPassportAccountService().getUserInfoByXlUserId(xlUserId);

		if (userAccount == null) {
			return null;
		}

		userAccount.setNickName(NickNameUtil.convertNickName(userAccount.getNickName(), userAccount.getNickNameType()));
		return userAccount;
	}

	/**
	 * 处理相同用户不同设备之间的登陆互踢逻辑
	 * 
	 * @param passportUserId
	 */
	protected void multiDeviceProcess(long xlUserId, String deviceId) {
		// TODO
	}

	private PassportUserAccountEntity newPassportUserAccountInstance(UserPortrait userPortrait, Long xlUserId,
			Long passportUserId, String ip, long time, UserAccountTypeEnum nickNameType) {
		PassportUserAccountEntity userAccount = new PassportUserAccountEntity();
		userAccount.setHeadIconUrl(userPortrait.getHeadIconURL());
		userAccount.setNickName(userPortrait.getNickname());
		userAccount.setPassportUserId(passportUserId);
		userAccount.setRegistIp(IPUtil.Ip2Int(ip));
		userAccount.setRegistTime(time);
		userAccount.setSex(userPortrait.getSex().value());
		userAccount.setUpdateIp(IPUtil.Ip2Int(ip));
		userAccount.setUpdateTime(time);
		userAccount.setUserName(userPortrait.getUsername());
		userAccount.setXlUserId(xlUserId);
		userAccount.setNickNameType(nickNameType.value());
		return userAccount;
	}

	private PassportOAuthAccountEntity newOAuthAccountInstance(UserPortrait userPortrait, String openId, Long xlUserId,
			String ip, long time, UserAccountTypeEnum accountType) {
		PassportOAuthAccountEntity ssoAccount = new PassportOAuthAccountEntity();
		ssoAccount.setBindIp(IPUtil.Ip2Int(ip));
		ssoAccount.setBindTime(time);
		ssoAccount.setHeadIconUrl(userPortrait.getOauthHeadIconURL());
		ssoAccount.setNickName(userPortrait.getNickname());
		ssoAccount.setOpenId(openId);
		ssoAccount.setSex(userPortrait.getSex().value());
		ssoAccount.setType(accountType.value());
		ssoAccount.setUpdateIp(IPUtil.Ip2Int(ip));
		ssoAccount.setUpdateTime(time);
		ssoAccount.setUserName(userPortrait.getUsername());
		ssoAccount.setXlUserId(xlUserId);
		return ssoAccount;
	}

	protected UserAccountSDK getXunleiUserCenterSDK() {
		return xunleiUserCenterSDK;
	}

	protected UserAccountSDK getWeichatUserSDK() {
		return weichatUserSDK;
	}

	protected UserAccountSDK getXiaomiUserSDK() {
		return xiaomiUserSDK;
	}

	protected UserAccountSDK getMobilAccountSDK() {
		return mobilAccountSDK;
	}

	protected PassportAccountService getPassportAccountService() {
		return passportAccountService;
	}

	protected TicketManager getTicketManager() {
		return ticketManager;
	}

	protected SafeChecker getSafeChecker() {
		return safeChecker;
	}

	protected PassportStorage getPassportStorage() {
		return passportStorage;
	}

	protected CommonHttpClient getCommonHttpClient() {
		return commonHttpClient;
	}

	protected QiniuConfig getQiniuConfig() {
		return qiniuConfig;
	}

	protected PassportCache getPassportCache() {
		return passportCache;
	}

	protected PassportOAuthAccountDao getPassportOAuthAccountDao() {
		return passportOAuthAccountDao;
	}

	protected PassportUserAccountDao getPassportUserAccountDao() {
		return passportUserAccountDao;
	}

	protected UserAccountSDK getMobileThunderSDK() {
		return mobileThunderSDK;
	}

	public PassportConfig getPassportConfig() {
		return passportConfig;
	}

}
