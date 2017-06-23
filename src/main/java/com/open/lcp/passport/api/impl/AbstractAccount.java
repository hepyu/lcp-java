package com.open.lcp.passport.api.impl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.open.common.enums.Gender;
import com.open.common.util.IPUtil;
import com.open.lcp.passport.PassportConfig;
import com.open.lcp.passport.PassportException;
import com.open.lcp.passport.UserAccountType;
import com.open.lcp.passport.cache.PassportCache;
import com.open.lcp.passport.dto.BindAccountResultDTO;
import com.open.lcp.passport.sdk.ThirdAccountSDK;
import com.open.lcp.passport.sdk.ThirdAccountSDKPortrait;
import com.open.lcp.passport.service.AccountInfoService;
import com.open.lcp.passport.service.AccountTicketService;
import com.open.lcp.passport.service.dao.PassportOAuthAccountDao;
import com.open.lcp.passport.service.dao.PassportUserAccountDao;
import com.open.lcp.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.open.lcp.passport.service.dao.entity.PassportUserAccountEntity;
import com.open.lcp.passport.storage.AccountAvatarStorage;
import com.open.lcp.passport.storage.impl.QiniuConfig;
import com.open.lcp.passport.ticket.Ticket;
import com.open.lcp.passport.ticket.TicketManager;
import com.open.lcp.passport.util.NickNameUtil;
import com.open.lcp.passport.util.PlaceholderAvatarUtil;

public abstract class AbstractAccount {

	private final Log logger = LogFactory.getLog(AbstractAccount.class);

	@Autowired
	private PassportCache passportCache;

	@Autowired
	private TicketManager ticketManager;

	@Resource(name = "weichatThirdAccountSDK")
	private ThirdAccountSDK weichatThirdAccountSDK;

	@Resource(name = "xiaomiThirdAccountSDK")
	private ThirdAccountSDK xiaomiThirdAccountSDK;

	@Resource(name = "mobileThirdAccountSDK")
	private ThirdAccountSDK mobileThirdAccountSDK;

	@Resource(name = "weiboThirdAccountSDK")
	private ThirdAccountSDK weiboThirdAccountSDK;

	@Resource(name = "qqThirdAccountSDK")
	private ThirdAccountSDK qqThirdAccountSDK;

	@Autowired
	private AccountAvatarStorage accountAvatarStorage;

	// service层有可能有缓存和业务逻辑
	@Autowired
	private AccountTicketService accountTicketService;

	@Autowired
	private AccountInfoService accountInfoService;

	// dao层都是简单的CRUD，并且没有缓存
	@Autowired
	private PassportOAuthAccountDao passportOAuthAccountDao;

	@Autowired
	private PassportUserAccountDao passportUserAccountDao;

	@Autowired
	private QiniuConfig qiniuConfig;

	@Autowired
	private PassportConfig passportConfig;

	@Autowired
	private CloseableHttpClient closeableHttpClient;

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

	protected void log(PassportException pae, Log logger) {
		logger.warn(pae.getMessage());
	}

