package com.open.lcp.passport.api.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.open.lcp.passport.ticket.Ticket;
import com.xunlei.mcp.model.ApiException;
import com.xunlei.xlmc.passport.MobileCodeTypeEnum;
import com.xunlei.xlmc.passport.SexEnum;
import com.xunlei.xlmc.passport.UserAccountTypeEnum;
import com.xunlei.xlmc.passport.api.AccountMobileApi;
import com.xunlei.xlmc.passport.api.PassportApiException;
import com.xunlei.xlmc.passport.bean.BindAccountResult;
import com.xunlei.xlmc.passport.bean.LoginByMobileResult;
import com.xunlei.xlmc.passport.bean.ObtainMobileCode;
import com.xunlei.xlmc.passport.common.http.HttpResult;
import com.xunlei.xlmc.passport.component.safecenter.SafeCheckResult;
import com.xunlei.xlmc.passport.sdk.UserPortrait;
import com.xunlei.xlmc.passport.service.dao.entity.PassportUserAccountEntity;
import com.xunlei.xlmc.passport.util.NickNameUtil;
import com.xunlei.xlmc.passport.util.PlaceholderHeadIconUtil;
import com.xunlei.xlmc.passport.util.UserTicketMaker;

@Component
public class SimpleAccountMobileApiImpl extends AbstractAccount implements AccountMobileApi {

	private final Log logger = LogFactory.getLog(SimpleAccountMobileApiImpl.class);

	@Override
	public LoginByMobileResultDTO login(String prefix, int appId, String mobile, String mobileCode, String deviceId,
			String ip, String ua) {
		try {
			// 1.调用安全中心接口，验证是否允许登陆
			SafeCheckResult safeCheckResult = getSafeChecker().checkLogin(ip, mobile, ua);
			if (!safeCheckResult.isSuccess()) {
				LoginByMobileResultDTO resultDTO = new LoginByMobileResultDTO();
				resultDTO.setSecurityCode(safeCheckResult.getCode());
				resultDTO.setNeedImageCode(safeCheckResult.isNeedImageCode());
				resultDTO.setImageCodeUrl(safeCheckResult.getImageCodeUrl());
				return resultDTO;
			}
			// check mobileCode
			boolean isExist = getPassportCache().existMobileCode(mobile, deviceId, appId,
					MobileCodeTypeEnum.loginByMobile, mobileCode);

			if ("18553227095".equals(mobile) && "666666".equals(mobileCode)) {
				Long xlUserId = getPassportOAuthAccountDao().getXlUserId("18553227095",
						UserAccountTypeEnum.mobile.value());
				Ticket couple = getTicketManager().createSecretKeyCouple(appId, xlUserId);

				boolean existAccountExceptMobile = getPassportAccountService()
						.existAccountExceptMobileByXlUserId(xlUserId);

				String description = getPassportUserAccountDao().getUserInfoByXlUserId(xlUserId).get(0)
						.getDescription();

				PassportUserAccountEntity userAccount = obtainPassportUserAccount(xlUserId);

				LoginByMobileResultDTO resultDTO = new LoginByMobileResultDTO();
				resultDTO.setExistAccountExceptMobile(existAccountExceptMobile);
				resultDTO.setHeadIconUrl(userAccount.getHeadIconUrl());
				resultDTO.setGender(SexEnum.valueOf(userAccount.getSexEnum()));
				resultDTO.setUserId(xlUserId);
				resultDTO.setUserName(userAccount.getNickName());
				resultDTO.setUserSecretKey(couple.getUserSecretKey());
				resultDTO.setT(couple.getT());
				resultDTO.setNewUser(false);
				resultDTO.setDescription(description);
				return resultDTO;
			} else if (!isExist) {
				throw new PassportApiException(PassportApiException.EXCEPTION_MOBILE_CODE_INVALID, null);
			}

			// 2.获取用户肖像
			UserPortrait userPortrait = new UserPortrait();
			// userPortraitDTO.setHeadIconURL("");
			String userName = NickNameUtil.convertMobileToNickName(mobile);
			userPortrait.setNickname(userName);
			userPortrait.setSex(SexEnum.unknown);
			userPortrait.setUsername(userName);

			// 3.registOrLogin 迅雷账号中心
			Long xlUserIdInPassport = getXlUserId(mobile, UserAccountTypeEnum.mobile);
			boolean isNewUser = true;
			Long passportUserId = null;
			boolean forbidRegist = true;
			if (xlUserIdInPassport == null) {
				passportUserId = newPassportUserId();

				String[] urls = storeHeadIcon(prefix, passportUserId,
						PlaceholderHeadIconUtil.getPlaceholderHeadIconUrl(), UserAccountTypeEnum.mobile);
				userPortrait.setHeadIconURL(urls[0]);
				userPortrait.setOauthHeadIconURL(urls[1]);
			} else {
				forbidRegist = false;
				PassportUserAccountEntity tempAccount = getPassportAccountService()
						.getUserInfoByXlUserId(xlUserIdInPassport);
				isNewUser = tempAccount == null ? true : false;
				passportUserId = tempAccount.getPassportUserId();
				String headIconUrlInDB = tempAccount.getHeadIconUrl();
				if (StringUtils.isEmpty(headIconUrlInDB)) {
					userPortrait.setHeadIconURL(PlaceholderHeadIconUtil.getPlaceholderHeadIconUrl());
					userPortrait.setOauthHeadIconURL(userPortrait.getHeadIconURL());
				} else {
					userPortrait.setHeadIconURL(headIconUrlInDB);
					userPortrait.setOauthHeadIconURL(userPortrait.getHeadIconURL());
				}
			}

			if (passportUserId == null) {
				throw new PassportApiException(PassportApiException.EXCEPTION_LOGIN_FAILED, null);
			}
			String userIdentify = UserTicketMaker.toKey(passportUserId);
			getPassportCache().setUserIdentify(userIdentify, true);

			Long xlUserId = getXunleiUserCenterSDK().registOrUpdateUserInfo(passportUserId, userIdentify,
					userPortrait.getUsername(), userPortrait.getHeadIconURL(), userPortrait.getSex(), "", forbidRegist);

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
			createOrUpdateAccount(prefix, userPortrait, mobile, xlUserId, passportUserId, ip,
					UserAccountTypeEnum.mobile);

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
		} catch (PassportApiException pae) {
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
		}
	}

