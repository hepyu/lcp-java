package com.open.lcp.passport.sdk;

import com.open.common.enums.Gender;
import com.open.common.enums.UserType;

public class PassportAccountPortrait {

	private String username;

	private String nickname;

	private String headIconURL;

	private String oauthHeadIconURL;

	private Gender gender;

	private UserType userType;

	// xunlei level,迅雷SDK独有
	private String xlLevel;

	// xunleiUserId,迅雷SDK独有
	private Long xlUserId;

	// 个人描述
	private String description;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHeadIconURL() {
		return headIconURL;
	}

	public void setHeadIconURL(String headIconURL) {
		this.headIconURL = headIconURL;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public String getXlLevel() {
		return xlLevel;
	}

	public void setXlLevel(String xlLevel) {
		this.xlLevel = xlLevel;
	}

	public Long getXlUserId() {
		return xlUserId;
	}

	public void setXlUserId(Long xlUserId) {
		this.xlUserId = xlUserId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOauthHeadIconURL() {
		return oauthHeadIconURL;
	}

	public void setOauthHeadIconURL(String oauthHeadIconURL) {
		this.oauthHeadIconURL = oauthHeadIconURL;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

}
