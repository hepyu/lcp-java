package com.open.lcp.biz.passport.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.open.lcp.biz.passport.PassportException;
import com.open.lcp.biz.passport.dto.LoginResultDTO;
import com.open.lcp.biz.passport.dto.ObtainMobileCodeResultDTO;
import com.open.lcp.biz.passport.dto.RequestUploadAvatarResultDTO;
import com.open.lcp.biz.passport.service.AbstractAccountService;
import com.open.lcp.biz.passport.service.PassportService;
import com.open.lcp.biz.passport.service.cache.MobileCodeCache;
import com.open.lcp.biz.passport.service.sdk.ThirdAccountSDKPortrait;
import com.open.lcp.biz.passport.util.NickNameUtil;
import com.open.lcp.core.common.enums.Gender;
import com.open.lcp.core.env.finder.EnvEnum;
import com.open.lcp.core.env.finder.EnvFinder;
import com.open.lcp.core.feature.user.api.MobileCodeType;
import com.open.lcp.core.feature.user.api.UserType;
import com.open.lcp.core.feature.user.api.dto.BindThirdAccountResultDTO;
import com.open.lcp.core.feature.user.api.dto.GetUserIdResultDTO;
import com.open.lcp.core.feature.user.api.dto.NewUserResultDTO;
import com.open.lcp.core.feature.user.api.dto.UserDetailInfoDTO;
import com.open.lcp.core.feature.user.api.dto.UserTicketDTO;
import com.open.lcp.core.feature.user.api.rpc.UserDubboRPC;
import com.open.lcp.core.feature.user.api.rpc.UserHttpRPC;
import com.open.lcp.core.feature.user.api.rpc.UserTicketDubboRPC;
import com.open.lcp.core.framework.api.ApiException;

@Service
public class PassportServiceImpl extends AbstractAccountService implements PassportService {

	@Autowired
	private UserDubboRPC userDubboRPC;

	@Autowired
	private UserHttpRPC userHttpRPC;

	@Autowired
	private UserTicketDubboRPC userTicketDubboRPC;

	@Autowired
	private MobileCodeCache mobileCodeCache;

	@Override
	public LoginResultDTO login(int appId, String oauthAppId, String openId, String accessToken, String deviceId,
			String ip, UserType accountType, String ua, String mobileCode) {
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
		String avatar = userPortrait.getAvatar();
		// 这里必须用passportUserId作为headiconurl一部分，因为现在还不知道userId

		GetUserIdResultDTO getUserIdResultDTO = userDubboRPC.getUserId(openId, accountType);
		Long userId = null;
		if (getUserIdResultDTO != null) {
			userId = getUserIdResultDTO.getUserId();
		}

		if (userId == null) {
			NewUserResultDTO dto = userHttpRPC.newUser(openId, ip, accountType, avatar, userPortrait.getNickname(),
					userPortrait.getUsername(), userPortrait.getGender());
			if (dto != null) {
				userId = dto.getUserId();
			}
		}

		if (userId == null) {
			throw new PassportException(PassportException.EXCEPTION_LOGIN_FAILED, "EXCEPTION_LOGIN_FAILED", null);
		}

		// 5.处理相同用户不同设备之间,或者相同用户相同设备的多次登陆的登陆互踢逻辑
		multiDeviceProcess(userId, deviceId);

		// 6.生成sk,uk
		UserTicketDTO ticketDTO = userTicketDubboRPC.generateKey(appId, userId);

		UserDetailInfoDTO userDTO = userDubboRPC.getUserDetailInfo(userId);

		LoginResultDTO resultDTO = new LoginResultDTO();
		resultDTO.setAvatar(userDTO.getAvatar());
		resultDTO.setGender(userDTO.getGender());
		resultDTO.setUserId(userId);
		resultDTO.setUserName(userDTO.getNickName());
		resultDTO.setUserSecretKey(ticketDTO.getUserSecretKey());
		resultDTO.setT(ticketDTO.getT());
		resultDTO.setDescription(userDTO.getDescription());
		return resultDTO;
	}

	@Override
	public BindThirdAccountResultDTO bindThirdAccount(int appId, String oauthAppId, String openId, String accessToken,
			String deviceId, String t, UserType accountType, String ip, String mobileCode) {
		UserTicketDTO ticketDTO = userTicketDubboRPC.validate(t);
		if (ticketDTO == null) {
			throw ApiException.E_SYS_INVALID_T;
		}

		ThirdAccountSDKPortrait userPortrait = null;
		if (accountType == UserType.mobile) {
			// openId=mobile, accountType=mobile
			// check mobileCode
			boolean isExist = mobileCodeCache.existMobileCode(openId, deviceId, appId, MobileCodeType.bindMobile,
					mobileCode);
			if (!isExist) {
				throw new PassportException(PassportException.EXCEPTION_MOBILE_CODE_INVALID, null);
			}

			userPortrait = new ThirdAccountSDKPortrait();
			userPortrait.setAvatar("");
			userPortrait.setNickname(NickNameUtil.convertNickName(openId));
			userPortrait.setGender(Gender.unknown);
			userPortrait.setUsername(NickNameUtil.convertNickName(openId));

		} else {
			userPortrait = obtainThirdAccountSDK(oauthAppId, openId, accessToken, accountType);
		}
		return userHttpRPC.bindThirdAccount(appId, oauthAppId, openId, deviceId, accessToken, accountType, ip,
				userPortrait.getAvatar(), userPortrait.getNickname(), userPortrait.getUsername(),
				userPortrait.getGender());
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
	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId, UserType accountType) {
		String key = accountAvatarStorage.getOAuthAvatarKey(userId, accountType);
		String uploadToken = accountAvatarStorage.requestUploadToken(key);

		RequestUploadAvatarResultDTO result = new RequestUploadAvatarResultDTO();
		result.setKey(key);
		result.setUploadToken(uploadToken);
		return result;
	}

	@Override
	public ObtainMobileCodeResultDTO obtainMobileCode(String ip, String deviceId, int appId, String mobile,
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
		ObtainMobileCodeResultDTO dto = new ObtainMobileCodeResultDTO();
		dto.setMsg(msg);
		return dto;
	}

}
