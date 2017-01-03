package com.open.lcp.passport.service.impl.dto;

import java.io.Serializable;
import java.util.Date;

public class UserAccountDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 804313507646558638L;

	private Long id;
	
	private String userId;
	
	private String userName;
	
	private String userType;//分白名单黑名单，暂时只有黑名单
	
	private String status;
	
	private String description;
	
	private Date ctime;
	
	private Date utime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCtime() {
		return ctime;
	}

	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}

	public Date getUtime() {
		return utime;
	}

	public void setUtime(Date utime) {
		this.utime = utime;
	}
	
	
}
