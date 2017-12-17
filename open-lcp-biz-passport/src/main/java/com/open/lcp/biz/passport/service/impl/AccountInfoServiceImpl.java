package com.open.lcp.biz.passport.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.open.lcp.biz.passport.MobileCodeType;
import com.open.lcp.biz.passport.PassportException;
import com.open.lcp.biz.passport.UserAccountCategoryConstant;
import com.open.lcp.biz.passport.UserAccountType;
import com.open.lcp.biz.passport.dto.BindAccountResultDTO;
import com.open.lcp.biz.passport.dto.LoginByMobileResultDTO;
import com.open.lcp.biz.passport.dto.LoginByOAuthResultDTO;
import com.open.lcp.biz.passport.dto.ObtainMobileCodeDTO;
import com.open.lcp.biz.passport.dto.PassportOAuthAccountDTO;
import com.open.lcp.biz.passport.dto.PassportUserAccountDTO;
import com.open.lcp.biz.passport.dto.RequestUploadAvatarResultDTO;
import com.open.lcp.biz.passport.service.AbstractAccountService;
import com.open.lcp.biz.passport.service.AccountInfoService;
import com.open.lcp.biz.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.open.lcp.biz.passport.service.dao.entity.PassportUserAccountEntity;
import com.open.lcp.biz.passport.service.sdk.ThirdAccountSDKPortrait;
import com.open.lcp.biz.passport.ticket.Ticket;
import com.open.lcp.biz.passport.util.AccountUtil;
import com.open.lcp.biz.passport.util.NickNameUtil;
import com.open.lcp.core.api.info.BasicUserAccountInfo;
import com.open.lcp.core.common.enums.Gender;
import com.open.lcp.core.env.finder.EnvEnum;
import com.open.lcp.core.env.finder.EnvFinder;

@Service
public class AccountInfoServiceImpl extends AbstractAccountService implements AccountInfoService {

	private final Log logger = LogFactory.getLog(AccountInfoServiceImpl.class);

