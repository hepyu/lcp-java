package com.open.lcp.passport.dto;

public class ObtainMobileCodeDTO {

	private int passportCode = 0;

	private int securityCode = 0;

	private String msg;

	private boolean needImageCode;

	private String imageCodeUrl;

	public boolean isSuccess() {
		return this.passportCode == 0 && this.securityCode == 0;
	}

	public int getPassportCode() {
		return passportCode;
	}

	public void setPassportCode(int passportCode) {
		this.passportCode = passportCode;
	}

	public int getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(int securityCode) {
		this.securityCode = securityCode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isNeedImageCode() {
		return needImageCode;
	}

	public void setNeedImageCode(boolean needImageCode) {
		this.needImageCode = needImageCode;
	}

	public String getImageCodeUrl() {
		return imageCodeUrl;
	}

	public void setImageCodeUrl(String imageCodeUrl) {
		this.imageCodeUrl = imageCodeUrl;
	}

}
