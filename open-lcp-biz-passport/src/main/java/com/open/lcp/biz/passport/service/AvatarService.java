package com.open.lcp.biz.passport.service;

import com.open.lcp.biz.passport.dto.RequestUploadAvatarResultDTO;
import com.open.lcp.core.feature.user.api.UserType;

public interface AvatarService {

	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId);

	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId, UserType accountType);
}
