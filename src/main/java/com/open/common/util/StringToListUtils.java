package com.open.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串转数组的工具类，以,�?;分隔
 * 
 * @author
 */
public class StringToListUtils {

	public static String[] toStringArray(String src) {
		if (src == null) {
			return null;
		}
		return src.split("[,;]");
	}

	public static List<String> toStringList(String src) {
		return toStringList(src, false);
	}

	public static List<String> toStringList(String src, boolean skipEmpty) {
		String[] array = toStringArray(src);
		if (array == null)
			return null;
		List<String> ls = new ArrayList<String>(array.length);
		for (String l : array) {
			if (skipEmpty && l.length() == 0)
				continue;
			ls.add(l);
		}
		return ls;
	}

	public static List<Integer> toIntList(String src) {
		String[] array = toStringArray(src);
		if (array == null)
			return null;
		if (array[0].startsWith("["))
			array[0] = array[0].substring(1);
		if (array[array.length - 1].endsWith("]"))
			array[array.length - 1] = array[array.length - 1].substring(0, array[array.length - 1].length() - 1);
		List<Integer> ls = new ArrayList<Integer>(array.length);
		for (String l : array) {
			l = l.trim();
			if (l.length() == 0)
				continue;
			ls.add(Integer.valueOf(l));
		}
		return ls;
	}

	public static List<Long> toLongList(String src) {
		String[] array = toStringArray(src);
		if (array == null)
			return null;
		if (array[0].startsWith("["))
			array[0] = array[0].substring(1);
		if (array[array.length - 1].endsWith("]"))
			array[array.length - 1] = array[array.length - 1].substring(0, array[array.length - 1].length() - 1);
		List<Long> ls = new ArrayList<Long>(array.length);
		for (String l : array) {
			l = l.trim();
			if (l.length() == 0)
				continue;
			ls.add(Long.valueOf(l));
		}
		return ls;
	}

	public static int[] toIntArray(String src) {
		String[] array = toStringArray(src);
		if (array == null)
			return null;
		int[] is = new int[array.length];
		for (int i = 0; i < array.length; i++) {
			array[i] = array[i].trim();
			if (array[i].length() == 0)
				continue;
			is[i] = Integer.valueOf(array[i]);
		}
		return is;
	}

	public static long[] toLongArray(String src) {
		String[] array = toStringArray(src);
		if (array == null)
			return null;
		long[] is = new long[array.length];
		for (int i = 0; i < array.length; i++) {
			array[i] = array[i].trim();
			if (array[i].length() == 0)
				continue;
			is[i] = Long.valueOf(array[i]);
		}
		return is;
	}
}
