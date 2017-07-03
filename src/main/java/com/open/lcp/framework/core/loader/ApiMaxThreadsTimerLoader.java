package com.open.lcp.framework.core.loader;

import com.open.lcp.framework.core.api.service.dao.entity.LcpApiMaxThreadsEntity;

public interface ApiMaxThreadsTimerLoader extends TimerLoader {

	public LcpApiMaxThreadsEntity getLcpApiMaxThreads(String api);
}
