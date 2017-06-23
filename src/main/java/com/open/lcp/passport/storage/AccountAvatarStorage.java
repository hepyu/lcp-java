package com.open.lcp.passport.storage;

import com.open.lcp.passport.UserAccountType;

public interface AccountAvatarStorage {

	// public String getOAuthAvatarUrl(long passportUserId,
	// UserAccountTypeEnum accountType);

	// public String getOAuthAvatarKey(long passportUserId,
	// UserAccountTypeEnum accountType);

	public String getOAuthAvatarKey(String prefix, long userId, UserAccountType accountType);

	public String getUserAvatarUrl(String prefix, long passportUserId);

	// public String getUserAvatarKey(long passportUserId);

	public String getUserAvatarKey(String prefix, long passportUserId);

	public String requestUploadToken(String key);

	public String uploadImg(byte[] bytes, String key);

	public String fetchResouce(String fromUrl, String key);

	public String getMd5(String url);

	public String getSHA1(String url);

}
