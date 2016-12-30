package com.open.lcp.framework.security.service;

import java.util.HashMap;
import java.util.Map;

import com.open.lcp.framework.security.domain.UserInfo;

public interface UserAccountService {

	public UserInfo getUserInfo(Long xlUserId);

	public enum UserType {
		admin(0), user(1);

		private int type;

		private UserType(int type) {
			this.type = type;
			Holder.cachedUserType.put(type, this);
		}

		public int type() {
			return this.type;
		}

		public static UserType get(int type) {
			return Holder.cachedUserType.get(type);
		}

		private static final class Holder {
			private static final Map<Integer, UserType> cachedUserType = new HashMap<Integer, UserType>();
		}
	}

	public enum Gender {
		female(0), male(1), unknown(2);
		private int type;

		Gender(int type) {
			this.type = type;
			Holder.cached.put(type, this);
		}

		public int type() {
			return type;
		}

		public static Gender get(int type) {
			return Holder.cached.get(type);
		}

		private static class Holder {
			private static final Map<Integer, Gender> cached = new HashMap<Integer, Gender>();
		}
	}

}
