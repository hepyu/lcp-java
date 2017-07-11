package com.open.lcp.plugin.passport.dto;

import com.open.common.enums.Gender;

public class LoginByMobileResultDTO {

	private int passportCode = 0;

	private int securityCode = 0;

	private Long userId;

	private String userName;

	private String avatar;

	private Gender gender;

	private String t;

	private String userSecretKey;

	private boolean existAccountExceptMobile;

	private boolean needImageCode;

	private String imageCodeUrl;

	private boolean isNewUser;

	private String description;

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
		return this.passportCode == 0 && this.securityCode == 0;
	}

	public boolean isExistAccountExceptMobile() {
		return existAccountExceptMobile;
	}

	public void setExistAccountExceptMobile(boolean existAccountExceptMobile) {
		this.existAccountExceptMobile = existAccountExceptMobile;
	}

	public boolean isNeedImageCode() {
		return needImageCode;
	}

	public void setNeedImageCode(boolean needImageCode) {
		this.needImageCode = needImageCode;
	}

	public String getImageCodeUrl() {
		return imageCodeUrl;
	}

	public void setImageCodeUrl(String imageCodeUrl) {
		this.imageCodeUrl = imageCodeUrl;
	}

	public int getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(int securityCode) {
		this.securityCode = securityCode;
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
