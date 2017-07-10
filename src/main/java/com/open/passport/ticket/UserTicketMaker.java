package com.open.passport.ticket;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.open.common.enums.UserType;
import com.open.dbs.encoder.DBSEncoder;

public class UserTicketMaker {
	private static final long HARD_CODE = 11491703;

	private static int safeCode(int appId, long userId) {
		return (int) (((userId % HARD_CODE) + (appId % HARD_CODE)) % 1000);
	}

	public static String makeTicket(int appId, long userId) {
		final int tick = 10000 + (int) (System.nanoTime() % 10000);// len 4
		final String strAppId = String.valueOf(appId);
		final int safeCode = safeCode(appId, userId);
		final int prefix = tick * 100000 + strAppId.length() * 1000 + safeCode;
		final String deci = String.format("%s%s%s", prefix, appId, userId);
		return DBSEncoder.deciToB69(deci);
	}

	public static String makeTicket(UserType userType, int appId, long userId) {
		if (userType == null || userType == UserType.admin) {
			return makeTicket(appId, userId);
		}
		final int tick = 20000 + userType.type() * 1000 + (int) (System.nanoTime() % 1000);// len
																							// 4
		final String strAppId = String.valueOf(appId);
		final int safeCode = safeCode(appId, userId);
		final long prefix = tick * 100000L + strAppId.length() * 1000 + safeCode;
		final String deci = String.format("%s%s%s", prefix, appId, userId);
		String ticket = DBSEncoder.deciToB69(deci);
		if (ticket == null || ticket.isEmpty()) {
			return null;
		}
		return ticket;
	}

	public static UserTicket toUserTicket(String ticket) {
		final String deci = DBSEncoder.b69ToDeci(ticket);
		UserTicket ut = new UserTicket();
		ut.version = Integer.valueOf(deci.substring(0, 1));
		switch (ut.version) {
		case 1:// 仅红二
			ut.tick = Integer.valueOf(deci.substring(1, 5));
			break;
		case 2:// 支持账号类型
			int type = Integer.valueOf(deci.substring(1, 2));
			ut.userType = UserType.get(type);
			ut.tick = Integer.valueOf(deci.substring(2, 5));
			break;
		default:
			return null;
		}
		final int lenAppId = Integer.valueOf(deci.substring(5, 7));
		ut.safeCode = Integer.valueOf(deci.substring(7, 10));
		ut.appId = Integer.valueOf(deci.substring(10, 10 + lenAppId));
		ut.userId = Long.valueOf(deci.substring(10 + lenAppId));
		if (ut.safeCode != safeCode(ut.appId, ut.userId)) {
			return null;
		}
		return ut;
	}

	public static String toKey(long userId) {
		String su = String.valueOf(userId);
		int mixCode = 0;
		for (int i = 0; i < su.length(); i++) {
			mixCode += Integer.valueOf(su.substring(i, i + 1));
			if (mixCode > 99) {
				mixCode = mixCode % 100;
			}
		}
		String deci = String.format("%s%s0000000000000000000000000000000%s", su.substring(su.length() - 1), mixCode,
				userId);
		return DBSEncoder.deciToB69(deci);
	}

	public static long fromKey(String key) {
		String b69 = DBSEncoder.b69ToDeci(key);
		return Long.valueOf(b69.substring(b69.length() - 20));
	}

	public static class UserTicket {
		private int version;
		private long tick;
		private long userId;
		private int appId;
		private int safeCode;
		private UserType userType;

		public long getUserId() {
			return userId;
		}

		public int getAppId() {
			return appId;
		}

		public long getTick() {
			return tick;
		}

		public int getSafeCode() {
			return safeCode;
		}

		public int getVersion() {
			return version;
		}

		public UserType getUserType() {
			return userType;
		}

	}

	public static boolean isUser(String t) {
		if (StringUtils.isNotEmpty(t)) {
			UserTicket userTicket = UserTicketMaker.toUserTicket(t);
			UserType userType = userTicket != null ? userTicket.getUserType() : null;
			if (userType != null && userType == UserType.user) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		// System.out.println(UserTicketMaker.toUserTicket("NpcspM_ls!q.").getUserId());
		// System.out.println(UserTicketMaker.toKey(416l));
		long max = 29999999999999999L;
		System.out.println(max - 21213867916011521L);
		System.out.println(max - 2121386791601151L);
		String StrBd = "111111111000000003.001";
		BigDecimal bd = new BigDecimal(StrBd);
		System.out.println(bd.doubleValue());
		System.out.println(Double.parseDouble("1111111000000001"));
		System.out.println(Double.parseDouble("32213867916011.5210"));
		System.out.println(Double.parseDouble("32213867916011.5220"));
		System.out.println(Double.parseDouble("32213867916011.5230"));

		System.out.println(Double.parseDouble("32213867916011.523") == Double.parseDouble("32213867916011.522"));

		//
		System.out.println(UserTicketMaker.isUser("P.1B2-ojstof"));
	}

}
