package com.open.lcp.biz.passport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PassportConfig {

	@Value("${passport.mobileCodeUrl}")
	private String mobileCodeUrl;

	public String getMobileCodeUrl() {
		return mobileCodeUrl;
	}

	public void setMobileCodeUrl(String mobileCodeUrl) {
		this.mobileCodeUrl = mobileCodeUrl;
	}

}
