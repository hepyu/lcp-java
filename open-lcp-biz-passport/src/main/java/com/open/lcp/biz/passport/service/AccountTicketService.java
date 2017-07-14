package com.open.lcp.biz.passport.service;

import com.open.lcp.biz.passport.dto.PassportUserAccountDTO;
import com.open.lcp.core.base.info.BaseUserAccountTicketInfo;
import com.open.lcp.core.base.service.BaseUserAccountTicketService;

public interface AccountTicketService extends BaseUserAccountTicketService {

	public BaseUserAccountTicketInfo validateTicket(String t);

	public boolean suicide(String t);

	public PassportUserAccountDTO getUserInfoByTicket(String t);

}
