package com.open.lcp.biz.passport.service;

import com.open.lcp.biz.passport.dto.CheckTicket;
import com.open.lcp.biz.passport.dto.PassportUserAccountDTO;

public interface AccountTicketService {

	public CheckTicket validateTicket(String t);

	public boolean suicide(String t);

	public PassportUserAccountDTO getUserInfoByTicket(String t);

}
