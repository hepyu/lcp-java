package com.open.lcp.passport.dto;

public class PassportOAuthAccountDTO {

	private Long xlUserId;

	private String openId;

	private UserAccountTypeEnum type;

	private String userName;

	private String nickName;

	private String headIconUrl;

	private Gender gender;

	private int bindIp;

	private int updateIp;

	private Long bindTime;

	private Long updateTime;

	private int bindType;

	public Long getXlUserId() {
		return xlUserId;
	}

	public void setXlUserId(Long xlUserId) {
		this.xlUserId = xlUserId;
	}

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

	public String getHeadIconUrl() {
		return headIconUrl;
	}

	public void setHeadIconUrl(String headIconUrl) {
		this.headIconUrl = headIconUrl;
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

	public UserAccountTypeEnum getType() {
		return type;
	}

	public void setType(UserAccountTypeEnum type) {
		this.type = type;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public int getBindType() {
		return bindType;
	}

	public void setBindType(int bindType) {
		this.bindType = bindType;
	}

}
