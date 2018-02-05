package com.open.lcp.core.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum CoreUserType {

	admin(0), user(1), test(2);

	private int type;

	private CoreUserType(int type) {
		this.type = type;
		Holder.cachedUserType.put(type, this);
	}

	public int type() {
		return this.type;
	}

	public static CoreUserType get(int type) {
		return Holder.cachedUserType.get(type);
	}

	private static final class Holder {
		private static final Map<Integer, CoreUserType> cachedUserType = new HashMap<Integer, CoreUserType>();
	}
}
