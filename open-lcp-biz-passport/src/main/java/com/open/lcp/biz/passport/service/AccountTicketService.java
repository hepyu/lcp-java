package com.open.lcp.biz.passport.service;

import com.open.lcp.biz.passport.dto.PassportUserAccountDTO;
import com.open.lcp.core.api.info.BasicUserAccountTicketInfo;
import com.open.lcp.core.api.service.BaseUserAccountTicketService;

public interface AccountTicketService extends BaseUserAccountTicketService {

	public BasicUserAccountTicketInfo validateTicket(String t);

	public boolean suicide(String t);

	public PassportUserAccountDTO getUserInfo(String ticket);

}
