package com.open.lcp.core.feature.user.api.rpc;

import com.open.lcp.core.common.enums.Gender;
import com.open.lcp.core.feature.user.api.UserType;
import com.open.lcp.core.feature.user.api.dto.BindThirdAccountResultDTO;
import com.open.lcp.core.feature.user.api.dto.NewUserResultDTO;

public interface UserHttpRPC {

	public BindThirdAccountResultDTO bindThirdAccount(int appId, String oauthAppId, String openId, String deviceId,
			String t, UserType accountType, String ip, String avatar, String nickName, String userName, Gender gender);

	public NewUserResultDTO newUser(String openId, String ip, UserType accountType, String avatar, String nickName,
			String userName, Gender gender);
}
