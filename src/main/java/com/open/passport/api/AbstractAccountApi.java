package com.open.passport.api;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.open.common.enums.Gender;
import com.open.common.util.IPUtil;
import com.open.passport.PassportConfig;
import com.open.passport.PassportException;
import com.open.passport.UserAccountType;
import com.open.passport.cache.PassportCache;
import com.open.passport.dto.BindAccountResultDTO;
import com.open.passport.dto.PassportUserAccountDTO;
import com.open.passport.sdk.ThirdAccountSDK;
import com.open.passport.sdk.ThirdAccountSDKPortrait;
import com.open.passport.service.AccountInfoService;
import com.open.passport.service.dao.PassportOAuthAccountDAO;
import com.open.passport.service.dao.PassportUserAccountDAO;
import com.open.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.open.passport.service.dao.entity.PassportUserAccountEntity;
import com.open.passport.storage.AccountAvatarStorage;
import com.open.passport.storage.impl.QiniuConfig;
import com.open.passport.ticket.Ticket;
import com.open.passport.ticket.TicketManager;
import com.open.passport.util.PlaceholderAvatarUtil;

public class AbstractAccountApi {

	protected final Log logger = LogFactory.getLog(AbstractAccountApi.class);

	@Autowired
	protected PassportCache passportCache;

	@Autowired
	protected TicketManager ticketManager;

	@Resource(name = "weichatUserSDK")
	protected ThirdAccountSDK weichatUserSDK;

	@Resource(name = "xiaomiUserSDK")
	protected ThirdAccountSDK xiaomiUserSDK;

	@Resource(name = "mobileThirdAccountSDK")
	protected ThirdAccountSDK mobilAccountSDK;

	@Resource(name = "weiboUserSDK")
	private ThirdAccountSDK weiboUserSDK;

	@Resource(name = "qqUserSDK")
	protected ThirdAccountSDK qqUserSDK;

	@Autowired
	protected AccountAvatarStorage accountAvatarStorage;

	// service层有可能有缓存和业务逻辑
	@Autowired
	protected AccountInfoService accountInfoService;

	// dao层都是简单的CRUD，并且没有缓存
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

	protected void log(PassportException pae, Log logger) {
		logger.warn(pae.getMessage());
	}

	// index0:userHeadIconUrl, index1:oauthHeadIconUrl
	protected String[] storeHeadIcon(String prefix, long userId, String headIconUrl, UserAccountType accountType) {
		if (StringUtils.isEmpty(headIconUrl)) {
			String url = PlaceholderAvatarUtil.getPlaceholderAvatarByMod(userId);
			return new String[] { url, url };
		} else {
			String oauthKey = accountAvatarStorage.getOAuthAvatarKey(prefix, userId, accountType);
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
	protected void createOrUpdateAccount(String prefix, ThirdAccountSDKPortrait userPortrait, String openId,
			Long userId, String ip, UserAccountType accountType) throws PassportException {
		long now = System.currentTimeMillis();
		PassportOAuthAccountEntity passportOAuthAccountEntity = newOAuthAccountInstance(userPortrait, openId, userId,
				ip, now, accountType);
		PassportUserAccountEntity passportUserAccountEntity = newPassportUserAccountInstance(userPortrait, userId, ip,
				now, accountType);

		accountInfoService.createOrUpdateAccount(prefix, passportUserAccountEntity, passportOAuthAccountEntity);
	}

	// insert new record into mysql.
	protected BindAccountResultDTO bindAccount(String prefix, ThirdAccountSDKPortrait userPortrait, String openId,
			Long userId, String ip, UserAccountType accountType) throws PassportException {

		// store head icon
		String headIconUrl = userPortrait.getAvatar();
		// 这里必须用passportUserId作为headiconurl一部分，因为现在还不知道xluserId
		String[] urls = storeHeadIcon(prefix, userId, headIconUrl, accountType);
		userPortrait.setAvatar(urls[0]);
		// userPortrait.setOauthHeadIconURL(urls[1]);

		long now = System.currentTimeMillis();
		PassportOAuthAccountEntity passportOAuthAccountEntity = newOAuthAccountInstance(userPortrait, openId, userId,
				ip, now, accountType);

		PassportUserAccountEntity passportUserAccountEntity = newPassportUserAccountInstance(userPortrait, userId, ip,
				now, accountType);

		accountInfoService.createOrUpdateAccount(prefix, passportUserAccountEntity, passportOAuthAccountEntity);

		BindAccountResultDTO dto = new BindAccountResultDTO();
		dto.setBindSuccess(true);

		PassportUserAccountEntity userAccount = passportUserAccountDAO.getUserInfoByUserId(userId);

		dto.setAvatar(userAccount.getAvatar());
		dto.setGender(Gender.get(userAccount.getGender()));
		dto.setUserName(passportOAuthAccountEntity.getNickName());

		return dto;
	}

	/**
	 * d 需要先判断openId,appId是否已经在账号系统中存在，如果存在，返回已有的passportUserId；不存在则生成。
	 * 这里暂时不用缓存，直接查库。
	 * 
	 * @param appId
	 * @param openId
	 * @return
	 */
	protected Long getUserId(String openId, UserAccountType accountType) throws PassportException {
		return passportOAuthAccountDAO.getUserId(openId, accountType.value());
	}

	/**
	 * 根据不同用户类型(weichat,weibo等)实现不同的用户肖像获取接口，同时根据token验证是否合法
	 * 
	 * @return
	 */
	protected ThirdAccountSDKPortrait obtainThirdAccountSDK(String appId, String openId, String accessToken,
			UserAccountType accountType, String bisType) throws PassportException {
		if (accountType.name().equalsIgnoreCase(UserAccountType.weichat.toString())) {
			return weichatUserSDK.validateAndObtainUserPortrait(appId, openId, accessToken, bisType);
		} else if (accountType.name().equalsIgnoreCase(UserAccountType.xiaomi.toString())) {
			return xiaomiUserSDK.validateAndObtainUserPortrait(appId, openId, accessToken, bisType);
		} else if (accountType.name().equalsIgnoreCase(UserAccountType.mobile.toString())) {
			return mobilAccountSDK.validateAndObtainUserPortrait(appId, openId, accessToken, bisType);
		} else if (accountType.name().equals(UserAccountType.weibo.name())) {
			return weiboUserSDK.validateAndObtainUserPortrait(appId, openId, accessToken, bisType);
		} else if (accountType.name().equals(UserAccountType.qq.name())) {
			return qqUserSDK.validateAndObtainUserPortrait(appId, openId, accessToken, bisType);
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

	protected PassportUserAccountDTO obtainPassportUserAccount(Long userId) throws PassportException {
		PassportUserAccountDTO userAccount = accountInfoService.getUserInfo(userId);

		if (userAccount == null) {
			return null;
		}

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
		ssoAccount.setType(accountType.value());
		ssoAccount.setUpdateIp(IPUtil.Ip2Int(ip));
		ssoAccount.setUpdateTime(time);
		ssoAccount.setUserName(userPortrait.getUsername());
		ssoAccount.setUserId(userId);
		return ssoAccount;
	}

}
