package com.open.passport.service;

import com.open.passport.dto.CheckTicket;
import com.open.passport.dto.PassportUserAccountDTO;

public interface AccountTicketService {

	public CheckTicket validateTicket(String t);

	public boolean suicide(String t);

	public PassportUserAccountDTO getUserInfoByTicket(String t);

}
