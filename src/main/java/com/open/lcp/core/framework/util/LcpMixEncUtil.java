package com.open.lcp.core.framework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.open.lcp.common.util.AESUtils;
import com.open.lcp.common.util.Base64Utils;

public class LcpMixEncUtil {
	/**
	 * 鎻愪緵缁檃pp.init鎺ュ彛鐢紝imei鍜宮ac鐨勮В瀵嗐��
	 * 
	 * 鍏坆ase64 decode锛岀劧鍚庣敤key鐨刴d5鍚庣殑16byte鏉ヨВ瀵嗭紝寰楀埌鐨刡yte[]鐩存帴new String銆�
	 * 
	 * @param srcBase64
	 * @param key
	 * @return
	 */
	public static String AesDecode(String srcBase64, byte[] aesKey) {
		try {
			final byte[] inBytes = Base64Utils.decode(srcBase64);
			final byte[] outBytes = AESUtils.decrypt(inBytes, aesKey);
			return new String(outBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 鎻愪緵缁檃pp.init鎺ュ彛鐢紝imei鍜宮ac鐨勫姞瀵�
	 * 
	 * @param content
	 *            鍘熸枃 mac鍘绘帀鍐掑彿鍜屽噺鍙凤紝杞垚澶у啓
	 * @param key
	 * @return
	 */
	public static String AesEncode(String content, byte[] aesKey) {
		try {
			final byte[] outBytes = AESUtils.encrypt(content.getBytes(), aesKey);
			return Base64Utils.encode(outBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * deBase64=>deAes=>ungz
	 * 
	 * @param srcBase64
	 * @param aesKey
	 * @return
	 */
	public static String AesGzDecode(String srcBase64, byte[] aesKey) {
		try {
			final byte[] inBytes = Base64Utils.decode(srcBase64);
			final byte[] outBytes = AESUtils.decrypt(inBytes, aesKey);
			final byte[] ungzBytes = ungz(outBytes);
			return new String(ungzBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * gz=>Aes=>Base64
	 * 
	 * @param content
	 * @param aesKey
	 * @return
	 */
	public static String AesGzEncode(String content, byte[] aesKey) {
		try {
			final byte[] btsgzed = gz(content.getBytes());
			final byte[] outBytes = AESUtils.encrypt(btsgzed, aesKey);
			return Base64Utils.encode(outBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static byte[] gz(byte[] src) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream zos = new GZIPOutputStream(bos);
		zos.write(src);
		zos.flush();
		zos.close();
		return bos.toByteArray();
	}

	public static byte[] ungz(byte[] src) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(src);
		GZIPInputStream zis = new GZIPInputStream(bis);
		byte[] bts = new byte[1024];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int count;
		while ((count = zis.read(bts)) > 0) {
			bos.write(bts, 0, count);
		}
		return bos.toByteArray();
	}

	public static String gzBase64(byte[] src) {
		try {
			final byte[] btsGzed = gz(src);
			return Base64Utils.encode(btsGzed);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] ungzBase64(String src) {
		try {
			final byte[] bts4gz = Base64Utils.decode(src);
			return ungz(bts4gz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
