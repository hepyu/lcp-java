package com.open.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum UserType {

	admin(0), user(1), test(2);

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
