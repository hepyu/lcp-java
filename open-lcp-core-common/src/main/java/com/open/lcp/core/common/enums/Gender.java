package com.open.lcp.core.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum Gender {
	female(0), male(1), unknown(2);
	private int gender;

	Gender(int gender) {
		this.gender = gender;
		Holder.cached.put(gender, this);
	}

	public int gender() {
		return gender;
	}

	public static Gender get(int gender) {
		return Holder.cached.get(gender);
	}

	private static class Holder {
		private static final Map<Integer, Gender> cached = new HashMap<Integer, Gender>();
	}
}