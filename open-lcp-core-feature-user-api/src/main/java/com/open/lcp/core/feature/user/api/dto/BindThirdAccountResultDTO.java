package com.open.lcp.core.feature.user.api.dto;

import com.open.lcp.core.common.enums.Gender;

public class BindThirdAccountResultDTO {

	private boolean bindSuccess;

	private String userName;
	
	private String nickName;

	private String avatar;

	private Gender gender;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public boolean isBindSuccess() {
		return bindSuccess;
	}

	public void setBindSuccess(boolean bindSuccess) {
		this.bindSuccess = bindSuccess;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
}