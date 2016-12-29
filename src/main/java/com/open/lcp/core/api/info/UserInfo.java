package com.open.lcp.core.api.info;

import java.util.HashMap;
import java.util.Map;

public interface UserInfo {
	long getId();

	Gender getGender();

	String getName();

	String getNickName();

	String getPortrait();

	<U> U getExt();

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
