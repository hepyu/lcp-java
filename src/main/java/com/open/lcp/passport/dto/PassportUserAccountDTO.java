package com.open.lcp.passport.dto;

import com.open.common.enums.Gender;

public class PassportUserAccountDTO {

	private Long userId;

	private String userName;

	private String nickName;

	private String avatar;

	private Gender gender;

	private Integer registIp;

	private Integer updateIp;

	private Long registTime;

	private Long updateTime;

	private String mobile;

	// 个人说明
	private String description;

	protected Long getUserId() {
		return userId;
	}

	protected void setUserId(Long userId) {
		this.userId = userId;
	}

	protected String getUserName() {
		return userName;
	}

	protected void setUserName(String userName) {
		this.userName = userName;
	}

	protected String getNickName() {
		return nickName;
	}

	protected void setNickName(String nickName) {
		this.nickName = nickName;
	}

	protected String getAvatar() {
		return avatar;
	}

	protected void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	protected Gender getGender() {
		return gender;
	}

	protected void setGender(Gender gender) {
		this.gender = gender;
	}

	protected Integer getRegistIp() {
		return registIp;
	}

	protected void setRegistIp(Integer registIp) {
		this.registIp = registIp;
	}

	protected Integer getUpdateIp() {
		return updateIp;
	}

	protected void setUpdateIp(Integer updateIp) {
		this.updateIp = updateIp;
	}

	protected Long getRegistTime() {
		return registTime;
	}

	protected void setRegistTime(Long registTime) {
		this.registTime = registTime;
	}

	protected Long getUpdateTime() {
		return updateTime;
	}

	protected void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	protected String getMobile() {
		return mobile;
	}

	protected void setMobile(String mobile) {
		this.mobile = mobile;
	}

	protected String getDescription() {
		return description;
	}

	protected void setDescription(String description) {
		this.description = description;
	}

}
