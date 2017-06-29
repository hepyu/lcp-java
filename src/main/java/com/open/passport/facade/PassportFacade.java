package com.open.passport.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.open.lcp.framework.core.annotation.LcpMethod;
import com.open.lcp.framework.core.api.command.CommandContext;
import com.open.lcp.framework.core.facade.ApiFacade;
import com.open.passport.service.AccountInfoService;

@Component
public class PassportFacade implements ApiFacade {

	@Autowired
	private AccountInfoService accountInfoService;

	@LcpMethod(name = "passport.login.third", ver = "1.0", desc = "第三方登录")
	public String loginThird(CommandContext context) {
		return "helloworld";
	}

	@LcpMethod(name = "passport.login.mobile", ver = "1.0", desc = "手机号登录")
	public String loginMobile(CommandContext context) {
		//
		return null;
	}
}
