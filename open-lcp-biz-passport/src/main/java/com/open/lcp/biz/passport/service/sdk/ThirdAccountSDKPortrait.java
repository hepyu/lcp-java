package com.open.lcp.biz.passport.service.sdk;

import com.open.lcp.core.common.enums.Gender;
import com.open.lcp.core.feature.user.api.UserType;

public class ThirdAccountSDKPortrait {

	private String username;

	private String nickname;

	private String avatar;

	private Gender gender;

	private UserType userType;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
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

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

}
