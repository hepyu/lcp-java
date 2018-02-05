package com.open.lcp.core.feature.user.ticket;

import com.open.lcp.core.feature.user.UserException;

public interface TicketManager {

	public Ticket generateKey(int appId, Long userId) throws UserException;

	public Ticket decodeTicket(String t) throws UserException;

}
