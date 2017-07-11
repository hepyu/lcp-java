package com.open.lcp.framework.core.api.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.open.lcp.framework.core.annotation.LcpMethod;
import com.open.lcp.framework.core.api.command.ApiCommand;
import com.open.lcp.framework.core.api.command.ApiFacadeMethod;
import com.open.lcp.framework.core.api.command.CommandModelHolder;
import com.open.lcp.framework.core.api.service.ApiCommandLookupService;
import com.open.lcp.framework.core.facade.ApiFacade;

@Service
public class ApiCommandLookupServiceImpl implements ApiCommandLookupService, InitializingBean {

	private static final Log logger = LogFactory.getLog(ApiCommandLookupServiceImpl.class);

	@Autowired
	private Collection<ApiFacade> apiFacadeList;

	@Autowired
	private ApiCommand apiCommand;

	private final Map<String, List<String>> allCommands = new HashMap<String, List<String>>();
	private final Map<String, AtomicLong> apiPvCounter = new HashMap<String, AtomicLong>();

	private String[] apiAndVerList;

	@Override
	public String[] getApiAndVerList() {
		return apiAndVerList;
	}

	@Override
	public long getApiAndVerPv(String apiAndVer) {
		AtomicLong counter = apiPvCounter.get(apiAndVer);
		if (counter == null) {
			return 0;
		}
		return counter.get();
	}

	@Override
	public ApiCommand lookupApiCommand(final ApiFacadeMethod afm) {
		if (afm != null) {
			final String apiAndVer = String.format("%s:%s", afm.getLcpMethod().name(), afm.getLcpMethod().ver());
			AtomicLong counter = apiPvCounter.get(apiAndVer);
			if (counter != null) {
				counter.incrementAndGet();
			}
			return this.apiCommand;
		}
		return null;
	}

	@Override
	public boolean isOpen(String methodValue, String version) {
		final ApiFacadeMethod afm = CommandModelHolder.getApiFacadeMethod(methodValue, version);
		if (afm == null) {
			return false;
		}
		final LcpMethod mm = afm.getLcpMethod();
		if (mm == null) {
			return false;
		}
		return mm.open();
	}

	@Override
	public boolean isNeedLogin(String methodValue, String version) {
		final ApiFacadeMethod apiFacadeMethod = CommandModelHolder.getApiFacadeMethod(methodValue, version);
		if (apiFacadeMethod == null) {
			return false;
		}
		return apiFacadeMethod.getLcpMethod().logon();
	}

	@Override
	public Map<String, List<String>> getCommands() {
		return this.allCommands;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// Assert.notNull(apiFacadeList, "groupModelList is required");
		Assert.notNull(apiCommand, "apiCommand is required");
		for (ApiFacade apiFacade : apiFacadeList) {
			List<ApiFacadeMethod> ls = CommandModelHolder.getMethodModelList(apiFacade);
			logger.info(apiFacade.toString());
			for (ApiFacadeMethod l : ls) {
				final String methodName = l.getLcpMethod().name();
				final String version = l.getLcpMethod().ver();
				final String apiAndVer = String.format("%s:%s", methodName, version);
				apiPvCounter.put(apiAndVer, new AtomicLong(-1));
				logger.info(l.toString());
				List<String> vers = allCommands.get(methodName);
				if (vers == null) {
					vers = new ArrayList<String>();
					allCommands.put(methodName, vers);
				} else if (vers.contains(version)) {
					final String errorMsg = String.format("ApiFacade load error, %s exist %s[%s/%s], exit."//
							, l.getMethodOfObject().getClass().getName()//
							, l.getMethod().getName()//
							, methodName, methodName//
					);
					Assert.notNull(null, errorMsg);
				}
				vers.add(version);
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("ApiFacade load %s: %s[%s]"//
							, l.getMethodOfObject().getClass().getName()//
							, l.getMethod().getName()//
							, methodName//
					));
				}
			}
		}
		apiAndVerList = apiPvCounter.keySet().toArray(new String[apiPvCounter.size()]);
	}
}
