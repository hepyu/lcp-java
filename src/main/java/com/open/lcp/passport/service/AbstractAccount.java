package com.open.lcp.passport.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.open.lcp.passport.PassportException;
import com.open.lcp.passport.cache.PassportCache;
import com.open.lcp.passport.service.dao.PassportOAuthAccountDao;
import com.open.lcp.passport.service.dao.PassportUserAccountDao;
import com.open.lcp.passport.service.dao.entity.PassportUserAccountEntity;
import com.open.lcp.passport.ticket.Ticket;
import com.open.lcp.passport.ticket.TicketManager;

public abstract class AbstractAccount {

	private final Log logger = LogFactory.getLog(AbstractAccount.class);

	@Autowired
	private TicketManager ticketManager;

	@Autowired
	protected PassportCache passportCache;

	@Autowired
	protected PassportUserAccountDao passportUserAccountDao;

	@Autowired
	protected PassportOAuthAccountDao passportOAuthAccountDao;

	protected Ticket checkTicket(String t) throws PassportException {
		Ticket ticketFromClient = ticketManager.decodeTicket(t);
		ticketFromClient.setT(t);

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

	protected PassportUserAccountEntity obtainPassportUserAccount(Long userId) {
		if (userId == null || userId <= 0) {
			return null;
		}

		PassportUserAccountEntity userAccount = passportCache.getUserInfoByUserId(userId);
		if (userAccount == null) {
			userAccount = passportUserAccountDao.getUserInfoByUserId(userId);

			if (userAccount != null) {
				passportCache.setUserInfoByUserId(userId, userAccount);
			}
		}
		return userAccount;
	}
}