	// index0:userAvatarUrl, index1:oauthAvatarUrl
	protected String[] storeAvatar(String prefix, long passportUserId, String headIconUrl,
			UserAccountType accountType) {
		if (StringUtils.isEmpty(headIconUrl)) {
			String url = PlaceholderAvatarUtil.getPlaceholderAvatarByMod(passportUserId);
			return new String[] { url, url };
		} else {
			String oauthKey = getAccountAvatarStorage().getOAuthAvatarKey(prefix, passportUserId, accountType);
			String oauthUrl = "";
			try {
				// byte[] image = obtainAvatarImg(headIconUrl, oauthAppId,
				// openId, accessToken, accountType);
				oauthUrl = getAccountAvatarStorage().fetchResouce(headIconUrl, oauthKey);
				if (oauthUrl == null) {
					oauthUrl = PlaceholderAvatarUtil.getPlaceholderAvatarByMod(passportUserId);
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
	 * @param userId
	 * @param passportUserId
	 * @param ip
	 * @param accountType
	 */
	// create new record, or update if exists.
	protected void createOrUpdateAccount(String prefix, ThirdAccountSDKPortrait userPortrait, String openId,
			Long userId, String ip, UserAccountType accountType) throws PassportException {
		long now = System.currentTimeMillis();
		PassportOAuthAccountEntity passportOAuthAccountEntity = newOAuthAccountInstance(userPortrait, openId, userId,
				ip, now, accountType);
		PassportUserAccountEntity passportUserAccountEntity = newPassportUserAccountInstance(userPortrait, userId, ip,
				now);

		accountInfoService.createOrUpdateAccount(prefix, passportUserAccountEntity, passportOAuthAccountEntity);
	}

	// insert new record into mysql.
	protected BindAccountResultDTO bindAccount(String prefix, ThirdAccountSDKPortrait userPortrait, String openId,
			Long userId, String ip, UserAccountType accountType) throws PassportException {

		// store head icon
		String headIconUrl = userPortrait.getAvatar();
		// 这里必须用passportUserId作为headiconurl一部分，因为现在还不知道xluserId
		String[] urls = storeAvatar(prefix, headIconUrl, accountType);
		userPortrait.setAvatar(urls[0]);
		userPortrait.setOauthAvatar(urls[1]);

		long now = System.currentTimeMillis();
		PassportOAuthAccountEntity passportOAuthAccountEntity = newOAuthAccountInstance(userPortrait, openId, userId,
				ip, now, accountType);

		PassportUserAccountEntity passportUserAccountEntity = newPassportUserAccountInstance(userPortrait, userId,
				passportUserId, ip, now, accountType);

		passportAccountService.createOrUpdateAccount(prefix, passportUserAccountEntity, passportOAuthAccountEntity);

		BindAccountResultDTO dto = new BindAccountResultDTO();
		dto.setBindSuccess(true);

		PassportUserAccountEntity userAccount = getPassportAccountService().getUserInfoByXlUserId(userId);

		dto.setAvatar(userAccount.getAvatar());
		dto.setGender(Gender.get(userAccount.getGender()));
		dto.setUserName(passportOAuthAccountEntity.getNickName());

		return dto;
	}

	// protected boolean validate(String appId, String openId, String
	// accessToken,
	// String accountType) {
	// if (accountType
	// .equalsIgnoreCase(UserAccountType.weichat.toString())) {
	// return weichatUserSDK.validate(appId, openId, accessToken);
	// } else if (accountType.equalsIgnoreCase(UserAccountType.xiaomi
	// .toString())) {
	// return xiaomiUserSDK.validate(appId, openId, accessToken);
	// } else if (accountType.equalsIgnoreCase(UserAccountType.mobile
	// .toString())) {
	// return mobilAccountSDK.validate(appId, openId, accessToken);
	// } else if (accountType
	// .equalsIgnoreCase(UserAccountType.mobileThunder.name())) {
	// return mobileThunderSDK.validate(appId, openId, accessToken);
	// } else {
	// throw new PassportException(
	// PassportException.EXCEPTION_INVALID_ACCOUNT_TYPE);
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
	protected Long getXlUserId(String openId, UserAccountType accountType) throws PassportException {
		return accountInfoService.getUserId(openId, accountType);
	}

	/**
	 * 根据不同用户类型(weichat,weibo等)实现不同的用户肖像获取接口，同时根据token验证是否合法
	 * 
	 * @return
	 */
	protected ThirdAccountSDKPortrait obtainThirdAccountSDKPortrait(String appId, String openId, String accessToken,
			UserAccountType accountType, String bisType) throws PassportException {
		if (accountType.name().equalsIgnoreCase(UserAccountType.weichat.toString())) {
			return weichatThirdAccountSDK.validateAndObtainUserPortrait(appId, openId, accessToken, bisType);
		} else if (accountType.name().equalsIgnoreCase(UserAccountType.xiaomi.toString())) {
			return xiaomiThirdAccountSDK.validateAndObtainUserPortrait(appId, openId, accessToken, bisType);
		} else if (accountType.name().equalsIgnoreCase(UserAccountType.mobile.toString())) {
			return mobileThirdAccountSDK.validateAndObtainUserPortrait(appId, openId, accessToken, bisType);
		} else if (accountType.name().equals(UserAccountType.weibo.name())) {
			return weiboThirdAccountSDK.validateAndObtainUserPortrait(appId, openId, accessToken, bisType);
		} else if (accountType.name().equals(UserAccountType.qq.name())) {
			return qqThirdAccountSDK.validateAndObtainUserPortrait(appId, openId, accessToken, bisType);
		} else {
			throw new PassportException(PassportException.EXCEPTION_INVALID_ACCOUNT_TYPE,
					"EXCEPTION_INVALID_ACCOUNT_TYPE", null);
		}
	}

	protected byte[] obtainAvatarImg(String url, String appId, String openId, String accessToken,
			UserAccountType accountType) throws PassportException {
		if (accountType.name().equalsIgnoreCase(UserAccountType.weichat.toString())) {
			return weichatThirdAccountSDK.obtainAvatar(url, appId, openId, accessToken);
		} else if (accountType.name().equalsIgnoreCase(UserAccountType.xiaomi.toString())) {
			return xiaomiThirdAccountSDK.obtainAvatar(url, appId, openId, accessToken);
		} else if (accountType.name().equalsIgnoreCase(UserAccountType.mobile.toString())) {
			return mobileThirdAccountSDK.obtainAvatar(url, appId, openId, accessToken);
		} else if (accountType.name().equals(UserAccountType.weibo.name())) {
			return weiboThirdAccountSDK.obtainAvatar(url, appId, openId, accessToken);
		} else if (accountType.name().equals(UserAccountType.qq.name())) {
			return qqThirdAccountSDK.obtainAvatar(url, appId, openId, accessToken);
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

	protected PassportUserAccountEntity obtainPassportUserAccount(Long userId) throws PassportException {
		PassportUserAccountEntity userAccount = getPassportUserAccountDao().getUserInfoByUserId(userId);

		if (userAccount == null) {
			return null;
		}

		userAccount.setNickName(NickNameUtil.convertNickName(userAccount.getNickName()));
		return userAccount;
	}

	/**
	 * 处理相同用户不同设备之间的登陆互踢逻辑
	 * 
	 * @param passportUserId
	 */
	protected void multiDeviceProcess(long userId, String deviceId) {
		// TODO
	}

	private PassportUserAccountEntity newPassportUserAccountInstance(ThirdAccountSDKPortrait userPortrait, Long userId,
			String ip, long time) {
		PassportUserAccountEntity userAccount = new PassportUserAccountEntity();
		userAccount.setAvatar(userPortrait.getAvatar());
		userAccount.setNickName(userPortrait.getNickname());
		userAccount.setRegistIp(IPUtil.Ip2Int(ip));
		userAccount.setRegistTime(time);
		userAccount.setGender(userPortrait.getGender().gender());
		userAccount.setUpdateIp(IPUtil.Ip2Int(ip));
		userAccount.setUpdateTime(time);
		userAccount.setUserName(userPortrait.getUsername());
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
		ssoAccount.setType(accountType.value());
		ssoAccount.setUpdateIp(IPUtil.Ip2Int(ip));
		ssoAccount.setUpdateTime(time);
		ssoAccount.setUserName(userPortrait.getUsername());
		return ssoAccount;
	}

	protected PassportCache getPassportCache() {
		return passportCache;
	}

	protected TicketManager getTicketManager() {
		return ticketManager;
	}

	protected ThirdAccountSDK getWeichatThirdAccountSDK() {
		return weichatThirdAccountSDK;
	}

	protected ThirdAccountSDK getXiaomiThirdAccountSDK() {
		return xiaomiThirdAccountSDK;
	}

	protected ThirdAccountSDK getMobileThirdAccountSDK() {
		return mobileThirdAccountSDK;
	}

	protected ThirdAccountSDK getWeiboThirdAccountSDK() {
		return weiboThirdAccountSDK;
	}

	protected ThirdAccountSDK getQqThirdAccountSDK() {
		return qqThirdAccountSDK;
	}

	protected AccountAvatarStorage getAccountAvatarStorage() {
		return accountAvatarStorage;
	}

	protected AccountTicketService getAccountTicketService() {
		return accountTicketService;
	}

	protected AccountInfoService getAccountInfoService() {
		return accountInfoService;
	}

	protected PassportOAuthAccountDao getPassportOAuthAccountDao() {
		return passportOAuthAccountDao;
	}

	protected PassportUserAccountDao getPassportUserAccountDao() {
		return passportUserAccountDao;
	}

	protected QiniuConfig getQiniuConfig() {
		return qiniuConfig;
	}

	protected PassportConfig getPassportConfig() {
		return passportConfig;
	}

	protected CloseableHttpClient getCloseableHttpClient() {
		return closeableHttpClient;
	}

}
