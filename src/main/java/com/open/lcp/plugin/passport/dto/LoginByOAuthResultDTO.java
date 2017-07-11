package com.open.lcp.plugin.passport.dto;

import com.open.common.enums.Gender;

public class LoginByOAuthResultDTO {

	private int passportCode = 0;

	private Long userId;

	private String userName;

	private String avatar;

	private Gender gender;

	private String t;

	private String userSecretKey;

	private String bindMobile;

	private String description;

	private boolean isNewUser;

	private int registType;

	public int getPassportCode() {
		return passportCode;
	}

	public void setPassportCode(int passportCode) {
		this.passportCode = passportCode;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

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

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public String getUserSecretKey() {
		return userSecretKey;
	}

	public void setUserSecretKey(String userSecretKey) {
		this.userSecretKey = userSecretKey;
	}

	public boolean isSuccess() {
		return this.passportCode == 0;
	}

	public String getBindMobile() {
		return bindMobile;
	}

	public void setBindMobile(String bindMobile) {
		this.bindMobile = bindMobile;
	}

	public boolean isNewUser() {
		return isNewUser;
	}

	public void setNewUser(boolean isNewUser) {
		this.isNewUser = isNewUser;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getRegistType() {
		return registType;
	}

	public void setRegistType(int registType) {
		this.registType = registType;
	}

}
