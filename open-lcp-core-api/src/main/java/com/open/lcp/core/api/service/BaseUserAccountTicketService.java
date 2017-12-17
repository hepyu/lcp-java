package com.open.lcp.core.api.service;

import com.open.lcp.core.api.info.BasicUserAccountTicketInfo;

public interface BaseUserAccountTicketService {

	public BasicUserAccountTicketInfo validateTicket(String ticket);

}
