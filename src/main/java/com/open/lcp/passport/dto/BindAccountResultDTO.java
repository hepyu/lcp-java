package com.open.lcp.passport.dto;

import com.open.common.enums.Gender;

public class BindAccountResultDTO {

	private boolean bindSuccess;

	private String userName;

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

}