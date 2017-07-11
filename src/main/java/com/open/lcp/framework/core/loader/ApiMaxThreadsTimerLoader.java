package com.open.lcp.framework.core.loader;

import com.open.lcp.framework.core.api.service.dao.entity.ApiMaxThreadsEntity;

public interface ApiMaxThreadsTimerLoader extends TimerLoader {

	public ApiMaxThreadsEntity getLcpApiMaxThreads(String api);
}
