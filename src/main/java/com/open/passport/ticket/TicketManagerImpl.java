package com.open.passport.ticket;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.open.common.enums.UserType;
import com.open.dbs.cache.redis.cluster.RedisXFactory;
import com.open.passport.PassportException;
import com.open.passport.cache.PassportCache;
import com.open.passport.ticket.UserTicketMaker.UserTicket;

@Component
public class TicketManagerImpl implements TicketManager {

	@Autowired
	private PassportCache passportCache;

	@Override
	public Ticket createSecretKeyCouple(int appId, Long userId) throws PassportException {
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
			throw new PassportException(PassportException.EXCEPTION_LOGIN_FAILED, "EXCEPTION_LOGIN_FAILED", e);
		}
	}

	@Override
	public Ticket decodeTicket(String t) throws PassportException {
		try {
			UserTicket ut = UserTicketMaker.toUserTicket(t);
			Ticket ticket = new Ticket();
			ticket.setAppId(ut.getAppId());
			ticket.setUserSecretKey(null);
			ticket.setUserId(ut.getUserId());
			return ticket;
		} catch (Exception e) {
			throw new PassportException(PassportException.EXCEPTION_NEED_LOGIN, e);
		}
	}

	// @Override
	// public Ticket createSecretKeyCouple2(UserType userType, int appId, Long
	// userId) throws PassportException {
	// try {
	// String userSecretKey = UUID.randomUUID().toString().replaceAll("-", "");
	// String t = UserTicketMaker.makeTicket(userType, appId, userId);
	//
	// Ticket couple = new Ticket();
	// couple.setUserSecretKey(userSecretKey);
	// couple.setUserId(userId);
	// couple.setAppId(appId);
	// couple.setT(t);
	//
	// passportCache.setTicket(couple, t);
	// // passportCache.hsetAppsByPassportUserId(xlUserId, t, appId);
	// return couple;
	// } catch (Exception e) {
	// throw new PassportException(PassportException.EXCEPTION_LOGIN_FAILED,
	// "EXCEPTION_LOGIN_FAILED", e);
	// }
	// }
}
