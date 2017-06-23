package com.open.lcp.passport.sdk;

import com.open.common.enums.Gender;
import com.open.common.enums.UserType;

public class ThirdAccountSDKPortrait {

	private String username;

	private String nickname;

	private String avatar;

	private String oauthAvatar;

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

	public String getOauthAvatar() {
		return oauthAvatar;
	}

	public void setOauthAvatar(String oauthAvatar) {
		this.oauthAvatar = oauthAvatar;
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
