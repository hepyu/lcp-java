package com.open.lcp.plugin.passport.service;

import com.open.lcp.plugin.passport.dto.CheckTicket;
import com.open.lcp.plugin.passport.dto.PassportUserAccountDTO;

public interface AccountTicketService {

	public CheckTicket validateTicket(String t);

	public boolean suicide(String t);

	public PassportUserAccountDTO getUserInfoByTicket(String t);

}
