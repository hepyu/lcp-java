package com.open.lcp.core.feature.user.ticket;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.open.lcp.core.feature.user.UserException;
import com.open.lcp.core.feature.user.service.cache.UserCache;
import com.open.lcp.core.feature.user.ticket.UserTicketMaker.UserTicket;

@Component
public class TicketManagerImpl implements TicketManager {

	@Autowired
	private UserCache passportCache;

	@Override
	public Ticket generateKey(int appId, Long userId) throws UserException {
		try {
			String userSecretKey = UUID.randomUUID().toString().replaceAll("-", "");
			String t = UserTicketMaker.makeTicket(appId, userId);

			Ticket couple = new Ticket();
			couple.setUserSecretKey(userSecretKey);
			couple.setUserId(userId);
			couple.setAppId(appId);
			couple.setT(t);

			passportCache.setTicket(couple, t);
			return couple;
		} catch (Exception e) {
			throw new UserException(UserException.EXCEPTION_LOGIN_FAILED, "EXCEPTION_LOGIN_FAILED", e);
		}
	}

	@Override
	public Ticket decodeTicket(String t) throws UserException {
		try {
			UserTicket ut = UserTicketMaker.toUserTicket(t);
			Ticket ticket = new Ticket();
			ticket.setAppId(ut.getAppId());
			ticket.setUserSecretKey(null);
			ticket.setUserId(ut.getUserId());
			return ticket;
		} catch (Exception e) {
			throw new UserException(UserException.EXCEPTION_NEED_LOGIN, e);
		}
	}

}
