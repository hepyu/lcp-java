package com.open.lcp.core.api.service;

import com.open.lcp.core.api.info.CoreFeatureAppInitInfo;

public interface CoreFeatureAppInitService {

	CoreFeatureAppInitInfo getAppInitInfo(String deviceId);

}
