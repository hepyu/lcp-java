package com.open.lcp.biz.passport.service;

import java.util.List;

import com.open.lcp.biz.passport.MobileCodeType;
import com.open.lcp.biz.passport.UserAccountType;
import com.open.lcp.biz.passport.dto.BindAccountResultDTO;
import com.open.lcp.biz.passport.dto.LoginByMobileResultDTO;
import com.open.lcp.biz.passport.dto.LoginByOAuthResultDTO;
import com.open.lcp.biz.passport.dto.ObtainMobileCodeDTO;
import com.open.lcp.biz.passport.dto.PassportOAuthAccountDTO;
import com.open.lcp.biz.passport.dto.RequestUploadAvatarResultDTO;
import com.open.lcp.core.api.info.CoreFeatureUserAccountInfo;
import com.open.lcp.core.api.service.CoreFeatureUserAccountInfoService;
import com.open.lcp.core.common.enums.Gender;

public interface AccountInfoService extends CoreFeatureUserAccountInfoService {

	public boolean suicide(String t);

	public LoginByMobileResultDTO loginByMobileAccount(int appId, String mobile, String mobileCode, String deviceId,
			String ip, String ua);

	public LoginByOAuthResultDTO loginByThirdAccount(int appId, String oauthAppId, String openId, String accessToken,
			String deviceId, String ip, UserAccountType accountType, String ua);

	public List<PassportOAuthAccountDTO> getOAuthAccountList(Long userId);

	public int unbindAccount(Long userId, UserAccountType userAccountType);

	public int updateGender(Long userId, Gender gender);

	public int updateNickName(Long userId, String nickName);

	public int updateDescription(Long userId, String description);

	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId);

	public String commitUploadAvatar(Long userId);

	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId, UserAccountType accountType);

	public String commitUploadAvatar(Long userId, UserAccountType accountType);

	public String getUserType(Long userId);

	public ObtainMobileCodeDTO obtainMobileCode(String ip, String deviceId, int appId, String mobile,
			MobileCodeType type);

	public boolean bindMobileAccount(int appId, String mobile, String mobileCode, String deviceId, String t, String ip);

	public BindAccountResultDTO bindThirdAccount(int appId, String oauthAppId, String openId, String accessToken,
			String deviceId, String t, UserAccountType accountType, String ip);

}
