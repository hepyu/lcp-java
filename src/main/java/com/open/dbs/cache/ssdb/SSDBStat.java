package com.open.dbs.cache.ssdb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SSDBStat {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(SSDBStat.class);

	public static boolean isEnabled() {
		final Boolean isEnabled = enabled.get();
		if (isEnabled == null) {
			return false;
		}
		return isEnabled;
	}

	public static void enable() {
		if (!isEnabled()) {
			enabled.set(true);
		}
	}

	public static void disable() {
		if (isEnabled()) {
			enabled.set(false);
		}
	}

	private static final int MAX_CACHED_SIZE = 1000;
	private static final ThreadLocal<Boolean> enabled = new ThreadLocal<Boolean>();
	private static final ThreadLocal<Map<String, Integer>> th = new ThreadLocal<Map<String, Integer>>();
	private static final ThreadLocal<List<String>> thCmd = new ThreadLocal<List<String>>();

	public static Map<String, Integer> getCount() {
		return th.get();
	}

	public static List<String> getCmds() {
		return thCmd.get();
	}

	public static void addCMD(String key) {
		if (!isEnabled()) {
			return;
		}
		List<String> ls = thCmd.get();
		if (ls == null) {
			ls = new ArrayList<String>();
			thCmd.set(ls);
		}
		ls.add(key);
		final int size = ls.size();
		if (size > MAX_CACHED_SIZE) {
			logger.warn(String.format("SSDBStat cmds %s > %s, cleared. key:%s", size, MAX_CACHED_SIZE, key));
			ls.clear();
		}
	}

	public static int inc(String key) {
		return inc(key, 1);
	}

	public static void clear() {
		Map<String, Integer> map = th.get();
		if (map != null && !map.isEmpty()) {
			map.clear();
		}
		List<String> ls = thCmd.get();
		if (ls != null && !ls.isEmpty()) {
			ls.clear();
		}
	}

	public static int inc(String key, int incValue) {
		if (!isEnabled()) {
			return 0;
		}
		Map<String, Integer> map = th.get();
		if (map == null) {
			map = new LinkedHashMap<String, Integer>();
			th.set(map);
		} else {
			int size = map.size();
			if (map.size() > MAX_CACHED_SIZE) {
				logger.warn(String.format("SSDBStat inckeys %s > %s, cleared. inckey:%s", size, MAX_CACHED_SIZE, key));
				map.clear();
			}
		}
		Integer oldValue = map.get(key);
		if (oldValue == null) {
			map.put(key, incValue);
			return incValue;
		}
		int newValue = oldValue + incValue;
		map.put(key, newValue);
		return newValue;
	}
}