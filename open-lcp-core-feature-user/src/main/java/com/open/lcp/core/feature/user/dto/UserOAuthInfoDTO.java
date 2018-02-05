package com.open.lcp.core.feature.user.dto;

import com.open.lcp.core.common.enums.Gender;
import com.open.lcp.core.feature.user.api.UserType;

public class UserOAuthInfoDTO {

	private Long userId;

	private String openId;

	private UserType type;

	private String userName;

	private String nickName;

	private String avatar;

	private Gender gender;

	private int bindIp;

	private int updateIp;

	private Long bindTime;

	private Long updateTime;

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getBindIp() {
		return bindIp;
	}

	public void setBindIp(int bindIp) {
		this.bindIp = bindIp;
	}

	public int getUpdateIp() {
		return updateIp;
	}

	public void setUpdateIp(int updateIp) {
		this.updateIp = updateIp;
	}

	public Long getBindTime() {
		return bindTime;
	}

	public void setBindTime(Long bindTime) {
		this.bindTime = bindTime;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
