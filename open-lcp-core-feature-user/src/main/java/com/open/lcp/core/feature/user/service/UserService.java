package com.open.lcp.core.feature.user.service;

import java.util.List;

import com.open.lcp.core.common.enums.Gender;
import com.open.lcp.core.feature.user.api.UserType;
import com.open.lcp.core.feature.user.api.dto.BindThirdAccountResultDTO;
import com.open.lcp.core.feature.user.api.dto.UserDetailInfoDTO;
import com.open.lcp.core.feature.user.api.dto.UserTicketDTO;
import com.open.lcp.core.feature.user.dto.UserOAuthInfoDTO;

public interface UserService {

	// (1).user-read

	public String getUserCategory(Long userId);

	public List<UserOAuthInfoDTO> getUserOAuthInfoList(Long userId);

	public UserDetailInfoDTO getUserDetailInfo(Long userId);

	public Long getUserId(String openId, UserType userType);

	// (2).user-write

	public boolean suicide(String t);

	public int unbindUserOAuthInfo(Long userId, UserType userAccountType);

	public int updateGender(Long userId, Gender gender);

	public int updateNickName(Long userId, String nickName);

	public int updateAvatar(Long userId, String avatar);

	public int updateDescription(Long userId, String description);

	public BindThirdAccountResultDTO bindThirdAccount(int appId, String oauthAppId, String openId, String deviceId,
			String t, UserType accountType, String ip, String avatar, String nickName, String userName, Gender gender);

	public Long newUser(String openId, String ip, UserType accountType, String avatar, String nickName, String userName,
			Gender gender);

	// (3).user-ticket

	public UserTicketDTO validate(String t);

	public UserDetailInfoDTO getUserDetailInfo(String ticket);

}