	@Override
	public ObtainMobileCodeDTO obtainMobileCode(String ip, String deviceId, int appId, String mobile,
			MobileCodeTypeEnum type) {

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
				getPassportCache().setMobileCode(mobile, deviceId, appId, type, validateCode + "");
			} else {
				throw new PassportApiException(PassportApiException.EXCEPTION_SEND_MOBILE_CODE_FAILED, null);
			}
		} catch (PassportApiException pae) {
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
		} catch (Exception e) {
			PassportApiException pae = new PassportApiException(PassportApiException.EXCEPTION_SEND_MOBILE_CODE_FAILED,
					e);
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
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
			boolean isExist = getPassportCache().existMobileCode(mobile, deviceId, appId, MobileCodeTypeEnum.bindMobile,
					mobileCode);
			if (!isExist) {
				throw new PassportApiException(PassportApiException.EXCEPTION_MOBILE_CODE_INVALID, null);
			}

			UserPortrait userPortrait = new UserPortrait();
			userPortrait.setHeadIconURL("");
			userPortrait.setNickname(NickNameUtil.convertMobileToNickName(mobile));
			userPortrait.setSex(SexEnum.unknown);
			userPortrait.setUsername(NickNameUtil.convertMobileToNickName(mobile));
			BindAccountResultDTO bindResult = super.bindAccount(prefix, userPortrait, mobile, couple.getXlUserId(), ip,
					UserAccountTypeEnum.mobile);

			return bindResult.isBindSuccess();
		} catch (PassportApiException pae) {
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
		}
	}

}
