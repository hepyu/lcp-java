package com.open.lcp.biz.passport.service.cache;

import com.open.lcp.core.feature.user.api.MobileCodeType;

public interface MobileCodeCache {

	public Boolean setMobileCode(String mobile, String deviceId, int appId, MobileCodeType type, String mobileCode);

	public Boolean existMobileCode(String mobile, String deviceId, int appId, MobileCodeType type, String mobileCode);
}
