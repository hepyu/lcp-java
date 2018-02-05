package com.open.lcp.biz.passport.storage;

import com.open.lcp.core.feature.user.api.UserType;

public interface AccountAvatarStorage {

	public String getOAuthAvatarKey(long userId, UserType accountType);

	// public String getOAuthAvatarKey(String prefix, long userId,
	// UserAccountType accountType);

	public String getOAuthAvatarUrl(long userId, UserType accountType);

	// public String getOAuthAvatarUrl(String prefix, long userId,
	// UserAccountType accountType);

	public String getUserAvatarUrl(long userId);

	public String getUserAvatarKey(long passportUserId);

	// public String getUserAvatarKey(String prefix, long userId);

	public String requestUploadToken(String key);

	public String uploadImg(byte[] bytes, String key);

	public String fetchResouce(String fromUrl, String key);

	public String getMd5(String url);

	public String getSHA1(String url);

}
