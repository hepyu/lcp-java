package com.open.lcp.biz.passport.service.impl;

import org.springframework.stereotype.Service;

import com.open.lcp.biz.passport.dto.RequestUploadAvatarResultDTO;
import com.open.lcp.biz.passport.service.AbstractAccountService;
import com.open.lcp.biz.passport.service.AvatarService;
import com.open.lcp.core.feature.user.api.UserType;

@Service
public class AvatarServiceImpl extends AbstractAccountService implements AvatarService {

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
}
