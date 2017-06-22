package com.open.lcp.passport.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.open.lcp.passport.PassportException;
import com.open.lcp.passport.cache.PassportCache;
import com.open.lcp.passport.ticket.Ticket;
import com.open.lcp.passport.ticket.TicketManager;

public abstract class AbstractPassportAccount {

	private final Log logger = LogFactory.getLog(AbstractPassportAccount.class);

	@Autowired
	private TicketManager ticketManager;

	@Autowired
	private PassportCache passportCache;

	protected Ticket checkTicket(String t) throws PassportException {
		Ticket ticketFromClient = ticketManager.decodeTicket(t);
		ticketFromClient.setT(t);

		// TODO need review code
		Ticket ticketInSSDB = null;
		try {
			ticketInSSDB = passportCache.getTicket(t);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			return ticketFromClient;
		}
		if (ticketInSSDB == null) {
			throw new PassportException(PassportException.EXCEPTION_TICKET_INVALID, "EXCEPTION_TICKET_INVALID", null);
		} else if (ticketInSSDB.equals(ticketFromClient)) {
			return ticketInSSDB;
		} else {
			throw new PassportException(PassportException.EXCEPTION_TICKET_INVALID, "EXCEPTION_TICKET_INVALID", null);
		}
	}
}
