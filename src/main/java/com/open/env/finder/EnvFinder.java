package com.open.env.finder;

import com.open.dbs.cache.SSDBLoader;
import com.open.dbs.cache.SSDBX;

public class EnvFinder {

	public static SSDBX loadSSDBX(String source) {
		return SSDBLoader.loadSSDBX(source, null);
	}

	public static SSDBX loadSSDBX(String source, String prefix) {
		return SSDBLoader.loadSSDBX(source, prefix);
	}

	public static String getProfile() {
		// TODO
		return "dev";
	}

}
