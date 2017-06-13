package com.open.lcp.passport.service;

import com.open.lcp.framework.security.CheckTicket;
import com.open.lcp.passport.service.impl.dto.UserAccountDto;

public interface UserAccountService {

	public UserAccountDto getUserInfo(Long xlUserId);

	public CheckTicket validateTicket(String t);

}
