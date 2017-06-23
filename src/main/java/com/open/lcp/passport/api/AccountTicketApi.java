package com.open.lcp.passport.api;

import com.open.lcp.passport.dto.CheckTicket;
import com.open.lcp.passport.dto.PassportUserAccountDTO;

public interface AccountTicketApi {

	public CheckTicket validateTicket(String t);

	public boolean suicide(String t);

	public PassportUserAccountDTO getUserInfoByTicket(String t);

}
