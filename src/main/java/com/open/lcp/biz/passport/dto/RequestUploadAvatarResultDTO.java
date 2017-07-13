package com.open.lcp.biz.passport.dto;

public class RequestUploadAvatarResultDTO {

	private String uploadToken;
	
	private String key;

	public String getUploadToken() {
		return uploadToken;
	}

	public void setUploadToken(String uploadToken) {
		this.uploadToken = uploadToken;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
}
