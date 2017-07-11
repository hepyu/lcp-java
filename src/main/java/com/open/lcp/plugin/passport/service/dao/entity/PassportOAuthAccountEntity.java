package com.open.lcp.plugin.passport.service.dao.entity;

import com.open.lcp.plugin.passport.UserAccountType;

public class PassportOAuthAccountEntity {

	private Long userId;

	private String openId;

	private int type;

	private String userName;

	private String nickName;

	private String avatar;

	private int gender;

	private int bindIp;

	private int updateIp;

	private Long bindTime;

	private Long updateTime;

	private Long lastLoginTime;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public int getType() {
		return type;
	}

	public UserAccountType getUserAccountType() {
		return UserAccountType.valueOf(this.getType());
	}

	public void setType(int type) {
		this.type = type;
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

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
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

	public Long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

}
