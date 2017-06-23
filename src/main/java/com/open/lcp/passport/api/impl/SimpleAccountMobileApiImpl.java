package com.open.lcp.passport.api.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.open.common.enums.Gender;
import com.open.lcp.passport.MobileCodeType;
import com.open.lcp.passport.PassportException;
import com.open.lcp.passport.UserAccountType;
import com.open.lcp.passport.api.AccountMobileApi;
import com.open.lcp.passport.dto.BindAccountResultDTO;
import com.open.lcp.passport.dto.LoginByMobileResultDTO;
import com.open.lcp.passport.dto.ObtainMobileCodeDTO;
import com.open.lcp.passport.sdk.ThirdAccountSDKPortrait;
import com.open.lcp.passport.service.dao.entity.PassportUserAccountEntity;
import com.open.lcp.passport.ticket.Ticket;
import com.open.lcp.passport.util.NickNameUtil;
import com.open.lcp.passport.util.PlaceholderAvatarUtil;

@Component
public class SimpleAccountMobileApiImpl extends AbstractAccount implements AccountMobileApi {

	private final Log logger = LogFactory.getLog(SimpleAccountMobileApiImpl.class);

	@Override
	public LoginByMobileResultDTO login(String prefix, int appId, String mobile, String mobileCode, String deviceId,
			String ip, String ua) {
		try {
			// 1.调用安全中心接口，验证是否允许登陆
			// SafeCheckResult safeCheckResult = getSafeChecker().checkLogin(ip,
			// mobile, ua);
			// if (!safeCheckResult.isSuccess()) {
			// LoginByMobileResultDTO resultDTO = new LoginByMobileResultDTO();
			// resultDTO.setSecurityCode(safeCheckResult.getCode());
			// resultDTO.setNeedImageCode(safeCheckResult.isNeedImageCode());
			// resultDTO.setImageCodeUrl(safeCheckResult.getImageCodeUrl());
			// return resultDTO;
			// }

			// check mobileCode
			boolean isExist = getPassportCache().existMobileCode(mobile, deviceId, appId, MobileCodeType.loginByMobile,
					mobileCode);

			// if ("18553227095".equals(mobile) && "666666".equals(mobileCode))
			// {
			// Long xlUserId =
			// getPassportOAuthAccountDao().getXlUserId("18553227095",
			// UserAccountType.mobile.value());
			// Ticket couple = getTicketManager().createSecretKeyCouple(appId,
			// xlUserId);
			//
			// boolean existAccountExceptMobile = getPassportAccountService()
			// .existAccountExceptMobileByXlUserId(xlUserId);
			//
			// String description =
			// getPassportUserAccountDao().getUserInfoByXlUserId(xlUserId).get(0)
			// .getDescription();
			//
			// PassportUserAccountEntity userAccount =
			// obtainPassportUserAccount(xlUserId);
			//
			// LoginByMobileResultDTO resultDTO = new LoginByMobileResultDTO();
			// resultDTO.setExistAccountExceptMobile(existAccountExceptMobile);
			// resultDTO.setHeadIconUrl(userAccount.getHeadIconUrl());
			// resultDTO.setGender(SexEnum.valueOf(userAccount.getSexEnum()));
			// resultDTO.setUserId(xlUserId);
			// resultDTO.setUserName(userAccount.getNickName());
			// resultDTO.setUserSecretKey(couple.getUserSecretKey());
			// resultDTO.setT(couple.getT());
			// resultDTO.setNewUser(false);
			// resultDTO.setDescription(description);
			// return resultDTO;
			// } else if (!isExist) {
			// throw new
			// PassportException(PassportException.EXCEPTION_MOBILE_CODE_INVALID,
			// null);
			// }

			// 2.获取用户肖像
			ThirdAccountSDKPortrait userPortrait = new ThirdAccountSDKPortrait();
			// userPortraitDTO.setHeadIconURL("");
			String userName = NickNameUtil.convertMobileToNickName(mobile);
			userPortrait.setNickname(userName);
			userPortrait.setGender(Gender.unknown);
			userPortrait.setUsername(userName);

			// 3.registOrLogin 迅雷账号中心
			Long xlUserIdInPassport = getXlUserId(mobile, UserAccountType.mobile);
			boolean isNewUser = true;
			Long passportUserId = null;
			boolean forbidRegist = true;
			if (xlUserIdInPassport == null) {
				passportUserId = newPassportUserId();

				String[] urls = storeHeadIcon(prefix, passportUserId,
						PlaceholderAvatarUtil.getPlaceholderHeadIconUrl(), UserAccountType.mobile);
				userPortrait.setHeadIconURL(urls[0]);
				userPortrait.setOauthHeadIconURL(urls[1]);
			} else {
				forbidRegist = false;
				PassportUserAccountEntity tempAccount = getPassportAccountService()
						.getUserInfoByXlUserId(xlUserIdInPassport);
				isNewUser = tempAccount == null ? true : false;
				String headIconUrlInDB = tempAccount.getAvatar();
				if (StringUtils.isEmpty(headIconUrlInDB)) {
					userPortrait.setHeadIconURL(PlaceholderAvatarUtil.getPlaceholderHeadIconUrl());
					userPortrait.setOauthHeadIconURL(userPortrait.getHeadIconURL());
				} else {
					userPortrait.setHeadIconURL(headIconUrlInDB);
					userPortrait.setOauthHeadIconURL(userPortrait.getHeadIconURL());
				}
			}

			// 4.mysql中创建用户记录,每次都需要更新，因为headIconURL可能会变，同时更新update_ip,
			// update_time
			createOrUpdateAccount(prefix, userPortrait, mobile, userId, passportUserId, ip, UserAccountType.mobile);

			String description = getPassportUserAccountDao().getUserInfoByXlUserId(xlUserId).get(0).getDescription();

			// 5.处理相同用户不同设备之间,或者相同用户相同设备的多次登陆的登陆互踢逻辑
			multiDeviceProcess(xlUserId, deviceId);

			// 6.生成sk,uk
			Ticket couple = getTicketManager().createSecretKeyCouple(appId, xlUserId);
			boolean existAccountExceptMobile = getPassportAccountService().existAccountExceptMobileByXlUserId(xlUserId);

			PassportUserAccountEntity userAccount = obtainPassportUserAccount(xlUserId);

			LoginByMobileResultDTO resultDTO = new LoginByMobileResultDTO();
			resultDTO.setExistAccountExceptMobile(existAccountExceptMobile);
			resultDTO.setHeadIconUrl(userAccount.getHeadIconUrl());
			resultDTO.setGender(SexEnum.valueOf(userAccount.getSexEnum()));
			resultDTO.setUserId(xlUserId);
			resultDTO.setUserName(userAccount.getNickName());
			resultDTO.setUserSecretKey(couple.getUserSecretKey());
			resultDTO.setT(couple.getT());
			resultDTO.setNewUser(isNewUser);
			resultDTO.setDescription(description);
			return resultDTO;
		} catch (PassportException pae) {
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
		}
	}

