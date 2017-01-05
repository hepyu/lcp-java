package com.open.lcp.framework.core.loader;

import com.open.lcp.dao.entity.ApiMaxThreadsEntity;

public interface ApiMaxThreadsTimerLoader extends TimerLoader {

	public ApiMaxThreadsEntity getLcpApiMaxThreads(String api);
}
