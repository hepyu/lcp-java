package com.open.passport.storage.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.open.common.util.HttpUtil;
import com.open.env.finder.EnvFinder;
import com.open.passport.UserAccountType;
import com.open.passport.storage.AccountAvatarStorage;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.squareup.okhttp.MediaType;

@Component
public class QiniuStorage implements AccountAvatarStorage {

	private static final Log logger = LogFactory.getLog(QiniuStorage.class);

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	@Autowired
	private QiniuConfig qiniuConfig;

	@Autowired
	private CloseableHttpClient closeableHttpClient;

	private Auth auth() {
		return Auth.create(qiniuConfig.getAccesskey(), qiniuConfig.getSecretkey());
	}

	@Override
	public String getOAuthAvatarKey(long userId, UserAccountType accountType) {
		String key = "avatar_" + userId + "_" + EnvFinder.getProfile().name() + "_" + accountType.name();
		return key;
	}

	@Override
	public String getOAuthAvatarUrl(long userId, UserAccountType accountType) {
		String key = getOAuthAvatarKey(userId, accountType);
		String url = "http://" + qiniuConfig.getQiniu_image_upload_url() + "/" + key;
		return url + "?v=" + System.currentTimeMillis();
	}

	@Override
	public String getUserAvatarUrl(long userId) {
		String key = getUserAvatarKey(userId);
		String url = "http://" + qiniuConfig.getQiniu_image_upload_url() + "/" + key;
		return url + "?v=" + System.currentTimeMillis();
	}

	// @Override
	// public String getUserAvatarKey(long passportUserId) {
	// return getUserAvatarKey(null, passportUserId);
	// }

	@Override
	public String getUserAvatarKey(long userId) {
		String key = "avatar_" + userId + "_" + EnvFinder.getProfile();
		return key;
	}

	@Override
	public String requestUploadToken(String key) {
		Auth auth = auth();
		String bucket = qiniuConfig.getQiniu_image_bucketname();
		StringMap sm = new StringMap();
		sm.put("scope", bucket + ":" + key);
		sm.put("insertOnly", 0);
		String uploadToken = auth.uploadToken(bucket, key, 3600, sm);
		return uploadToken;
	}

	@Override
	public String fetchResouce(String fromUrl, String key) {
		// 获取到 Access Key 和 Secret Key 之后，您可以按照如下方式进行密钥配置
		Auth auth = auth();

		// 获取空间管理器
		BucketManager bucketManager = new BucketManager(auth);
		try {

			// 要求url可公网正常访问BucketManager.fetch(url, bucketName, key);
			// @param url 网络上一个资源文件的URL
			// @param bucketName 空间名称
			// @param key 空间内文件的key[唯一的]
			String bucket = qiniuConfig.getQiniu_image_bucketname();
			bucketManager.fetch(fromUrl, bucket, key);

			String url = "http://" + qiniuConfig.getQiniu_image_upload_url() + "/" + key;
			return url + "?v=" + System.currentTimeMillis();
		} catch (QiniuException e) {
			logger.warn(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public String uploadImg(byte[] bytes, String key) {
		UploadManager uploadManager = new UploadManager();
		Response response = null;
		try {
			Auth auth = auth();
			String bucket = qiniuConfig.getQiniu_image_bucketname();
			StringMap sm = new StringMap();
			sm.put("scope", bucket + ":" + key);
			sm.put("insertOnly", 0);
			String uploadToken = auth.uploadToken(bucket, key, 3600, sm);
			response = uploadManager.put(bytes, key, uploadToken);
			if (response.isOK()) {
				String url = "http://" + qiniuConfig.getQiniu_image_upload_url() + "/" + key;
				return url + "?v=" + System.currentTimeMillis();
				// auth.privateDownloadUrl(url, 3600 * 24 * 365 * 30);
			}
			logger.warn(response.bodyString());
		} catch (QiniuException e) {
			logger.warn(e.getMessage(), e);
		}
		return null;
	}

	class MD5Result {
		private String md5;

		public String getMd5() {
			return md5;
		}

		public void setMd5(String md5) {
			this.md5 = md5;
		}
	}

	@Override
	public String getMd5(String url) {
		try {
			String httpResult = HttpUtil.get(closeableHttpClient, url + "&hash/md5");
			Gson gson = new Gson();
			MD5Result md5Result = gson.fromJson(httpResult, MD5Result.class);

			return md5Result.getMd5();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	class SHA1Result {
		private String sha1;

		public String getSha1() {
			return sha1;
		}

		public void setSha1(String sha1) {
			this.sha1 = sha1;
		}

	}

	@Override
	public String getSHA1(String url) {
		try {
			if (url.indexOf("?") > 0) {
				url = url + "&hash/sha1";
			} else {
				url = url + "?hash/sha1";
			}

			String httpResult = HttpUtil.get(closeableHttpClient, url);

			Gson gson = new Gson();
			SHA1Result sha1Result = gson.fromJson(httpResult, SHA1Result.class);
			return sha1Result.getSha1();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

}
