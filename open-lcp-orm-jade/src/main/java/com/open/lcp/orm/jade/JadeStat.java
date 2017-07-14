/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package com.open.lcp.orm.jade;

import java.util.LinkedHashMap;
import java.util.Map;

public class JadeStat {
	private static final ThreadLocal<Map<String, Long>> thJadeStat = new ThreadLocal();
	private static final ThreadLocal<Integer> thCounter = new ThreadLocal();
	private static boolean isRec = false;

	public static void enable() {
		isRec = true;
	}

	public static void disable() {
		isRec = false;
	}

	public static void add(String key, long value) {
		if (!(isRec)) {
			return;
		}
		Map m = (Map) thJadeStat.get();
		if (m == null) {
			m = new LinkedHashMap();
			thJadeStat.set(m);
			thCounter.set(Integer.valueOf(1));
		} else {
			thCounter.set(Integer.valueOf(((Integer) thCounter.get()).intValue() + 1));
		}
		if (m.size() > 100) {
			m.clear();
		}
		m.put(key, Long.valueOf(value));
	}

	public static void clear() {
		if (!(isRec)) {
			return;
		}
		Map m = (Map) thJadeStat.get();
		if (m != null) {
			m.clear();
		}
		thCounter.set(Integer.valueOf(0));
	}

	public static int getCount() {
		Integer i = (Integer) thCounter.get();
		if (i == null) {
			return 0;
		}
		return i.intValue();
	}

	public static Map<String, Long> getStat() {
		return ((Map) thJadeStat.get());
	}
}