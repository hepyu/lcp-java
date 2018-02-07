package com.open.lcp.biz.passport.service;

import com.open.lcp.biz.passport.dto.ObtainMobileCodeResultDTO;
import com.open.lcp.core.feature.user.api.MobileCodeType;

public interface MobileCodeService {

	public ObtainMobileCodeResultDTO obtainMobileCode(String ip, String deviceId, int appId, String mobile,
			MobileCodeType type);
}
