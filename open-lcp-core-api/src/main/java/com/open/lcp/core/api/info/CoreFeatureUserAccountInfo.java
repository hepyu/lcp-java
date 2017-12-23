package com.open.lcp.core.api.info;

import com.open.lcp.core.common.enums.Gender;

public interface CoreFeatureUserAccountInfo {

	public Long getUserId();

	public String getUserName();

	public String getNickName();

	public String getAvatar();

	public Gender getGender();

	public Integer getRegistIp();

	public Integer getUpdateIp();

	public Long getRegistTime();

	public Long getUpdateTime();

	public String getMobile();

	public String getDescription();

}
