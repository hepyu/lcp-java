package com.open.lcp.framework.util;

import java.security.MessageDigest;

public class Md5Utils {

	public static String md5(String str) throws Exception {
		byte[] src = MessageDigest.getInstance("md5").digest(str.getBytes());

		StringBuilder stringBuilder = new StringBuilder("");
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}

		return stringBuilder.toString();
	}

	public static void main(String[] args) throws Exception {
		System.out
				.println(md5("http://www.baidu.com/asfdasdfsaddsdafdasdfasfdasdfasfdasfwqreeqwpyusdagfjasfpoasifasd;jfsalfsadfasf"));
	}
}
