package com.open.lcp.core.feature.user.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.open.lcp.core.feature.user.UserException;
import com.open.lcp.core.feature.user.service.cache.UserCache;
import com.open.lcp.core.feature.user.service.dao.UserDetailInfoDAO;
import com.open.lcp.core.feature.user.service.dao.UserOAuthInfoDAO;
import com.open.lcp.core.feature.user.ticket.Ticket;
import com.open.lcp.core.feature.user.ticket.TicketManager;

public abstract class AbstractUserService {

	private final Log logger = LogFactory.getLog(AbstractUserService.class);

	@Autowired
	protected TicketManager ticketManager;

	@Autowired
	protected UserCache userCache;

	@Autowired
	protected UserDetailInfoDAO userDetailInfoDAO;

	@Autowired
	protected UserOAuthInfoDAO userOAuthInfoDAO;

	protected Ticket checkTicket(String t) throws UserException {
		Ticket ticketFromClient = ticketManager.decodeTicket(t);
		ticketFromClient.setT(t);

		Ticket ticketInCache = null;
		try {
			ticketInCache = userCache.getTicket(t);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			return ticketFromClient;
		}
		if (ticketInCache == null) {
			throw new UserException(UserException.EXCEPTION_TICKET_INVALID, "EXCEPTION_TICKET_INVALID", null);
		} else if (ticketInCache.equals(ticketFromClient)) {
			return ticketInCache;
		} else {
			throw new UserException(UserException.EXCEPTION_TICKET_INVALID, "EXCEPTION_TICKET_INVALID", null);
		}
	}

}