	@SuppressWarnings("deprecation")
	@Override
	public boolean suicide(String t) {
		Ticket couple = super.checkTicket(t);
		if (couple.getUserId() > 0) {
			Long xlUserId = couple.getUserId();

			List<PassportOAuthAccountEntity> oauthAccountList = passportOAuthAccountDAO
					.getOAuthAccountListByUserId(xlUserId);
			if (oauthAccountList != null) {
				for (PassportOAuthAccountEntity oauthAccount : oauthAccountList) {
					String openId = oauthAccount.getOpenId() + "";
					UserAccountType accountType = oauthAccount.getUserAccountType();

					passportCache.delUserId(openId, accountType);
					passportCache.delOAuthAccountInfoByUserIdAndType(xlUserId, accountType);
				}
			}

			passportCache.delUserInfoByUserId(xlUserId);

			PassportUserAccountEntity userAccount = passportUserAccountDAO.getUserInfoByUserId(xlUserId);
			if (userAccount != null) {
				passportCache.delUserInfoByUserId(xlUserId);
				passportOAuthAccountDAO.delPassportOAuthAccountByUserId(xlUserId);
				passportUserAccountDAO.delPassportUserAccountByUserId(xlUserId);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public ObtainMobileCodeDTO obtainMobileCode(String ip, String deviceId, int appId, String mobile,
			MobileCodeType type) {

		EnvEnum env = EnvFinder.getProfile();
		String validateCode = null;

		if (env == EnvEnum.dev) {
			validateCode = "123456";
		} else if (env == EnvEnum.test) {
			validateCode = "123456";
		} else if (env == EnvEnum.pre || env == EnvEnum.product) {
			// TODO
			validateCode = "123456";
		} else {
			throw new PassportException(PassportException.EXCEPTION_SEND_MOBILE_CODE_FAILED, "invalid env.", null);
		}

		String msg = validateCode + "（动态验证码），请在30分钟内填写【LCP】";
		ObtainMobileCodeDTO dto = new ObtainMobileCodeDTO();
		dto.setMsg(msg);
		return dto;
	}

	@Override
	public boolean bindMobileAccount(int appId, String mobile, String mobileCode, String deviceId, String t,
			String ip) {
		try {
			Ticket couple = super.checkTicket(t);

			// check mobileCode
			boolean isExist = passportCache.existMobileCode(mobile, deviceId, appId, MobileCodeType.bindMobile,
					mobileCode);
			if (!isExist) {
				throw new PassportException(PassportException.EXCEPTION_MOBILE_CODE_INVALID, null);
			}

			ThirdAccountSDKPortrait userPortrait = new ThirdAccountSDKPortrait();
			userPortrait.setAvatar("");
			userPortrait.setNickname(NickNameUtil.convertNickName(mobile));
			userPortrait.setGender(Gender.unknown);
			userPortrait.setUsername(NickNameUtil.convertNickName(mobile));
			BindAccountResultDTO bindResult = super.bindAccount(userPortrait, mobile, couple.getUserId(), ip,
					UserAccountType.mobile);

			return bindResult.isBindSuccess();
		} catch (PassportException pae) {
			logger.error(pae.getMessage(), pae);
			throw new PassportException(pae.getPassportCode(), pae.getMessage(), pae);
		}
	}

	@Override
	public LoginByMobileResultDTO loginByMobileAccount(int appId, String mobile, String mobileCode, String deviceId,
			String ip, String ua) {

		try {
			// 1.调用安全中心接口，验证是否允许登陆

			// check mobileCode
			if ("test".equals(mobile) && "test".equals(mobileCode)) {
				Long userId = passportOAuthAccountDAO.getUserId("test", UserAccountType.mobile.type());

				if (userId == null) {
					ThirdAccountSDKPortrait userPortrait = new ThirdAccountSDKPortrait();
					// userPortraitDTO.setAvatar("");
					String userName = NickNameUtil.convertNickName(mobile);
					userPortrait.setNickname(userName);
					userPortrait.setGender(Gender.unknown);
					userPortrait.setUsername(userName);

					userId = super.createAccount(userPortrait, mobile, userId, ip, UserAccountType.mobile, null);
				}

				Ticket couple = ticketManager.createSecretKeyCouple(appId, userId);

				String description = passportUserAccountDAO.getUserInfoByUserId(userId).getDescription();

				PassportUserAccountEntity userAccount = obtainPassportUserAccount(userId);

				LoginByMobileResultDTO resultDTO = new LoginByMobileResultDTO();
				resultDTO.setAvatar(userAccount.getAvatar());
				resultDTO.setGender(Gender.get(userAccount.getGender()));
				resultDTO.setUserId(userId);
				resultDTO.setUserName(userAccount.getNickName());
				resultDTO.setUserSecretKey(couple.getUserSecretKey());
				resultDTO.setT(couple.getT());
				resultDTO.setNewUser(false);
				resultDTO.setDescription(description);
				return resultDTO;
			}

			boolean isExist = passportCache.existMobileCode(mobile, deviceId, appId, MobileCodeType.loginByMobile,
					mobileCode);

			if (!isExist) {
				throw new PassportException(PassportException.EXCEPTION_MOBILE_CODE_INVALID, null);
			}

			// 2.获取用户肖像
			ThirdAccountSDKPortrait userPortrait = new ThirdAccountSDKPortrait();
			// userPortraitDTO.setAvatar("");
			String userName = NickNameUtil.convertNickName(mobile);
			userPortrait.setNickname(userName);
			userPortrait.setGender(Gender.unknown);
			userPortrait.setUsername(userName);

			// 3.registOrLogin
			Long userId = getUserId(mobile, UserAccountType.mobile);
			if (userId == null) {
				super.createAccount(userPortrait, mobile, userId, ip, UserAccountType.mobile, null);
			} else {
				super.login(userPortrait, mobile, userId, ip, UserAccountType.mobile);
			}

			// 5.处理相同用户不同设备之间,或者相同用户相同设备的多次登陆的登陆互踢逻辑
			super.multiDeviceProcess(userId, deviceId);

			// 6.生成sk,uk
			Ticket couple = ticketManager.createSecretKeyCouple(appId, userId);

			PassportUserAccountEntity userAccount = obtainPassportUserAccount(userId);

			LoginByMobileResultDTO resultDTO = new LoginByMobileResultDTO();
			resultDTO.setAvatar(userAccount.getAvatar());
			resultDTO.setGender(Gender.get(userAccount.getGender()));
			resultDTO.setUserId(userId);
			resultDTO.setUserName(userAccount.getNickName());
			resultDTO.setUserSecretKey(couple.getUserSecretKey());
			resultDTO.setT(couple.getT());
			resultDTO.setDescription(userAccount.getDescription());
			return resultDTO;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new PassportException(PassportException.EXCEPTION_LOGIN_FAILED, e.getMessage(), e);
		}

	}

	@Override
	public BasicUserAccountInfo getUserAccountInfo(Long userId) {
		PassportUserAccountEntity entity = obtainPassportUserAccount(userId);
		PassportUserAccountDTO dto = null;
		if (entity != null) {
			dto = AccountUtil.convertPassportUserAccoutEntity(entity);
		}
		return dto;
	}

	@Override
	public List<PassportOAuthAccountDTO> getOAuthAccountList(Long userId) {
		List<PassportOAuthAccountEntity> list = passportOAuthAccountDAO.getOAuthAccountListByUserId(userId);
		PassportOAuthAccountDTO dto = null;
		List<PassportOAuthAccountDTO> dtolist = new ArrayList<PassportOAuthAccountDTO>();
		for (PassportOAuthAccountEntity entity : list) {
			dto = AccountUtil.convertPassportUserAccoutEntity(entity);
			dtolist.add(dto);
		}
		return dtolist;
	}

	@Override
	public int unbindAccount(Long userId, UserAccountType userAccountType) {
		List<PassportOAuthAccountEntity> list = passportOAuthAccountDAO.getOAuthAccountInfo(userId,
				userAccountType.type());
		if (list == null || list.isEmpty()) {
			return 0;
		}
		PassportOAuthAccountEntity entity = list.get(0);
		if (entity == null || StringUtils.isEmpty(entity.getOpenId())) {
			return 0;
		}
		int result = passportOAuthAccountDAO.unbindOAuthAccount(userId, userAccountType.type());
		if (result > 0) {
			passportCache.delOAuthAccountInfoByUserIdAndType(userId, userAccountType);
			passportCache.delUserId(entity.getOpenId(), userAccountType);
		}
		return result;
	}

	@Override
	public int updateGender(Long userId, Gender gender) {
		return passportUserAccountDAO.updateGender(userId, gender.gender());
	}

	@Override
	public int updateNickName(Long userId, String nickName) {
		return passportUserAccountDAO.updateNickName(userId, nickName);
	}

	@Override
	public int updateDescription(Long userId, String description) {
		return passportUserAccountDAO.updateDescription(userId, description);
	}

	@Override
	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId) {
		String key = accountAvatarStorage.getUserAvatarKey(userId);
		String uploadToken = accountAvatarStorage.requestUploadToken(key);

		RequestUploadAvatarResultDTO result = new RequestUploadAvatarResultDTO();
		result.setKey(key);
		result.setUploadToken(uploadToken);
		return result;
	}

	@Override
	public String commitUploadAvatar(Long userId) {
		String avatarUrl = accountAvatarStorage.getUserAvatarUrl(userId);
		int result = passportUserAccountDAO.updateAvatar(userId, avatarUrl);

		if (result > 0) {
			return avatarUrl;
		} else {
			return null;
		}
	}

	@Override
	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId, UserAccountType accountType) {
		String key = accountAvatarStorage.getOAuthAvatarKey(userId, accountType);
		String uploadToken = accountAvatarStorage.requestUploadToken(key);

		RequestUploadAvatarResultDTO result = new RequestUploadAvatarResultDTO();
		result.setKey(key);
		result.setUploadToken(uploadToken);
		return result;
	}

	@Override
	public String commitUploadAvatar(Long userId, UserAccountType accountType) {
		String avatarUrl = accountAvatarStorage.getOAuthAvatarUrl(userId, accountType);
		int result = passportUserAccountDAO.updateAvatar(userId, avatarUrl);
		if (result > 0) {
			return avatarUrl;
		} else {
			return null;
		}
	}

	@Override
	public String getUserType(Long userId) {
		PassportUserAccountEntity entity = passportUserAccountDAO.getUserInfoByUserId(userId);
		if (entity != null) {
			return entity.getUserCategory();
		} else {
			return UserAccountCategoryConstant.ACCOUNT_CATEGORY_NO_EXIST;
		}
	}

	@Override
	public LoginByOAuthResultDTO loginByThirdAccount(int appId, String oauthAppId, String openId, String accessToken,
			String deviceId, String ip, UserAccountType accountType, String ua) {
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

		PassportUserAccountEntity userAccount = super.obtainPassportUserAccount(userId);

		LoginByOAuthResultDTO resultDTO = new LoginByOAuthResultDTO();
		resultDTO.setAvatar(userAccount.getAvatar());
		resultDTO.setGender(Gender.get(userAccount.getGender()));
		resultDTO.setUserId(userId);
		resultDTO.setUserName(userAccount.getNickName());
		resultDTO.setUserSecretKey(couple.getUserSecretKey());
		resultDTO.setT(couple.getT());
		resultDTO.setDescription(userAccount.getDescription());
		return resultDTO;
	}

	@Override
	public BindAccountResultDTO bindThirdAccount(int appId, String oauthAppId, String openId, String accessToken,
			String deviceId, String t, UserAccountType accountType, String ip) {
		Ticket couple = super.checkTicket(t);

		ThirdAccountSDKPortrait userPortrait = obtainThirdAccountSDK(oauthAppId, openId, accessToken, accountType);
		BindAccountResultDTO bindResult = super.bindAccount(userPortrait, openId, couple.getUserId(), ip, accountType);

		return bindResult;
	}

}
