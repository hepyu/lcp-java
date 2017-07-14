/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package com.open.lcp.dbs.cache;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class CacheDBSEncoder {
	private static final String CHARS;
	// private static final String chars_src =
	// "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ._-!";
	public static final int MAX_INT_LENGTH = String.valueOf(2147483647).length();
	public static final int MAX_LONG_LENGTH = String.valueOf(9223372036854775807L).length();
	private static final String STR_0;
	private static final BigInteger bintLength;

	static {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < MAX_LONG_LENGTH; ++i) {
			sb.append('0');
		}
		STR_0 = sb.toString();
		int[] indexs = { 8, 49, 51, 23, 60, 14, 4, 42, 24, 52, 61, 62, 33, 43, 6, 15, 58, 2, 63, 26, 45, 37, 27, 18, 47,
				29, 57, 10, 48, 38, 19, 39, 1, 30, 12, 13, 5, 53, 34, 35, 7, 64, 55, 46, 56, 65, 28, 11, 50, 31, 21, 3,
				44, 36, 9, 40, 32, 25, 16, 54, 17, 20, 22, 59, 0, 41 };
		sb = new StringBuilder();
		for (int i : indexs) {
			sb.append("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ._-!".charAt(i));
		}
		CHARS = sb.toString();

		bintLength = BigInteger.valueOf(CHARS.length());
	}

	public static int charSize() {
		return CHARS.length();
	}

	public static BigInteger toBint(String deci) {
		return new BigInteger(deci);
	}

	public static String deciToB69(String deci) {
		return toB69(toBint(deci));
	}

	public static String b69ToDeci(String b69) {
		return deBintB69(b69).toString();
	}

	public static String toB69(long value) {
		if (value < 0L) {
			return null;
		}
		if (value < CHARS.length()) {
			return new String(new char[] { CHARS.charAt((int) value) });
		}
		StringBuilder sb = new StringBuilder();
		while (value > 0L) {
			int mod = (int) (value % CHARS.length());
			value /= CHARS.length();
			sb.insert(0, CHARS.charAt(mod));
		}
		return sb.toString();
	}

	public static long deB69(String b69) {
		long value = 0L;
		for (int i = 0; i < b69.length(); ++i) {
			int index = CHARS.indexOf(b69.charAt(i));
			if (index < 0)
				return -1L;
			value = value * CHARS.length() + index;
		}
		return value;
	}

	public static String toB69(BigInteger bint) {
		StringBuilder sb = new StringBuilder();
		while (bint.signum() > 0) {
			int mod = bint.mod(bintLength).intValue();
			bint = bint.divide(bintLength);
			sb.insert(0, CHARS.charAt(mod));
		}
		return sb.toString();
	}

	public static BigInteger deBintB69(String b69) {
		BigInteger bint = BigInteger.valueOf(0L);
		for (int i = 0; i < b69.length(); ++i) {
			int index = CHARS.indexOf(b69.charAt(i));
			if (index < 0)
				return null;
			bint = bint.multiply(bintLength).add(BigInteger.valueOf(index));
		}
		return bint;
	}

	public static String fullInt(int value) {
		String strInt = STR_0 + String.valueOf(value);
		return strInt.substring(strInt.length() - MAX_INT_LENGTH);
	}

	public static String fullLong(long value) {
		String strInt = STR_0 + String.valueOf(value);
		return strInt.substring(strInt.length() - MAX_LONG_LENGTH);
	}

	public static String fillZeroPreFix(String value, int length) {
		if (value == null) {
			return null;
		}
		if (value.length() >= length) {
			return value;
		}
		do
			value = STR_0 + value;
		while (value.length() < length);

		return value.substring(value.length() - length);
	}

	@SuppressWarnings("unchecked")
	public static void makeMix() {
		@SuppressWarnings("rawtypes")
		Set set = new HashSet("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ._-!".length());
		while (set.size() < "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ._-!".length()) {
			int index = (int) (System.nanoTime()
					% "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ._-!".length());
			if (set.contains(Integer.valueOf(index))) {
				continue;
			}
			set.add(Integer.valueOf(index));
			System.out.print(index);
			System.out.print(",");
		}
	}
}