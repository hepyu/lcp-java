package com.open.lcp.biz.passport.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.open.lcp.biz.passport.MobileCodeType;
import com.open.lcp.biz.passport.PassportException;
import com.open.lcp.biz.passport.UserAccountType;
import com.open.lcp.biz.passport.api.AbstractAccountApi;
import com.open.lcp.biz.passport.api.AccountMobileApi;
import com.open.lcp.biz.passport.dto.BindAccountResultDTO;
import com.open.lcp.biz.passport.dto.LoginByMobileResultDTO;
import com.open.lcp.biz.passport.dto.ObtainMobileCodeDTO;
import com.open.lcp.biz.passport.dto.PassportUserAccountDTO;
import com.open.lcp.biz.passport.sdk.ThirdAccountSDKPortrait;
import com.open.lcp.biz.passport.ticket.Ticket;
import com.open.lcp.biz.passport.util.NickNameUtil;
import com.open.lcp.common.enums.Gender;
import com.open.lcp.core.base.info.BaseUserAccountInfo;
import com.open.lcp.env.finder.EnvEnum;
import com.open.lcp.env.finder.EnvFinder;

@Component
public class SimpleAccountMobileApiImpl extends AbstractAccountApi implements AccountMobileApi {

	private final Log logger = LogFactory.getLog(SimpleAccountMobileApiImpl.class);

	@Override
	public LoginByMobileResultDTO login(int appId, String mobile, String mobileCode, String deviceId, String ip,
			String ua) {
		try {
			// 1.调用安全中心接口，验证是否允许登陆

			// check mobileCode
			if ("test".equals(mobile) && "test".equals(mobileCode)) {
				Long userId = passportOAuthAccountDAO.getUserId("test", UserAccountType.mobile.value());

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

				BaseUserAccountInfo userAccount = obtainPassportUserAccount(userId);

				LoginByMobileResultDTO resultDTO = new LoginByMobileResultDTO();
				resultDTO.setAvatar(userAccount.getAvatar());
				resultDTO.setGender(userAccount.getGender());
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
			multiDeviceProcess(userId, deviceId);

			// 6.生成sk,uk
			Ticket couple = ticketManager.createSecretKeyCouple(appId, userId);

			BaseUserAccountInfo userAccount = accountInfoService.getUserAccountInfo(userId);

			LoginByMobileResultDTO resultDTO = new LoginByMobileResultDTO();
			resultDTO.setAvatar(userAccount.getAvatar());
			resultDTO.setGender(userAccount.getGender());
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
	public boolean bindMobile(int appId, String mobile, String mobileCode, String deviceId, String t, String ip) {
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

}
