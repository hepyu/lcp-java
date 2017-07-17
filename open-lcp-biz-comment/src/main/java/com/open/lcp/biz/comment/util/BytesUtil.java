package com.open.lcp.biz.comment.util;

public class BytesUtil {
	
	public static byte[] spliceBytes(byte[]... bs) {
		int len = 0;
		for (byte[] b : bs) {
			len += b.length;
		}
		byte[] newBytes = new byte[len];
		int pos = 0;
		for (byte[] b : bs) {
			System.arraycopy(b, 0, newBytes, pos, b.length);
			pos += b.length;
		}
		return newBytes;
	}

}
