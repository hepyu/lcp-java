package com.open.lcp.framework.core.dao.entity;

import java.io.Serializable;

import com.open.lcp.framework.core.consts.Gender;

public class UserInfoEntity implements Serializable {

	private static final long serialVersionUID = -3516154462903428228L;
	private long id;
	private String name, nickName, portrait;
	private Gender gender;

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

}
