package com.open.lcp.core.api.info.impl;

import java.io.Serializable;

import com.open.lcp.core.api.info.UserInfo;

public class UserInfoImpl implements UserInfo, Serializable {

	private static final long serialVersionUID = -3516154462903428228L;
	private final Object user;
	private long id;
	private String name, nickName, portrait;
	private Gender gender;

	UserInfoImpl(Object user) {
		this.user = user;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> U getExt() {
		return (U) user;
	}

}
