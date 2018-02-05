package com.open.lcp.core.feature.user.api.rpc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.open.lcp.core.feature.user.api.UserType;
import com.open.lcp.core.feature.user.api.dto.GetUserIdResultDTO;
import com.open.lcp.core.feature.user.api.dto.UserDetailInfoDTO;
import com.open.lcp.core.feature.user.api.rpc.UserDubboRPC;
import com.open.lcp.core.feature.user.service.UserService;

@Service
public class UserDubboRPCImpl implements UserDubboRPC {

	@Autowired
	private UserService userService;

	@Override
	public UserDetailInfoDTO getUserDetailInfo(Long userId) {
		return userService.getUserDetailInfo(userId);
	}

	@Override
	public GetUserIdResultDTO getUserId(String openId, UserType userType) {
		GetUserIdResultDTO dto = new GetUserIdResultDTO();
		dto.setUserId(userService.getUserId(openId, userType));
		return dto;
	}

}
