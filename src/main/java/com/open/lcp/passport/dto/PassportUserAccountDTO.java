package com.open.lcp.passport.dto;

import com.xunlei.mcp.model.UserInfo.Gender;
import com.xunlei.xlmc.passport.UserAccountTypeEnum;
import com.xunlei.xlmc.passport.UserType;

public class PassportUserAccountDTO {

	private Long xlUserId;

	private Long passportUserId;

	private String userName;

	private String nickName;

	private String headIconUrl;

	private Gender gender;

	private Integer registIp;

	private Integer updateIp;

	private Long registTime;

	private Long updateTime;

	private Integer nickNameType;

	private String mobile;

	// 迅雷账号等级
	private String xlLevel;

	// 这个值不太准（因为是后加的），根据接口不同准确度不同，是为了参考。
	private String source;

	// 个人说明
	private String description;

	public Long getXlUserId() {
		return xlUserId;
	}

	public void setXlUserId(Long xlUserId) {
		this.xlUserId = xlUserId;
	}

	public Long getPassportUserId() {
		return passportUserId;
	}

	public void setPassportUserId(Long passportUserId) {
		this.passportUserId = passportUserId;
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

	public String getHeadIconUrl() {
		return headIconUrl;
	}

	public void setHeadIconUrl(String headIconUrl) {
		this.headIconUrl = headIconUrl;
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

	public Integer getNickNameType() {
		return nickNameType;
	}

	public void setNickNameType(Integer nickNameType) {
		this.nickNameType = nickNameType;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getXlLevel() {
		return xlLevel;
	}

	public void setXlLevel(String xlLevel) {
		this.xlLevel = xlLevel;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public UserType getUserType() {
		return UserType.valueOf(UserAccountTypeEnum.valueOf(this
				.getNickNameType()));
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
