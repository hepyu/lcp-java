package com.open.lcp.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>
 * AES加密解密工具�?
 * </p>
 *
 * @date 2012-5-18
 * @version 1.0
 */
public class AESUtils {

	// public static void main(String[] args) throws Exception {
	// final byte[] key = DigestUtils.md5("bbmCaaacHJnLTydddKrLLUGLddagYRA");
	//
	// String source = "这是�?行测试AES加密/解密的文字，你看完也等于没看，是不是啊？�?";
	// System.err.println("原文:\t" + source);
	// byte[] inputData = source.getBytes();
	// inputData = AESUtils.encrypt(inputData, key);
	// System.err.println("加密�?:\t" + Base64Utils.encode(inputData));
	// byte[] outputData = AESUtils.decrypt(inputData, key);
	// String outputStr = new String(outputData);
	// System.err.println("解密�?:\t" + outputStr);
	//
	// }

	private static final String ALGORITHM = "AES";
	private static final int KEY_SIZE = 128;
	private static final int CACHE_SIZE = 1024;

	/**
	 * <p>
	 * 生成随机密钥
	 * </p>
	 *
	 * @return
	 * @throws Exception
	 */
	public static String getSecretKey() throws Exception {
		return getSecretKey(null);
	}

	/**
	 * <p>
	 * 生成密钥
	 * </p>
	 *
	 * @param seed
	 *            密钥种子
	 * @return
	 * @throws Exception
	 */
	public static String getSecretKey(String seed) throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
		SecureRandom secureRandom;
		if (seed != null && !"".equals(seed)) {
			secureRandom = new SecureRandom(seed.getBytes());
		} else {
			secureRandom = new SecureRandom();
		}
		keyGenerator.init(KEY_SIZE, secureRandom);
		SecretKey secretKey = keyGenerator.generateKey();
		return Base64Utils.encode(secretKey.getEncoded());
	}

	/**
	 * <p>
	 * 加密
	 * </p>
	 *
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		Key k = toKey(key);
		byte[] raw = k.getEncoded();
		SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		return cipher.doFinal(data);
	}

	/**
	 * <p>
	 * 文件加密
	 * </p>
	 *
	 * @param key
	 * @param sourceFilePath
	 * @param destFilePath
	 * @throws Exception
	 */
	public static void encryptFile(byte[] key, String sourceFilePath, String destFilePath) throws Exception {
		File sourceFile = new File(sourceFilePath);
		File destFile = new File(destFilePath);
		if (sourceFile.exists() && sourceFile.isFile()) {
			if (!destFile.getParentFile().exists()) {
				destFile.getParentFile().mkdirs();
			}
			destFile.createNewFile();
			InputStream in = new FileInputStream(sourceFile);
			OutputStream out = new FileOutputStream(destFile);
			Key k = toKey(key);
			byte[] raw = k.getEncoded();
			SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			CipherInputStream cin = new CipherInputStream(in, cipher);
			byte[] cache = new byte[CACHE_SIZE];
			int nRead = 0;
			while ((nRead = cin.read(cache)) != -1) {
				out.write(cache, 0, nRead);
				out.flush();
			}
			out.close();
			cin.close();
			in.close();
		}
	}

	/**
	 * <p>
	 * 解密
	 * </p>
	 *
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		Key k = toKey(key);
		byte[] raw = k.getEncoded();
		SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		return cipher.doFinal(data);
	}

	/**
	 * <p>
	 * 文件解密
	 * </p>
	 *
	 * @param key
	 * @param sourceFilePath
	 * @param destFilePath
	 * @throws Exception
	 */
	public static void decryptFile(byte[] key, String sourceFilePath, String destFilePath) throws Exception {
		File sourceFile = new File(sourceFilePath);
		File destFile = new File(destFilePath);
		if (sourceFile.exists() && sourceFile.isFile()) {
			if (!destFile.getParentFile().exists()) {
				destFile.getParentFile().mkdirs();
			}
			destFile.createNewFile();
			FileInputStream in = new FileInputStream(sourceFile);
			FileOutputStream out = new FileOutputStream(destFile);
			Key k = toKey(key);
			byte[] raw = k.getEncoded();
			SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			CipherOutputStream cout = new CipherOutputStream(out, cipher);
			byte[] cache = new byte[CACHE_SIZE];
			int nRead = 0;
			while ((nRead = in.read(cache)) != -1) {
				cout.write(cache, 0, nRead);
				cout.flush();
			}
			cout.close();
			out.close();
			in.close();
		}
	}

	/**
	 * <p>
	 * 转换密钥
	 * </p>
	 *
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static Key toKey(byte[] key) throws Exception {
		SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
		return secretKey;
	}

}