package com.open.lcp.core.feature.user.api.facade;

import com.open.lcp.core.common.enums.Gender;
import com.open.lcp.core.feature.user.api.UserType;
import com.open.lcp.core.feature.user.api.dto.BindThirdAccountResultDTO;
import com.open.lcp.core.feature.user.api.dto.NewUserResultDTO;

public class UserFacadeProxy {

	public BindThirdAccountResultDTO bindThirdAccount(int appId, String oauthAppId, String openId, String deviceId,
			String t, UserType accountType, String ip, String avatar, String nickName, String userName, Gender gender){
		//TODO zk
		return null;
	}

	public NewUserResultDTO newUser(String openId, String ip, UserType accountType, String avatar, String nickName,
			String userName, Gender gender){
		//TODO zk
		return null;
	}
}
