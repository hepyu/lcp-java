package com.open.lcp.core.feature.user.util;

import com.open.lcp.core.common.enums.Gender;
import com.open.lcp.core.feature.user.api.dto.UserDetailInfoDTO;
import com.open.lcp.core.feature.user.dto.UserOAuthInfoDTO;
import com.open.lcp.core.feature.user.service.dao.entity.UserDetailInfoEntity;
import com.open.lcp.core.feature.user.service.dao.entity.UserOAuthInfoEntity;

public class UserUtil {

	public static UserDetailInfoDTO convertToUserDetailInfoDTO(UserDetailInfoEntity entity) {
		UserDetailInfoDTO dto = new UserDetailInfoDTO();
		dto.setAvatar(entity.getAvatar());
		dto.setDescription(entity.getDescription());
		dto.setGender(Gender.get(entity.getGender()));
		// dto.setMobile();
		dto.setNickName(entity.getNickName());
		dto.setRegistIp(entity.getRegistIp());
		dto.setRegistTime(entity.getRegistTime());
		dto.setUpdateIp(entity.getUpdateIp());
		dto.setUpdateTime(entity.getUpdateTime());
		dto.setUserId(entity.getUserId());
		dto.setUserName(entity.getUserName());
		return dto;
	}

	public static UserOAuthInfoDTO convertToUserOAuthInfoDTO(UserOAuthInfoEntity entity) {
		UserOAuthInfoDTO dto = new UserOAuthInfoDTO();
		dto.setBindIp(entity.getBindIp());
		dto.setBindTime(entity.getBindTime());
		dto.setGender(Gender.get(entity.getGender()));
		dto.setAvatar(entity.getAvatar());
		dto.setNickName(entity.getNickName());
		dto.setOpenId(entity.getOpenId());
		dto.setType(entity.getUserAccountType());
		dto.setUpdateIp(entity.getUpdateIp());
		dto.setUpdateTime(entity.getUpdateTime());
		dto.setUserId(entity.getUserId());
		dto.setUserName(entity.getUserName());
		return dto;
	}
}
