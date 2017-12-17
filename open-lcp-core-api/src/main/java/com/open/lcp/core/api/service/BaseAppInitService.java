package com.open.lcp.core.api.service;

import com.open.lcp.core.api.info.BasicAppInitInfo;

public interface BaseAppInitService {

	BasicAppInitInfo getAppInitInfo(String deviceId);

}
