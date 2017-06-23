package com.open.lcp.passport.service;

import com.open.lcp.passport.dto.CheckTicket;
import com.open.lcp.passport.dto.PassportUserAccountDTO;

public interface AccountTicketService {

	public CheckTicket validateTicket(String t);

	public boolean suicide(String t);

	public PassportUserAccountDTO getUserInfoByTicket(String t);

}
