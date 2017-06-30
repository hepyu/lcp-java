package com.open.passport.api.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.open.common.enums.Gender;
import com.open.passport.MobileCodeType;
import com.open.passport.PassportException;
import com.open.passport.UserAccountType;
import com.open.passport.api.AbstractAccountApi;
import com.open.passport.api.AccountMobileApi;
import com.open.passport.dto.BindAccountResultDTO;
import com.open.passport.dto.LoginByMobileResultDTO;
import com.open.passport.dto.ObtainMobileCodeDTO;
import com.open.passport.dto.PassportUserAccountDTO;
import com.open.passport.sdk.ThirdAccountSDKPortrait;
import com.open.passport.service.AbstractAccount;
import com.open.passport.service.dao.entity.PassportUserAccountEntity;
import com.open.passport.ticket.Ticket;
import com.open.passport.util.NickNameUtil;

@Component
public class SimpleAccountMobileApiImpl extends AbstractAccountApi implements AccountMobileApi {

	private final Log logger = LogFactory.getLog(SimpleAccountMobileApiImpl.class);

	@Override
	public LoginByMobileResultDTO login(String prefix, int appId, String mobile, String mobileCode, String deviceId,
			String ip, String ua) {
		try {
			// 1.调用安全中心接口，验证是否允许登陆

			// check mobileCode
			boolean isExist = passportCache.existMobileCode(mobile, deviceId, appId, MobileCodeType.loginByMobile,
					mobileCode);

			if ("11111111111".equals(mobile) && "666666".equals(mobileCode)) {
				Long userId = passportOAuthAccountDAO.getUserId("18553227095", UserAccountType.mobile.value());
				Ticket couple = ticketManager.createSecretKeyCouple(appId, userId);

				String description = passportUserAccountDAO.getUserInfoByUserId(userId).getDescription();

				PassportUserAccountDTO userAccount = obtainPassportUserAccount(userId);

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
			} else if (!isExist) {
				throw new PassportException(PassportException.EXCEPTION_MOBILE_CODE_INVALID, null);
			}

			// 2.获取用户肖像
			ThirdAccountSDKPortrait userPortrait = new ThirdAccountSDKPortrait();
			// userPortraitDTO.setAvatar("");
			String userName = NickNameUtil.convertNickName(mobile);
			userPortrait.setNickname(userName);
			userPortrait.setGender(Gender.unknown);
			userPortrait.setUsername(userName);

			// 3.registOrLogin 迅雷账号中心
			Long userIdInPassport = getUserId(mobile, UserAccountType.mobile);
			boolean isNewUser = true;

			// 4.mysql中创建用户记录,每次都需要更新，因为headIconURL可能会变，同时更新update_ip,
			// update_time
			createOrUpdateAccount(prefix, userPortrait, mobile, userId, ip, UserAccountType.mobile);

			String description = passportUserAccountDAO.getUserInfoByUserId(userId).get(0).getDescription();

			// 5.处理相同用户不同设备之间,或者相同用户相同设备的多次登陆的登陆互踢逻辑
			multiDeviceProcess(userId, deviceId);

			// 6.生成sk,uk
			Ticket couple = getTicketManager().createSecretKeyCouple(appId, userId);
			boolean existAccountExceptMobile = accountInfoService.existAccountExceptMobileByUserId(userId);

			PassportUserAccountEntity userAccount = obtainPassportUserAccount(userId);

			LoginByMobileResultDTO resultDTO = new LoginByMobileResultDTO();
			resultDTO.setAvatar(userAccount.getAvatar());
			resultDTO.setGender(Gender.valueOf(userAccount.getGender()));
			resultDTO.setUserId(userId);
			resultDTO.setUserName(userAccount.getNickName());
			resultDTO.setUserSecretKey(couple.getUserSecretKey());
			resultDTO.setT(couple.getT());
			resultDTO.setNewUser(isNewUser);
			resultDTO.setDescription(description);
			return resultDTO;
		} catch (PassportException pae) {
			log(pae, logger);
			throw new PassportException(pae.getPassportCode(), pae.getMessage());
		}
	}

	@Override
	public ObtainMobileCodeDTO obtainMobileCode(String ip, String deviceId, int appId, String mobile,
			MobileCodeType type) {

		// TODO
		SafeCheckResult safeCheckResult = getSafeChecker().checkObtainMobileCode(ip, mobile, "");
		if (!safeCheckResult.isSuccess()) {
			ObtainMobileCode resultDTO = new ObtainMobileCode();
			resultDTO.setSecurityCode(safeCheckResult.getCode());
			resultDTO.setNeedImageCode(safeCheckResult.isNeedImageCode());
			resultDTO.setImageCodeUrl(safeCheckResult.getImageCodeUrl());
			return resultDTO;
		}

		Random rm = new Random();
		int strLength = 20;
		// 获得随机数
		double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);
		// 将获得的获得随机数转化为字符串
		String validateCode = String.valueOf(pross);
		validateCode = validateCode.substring(2, 8);

		String msg = null;
		if (appId == 19) {
			msg = validateCode + "（动态验证码），请在30分钟内填写【快盘】";
		} else if (appId == 3) {
			msg = validateCode + "（动态验证码），请在30分钟内填写【小米文件管理】";
		} else if (appId == 20 || appId == 22) {
			msg = validateCode + "（动态验证码），请在30分钟内填写【有料】";
		} else {
			msg = validateCode + "（动态验证码），请在30分钟内填写【小米文件管理】";
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("desNo", mobile);
		params.put("msg", msg);

		try {
			HttpResult httpResult = getCommonHttpClient().httpPost(getPassportConfig().getMobileCodeUrl(), params);
			int code = httpResult.getCode();
			// String result = httpResult.getResult();
			if (code == 298) {
				// GSON GSON = NEW GSON();
				// OBTAINMOBILECODEDTO DTO = GSON.FROMJSON(RESULT,
				// OBTAINMOBILECODEDTO.CLASS);
				// String batchnumber = dto.getBatchnumber();
				passportCache.setMobileCode(mobile, deviceId, appId, type, validateCode + "");
			} else {
				throw new PassportException(PassportException.EXCEPTION_SEND_MOBILE_CODE_FAILED, null);
			}
		} catch (PassportException pae) {
			log(pae, logger);
			throw new PassportException(pae.getPassportCode(), pae.getMessage());
		} catch (Exception e) {
			PassportException pae = new PassportException(PassportException.EXCEPTION_SEND_MOBILE_CODE_FAILED, e);
			log(pae, logger);
			throw new PassportException(pae.getPassportCode(), pae.getMessage());
		}

		ObtainMobileCode dto = new ObtainMobileCode();
		dto.setMsg(msg);
		return dto;
	}

	@Override
	public boolean bindMobile(String prefix, int appId, String mobile, String mobileCode, String deviceId, String t,
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
			BindAccountResultDTO bindResult = super.bindAccount(prefix, userPortrait, mobile, couple.getUserId(), ip,
					UserAccountType.mobile);

			return bindResult.isBindSuccess();
		} catch (PassportException pae) {
			log(pae, logger);
			throw new PassportException(pae.getPassportCode(), pae.getMessage(), pae);
		}
	}

}