	@Override
	public ObtainMobileCodeDTO obtainMobileCode(String ip, String deviceId, int appId, String mobile,
			MobileCodeType type) {

		// TODO
		SafeCheckResult safeCheckResult = getSafeChecker().checkObtainMobileCode(ip, mobile, "");
		if (!safeCheckResult.isSuccess()) {
			ObtainMobileCodeDTO resultDTO = new ObtainMobileCodeDTO();
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

		String msg = validateCode + "（动态验证码），请在30分钟内填写";

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
				getPassportCache().setMobileCode(mobile, deviceId, appId, type, validateCode + "");
			} else {
				throw new PassportException(PassportException.EXCEPTION_SEND_MOBILE_CODE_FAILED, null);
			}
		} catch (PassportException pae) {
			log(pae, logger);
			throw new PassportException(pae.getPassportCode(), pae.getMessage(), pae);
		} catch (Exception e) {
			PassportException pae = new PassportException(PassportException.EXCEPTION_SEND_MOBILE_CODE_FAILED, e);
			log(pae, logger);
			throw new PassportException(pae.getPassportCode(), pae.getMessage(), pae);
		}

		ObtainMobileCodeDTO dto = new ObtainMobileCodeDTO();
		dto.setMsg(msg);
		return dto;
	}

	@Override
	public boolean bindMobile(String prefix, int appId, String mobile, String mobileCode, String deviceId, String t,
			String ip) {
		try {
			Ticket couple = super.checkTicket(t);

			// check mobileCode
			boolean isExist = getPassportCache().existMobileCode(mobile, deviceId, appId, MobileCodeType.bindMobile,
					mobileCode);
			if (!isExist) {
				throw new PassportException(PassportException.EXCEPTION_MOBILE_CODE_INVALID, null);
			}

			ThirdAccountSDKPortrait userPortrait = new ThirdAccountSDKPortrait();
			userPortrait.setHeadIconURL("");
			userPortrait.setNickname(NickNameUtil.convertMobileToNickName(mobile));
			userPortrait.setGender(Gender.unknown);
			userPortrait.setUsername(NickNameUtil.convertMobileToNickName(mobile));
			BindAccountResultDTO bindResult = super.bindAccount(prefix, userPortrait, mobile, couple.getUserId(), ip,
					UserAccountType.mobile);

			return bindResult.isBindSuccess();
		} catch (PassportException pae) {
			log(pae, logger);
			throw new PassportException(pae.getPassportCode(), pae.getMessage(), pae);
		}
	}

}
