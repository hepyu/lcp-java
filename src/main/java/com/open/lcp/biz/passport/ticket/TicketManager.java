package com.open.lcp.biz.passport.ticket;

import com.open.lcp.biz.passport.PassportException;

public interface TicketManager {

	public Ticket createSecretKeyCouple(int appId, Long userId) throws PassportException;

	// public Ticket createSecretKeyCouple2(UserType userType, int appId, Long
	// userId) throws PassportException;

	public Ticket decodeTicket(String t) throws PassportException;

}
