package com.open.lcp.passport.storage.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "qiniuConfig")
public class QiniuConfig {

	@Value("${passport.storage.avatar.qiniu.accesskey}")
	private String accesskey;

	@Value("${passport.storage.avatar.qiniu.secretkey}")
	private String secretkey;

	@Value("${passport.storage.avatar.qiniu.upload.url}")
	private String qiniu_image_upload_url;

	@Value("${passport.storage.avatar.qiniu.bucketname}")
	private String qiniu_image_bucketname;

	public String getAccesskey() {
		return accesskey;
	}

	public void setAccesskey(String accesskey) {
		this.accesskey = accesskey;
	}

	public String getSecretkey() {
		return secretkey;
	}

	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}

	public String getQiniu_image_upload_url() {
		return qiniu_image_upload_url;
	}

	public void setQiniu_image_upload_url(String qiniu_image_upload_url) {
		this.qiniu_image_upload_url = qiniu_image_upload_url;
	}

	public String getQiniu_image_bucketname() {
		return qiniu_image_bucketname;
	}

	public void setQiniu_image_bucketname(String qiniu_image_bucketname) {
		this.qiniu_image_bucketname = qiniu_image_bucketname;
	}

}
