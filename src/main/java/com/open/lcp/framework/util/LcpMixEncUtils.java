package com.open.lcp.framework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LcpMixEncUtils {
	/**
	 * 提供给app.init接口用，imei和mac的解密。
	 * 
	 * 先base64 decode，然后用key的md5后的16byte来解密，得到的byte[]直接new String。
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
	 * 提供给app.init接口用，imei和mac的加密
	 * 
	 * @param content
	 *            原文 mac去掉冒号和减号，转成大写
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
