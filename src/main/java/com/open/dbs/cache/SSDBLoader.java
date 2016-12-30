package com.open.dbs.cache;

public class SSDBLoader {

	public static SSDBX loadSSDBX(String source) {
		return loadSSDBX(source, null);
	}

	public static SSDBX loadSSDBX(String source, String prefix) {
		return SSDBXFactory.getSSDBX(source, prefix);
	}
}
