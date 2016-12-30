package com.open.lcp.framework.core.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.open.lcp.framework.core.api.command.ApiCommand;
import com.open.lcp.framework.core.facade.ApiFacade;
import com.open.lcp.framework.core.service.ApiFacadeLookupService;

@Service
public class ApiFacadeLookupServiceImpl implements ApiFacadeLookupService, InitializingBean {

	@Autowired
	private Collection<ApiFacade> apiFacadeList;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println(apiFacadeList);

	}

	@Override
	public ApiCommand lookupApiCommand(String methodValue, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen(String methodValue, String version) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNeedLogin(String methodValue, String version) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, List<String>> getCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getApiAndVerList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getApiAndVerPv(String apiAndVer) {
		// TODO Auto-generated method stub
		return 0;
	}

}
