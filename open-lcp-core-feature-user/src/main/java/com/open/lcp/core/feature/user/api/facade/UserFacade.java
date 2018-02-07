package com.open.lcp.core.feature.user.api.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.open.lcp.core.api.annotation.LcpHttpMethod;
import com.open.lcp.core.api.facade.ApiFacade;
import com.open.lcp.core.common.enums.Gender;
import com.open.lcp.core.feature.user.api.UserType;
import com.open.lcp.core.feature.user.api.dto.BindThirdAccountResultDTO;
import com.open.lcp.core.feature.user.api.dto.NewUserResultDTO;
import com.open.lcp.core.feature.user.service.UserService;

@Component
public class UserFacade implements ApiFacade {

	@Autowired
	private UserService userService;

	@LcpHttpMethod(name = "user.bind.thirdAccount", ver = "1.0", desc = "绑定第三方账号", logon = true)
	public BindThirdAccountResultDTO bindThirdAccount(int appId, String oauthAppId, String openId, String deviceId,
			String t, UserType accountType, String ip, String avatar, String nickName, String userName, Gender gender) {
		return userService.bindThirdAccount(appId, oauthAppId, openId, deviceId, t, accountType, ip, avatar, nickName,
				userName, gender);
	}

	@LcpHttpMethod(name = "user.create", ver = "1.0", desc = "新建账号", logon = false)
	public NewUserResultDTO newUser(String openId, String ip, UserType accountType, String avatar, String nickName,
			String userName, Gender gender) {
		NewUserResultDTO dto = new NewUserResultDTO();
		dto.setUserId(userService.newUser(openId, ip, accountType, avatar, nickName, userName, gender));
		return dto;
	}
}
