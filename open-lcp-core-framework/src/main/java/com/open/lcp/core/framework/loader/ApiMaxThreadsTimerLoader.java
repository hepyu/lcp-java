package com.open.lcp.core.framework.loader;

import com.open.lcp.core.framework.api.service.dao.entity.ApiMaxThreadsEntity;

public interface ApiMaxThreadsTimerLoader extends TimerLoader {

	public ApiMaxThreadsEntity getLcpApiMaxThreads(String api);
}
