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

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Integer getRegistIp() {
		return registIp;
	}

	public void setRegistIp(Integer registIp) {
		this.registIp = registIp;
	}

	public Integer getUpdateIp() {
		return updateIp;
	}

	public void setUpdateIp(Integer updateIp) {
		this.updateIp = updateIp;
	}

	public Long getRegistTime() {
		return registTime;
	}

	public void setRegistTime(Long registTime) {
		this.registTime = registTime;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
