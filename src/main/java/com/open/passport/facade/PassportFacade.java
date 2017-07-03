package com.open.passport.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.open.lcp.framework.core.annotation.LcpMethod;
import com.open.lcp.framework.core.api.command.CommandContext;
import com.open.lcp.framework.core.facade.ApiFacade;
import com.open.lcp.framework.core.facade.CommonResultResp;
import com.open.passport.api.AccountMobileApi;
import com.open.passport.api.AccountOAuthApi;
import com.open.passport.facade.req.LoginMobileReq;
import com.open.passport.facade.req.LoginThirdReq;

@Component
public class PassportFacade implements ApiFacade {

	@Autowired
	private AccountMobileApi accountMobileApi;

	@Autowired
	private AccountOAuthApi accountOAuthApi;

	@LcpMethod(name = "passport.login.third", ver = "1.0", desc = "第三方登录", logon = false)
	public CommonResultResp loginThird(LoginThirdReq req, CommandContext context) {
		accountOAuthApi.login(context.getAppInfo().getAppId(), req.getThirdAppId(), req.getOpenId(),
				req.getAccessToken(), req.getDeviceId(), req.getIp(), req.getAccountType(), req.getUa());
		return CommonResultResp.SUCCESS;
	}

	@LcpMethod(name = "passport.login.mobile", ver = "1.0", desc = "手机号登录", logon = false)
	public CommonResultResp loginMobile(LoginMobileReq req, CommandContext context) {
		accountMobileApi.login(context.getAppInfo().getAppId(), req.getMobile(), req.getMobileCode(), req.getDeviceId(),
				req.getIp(), req.getUa());
		return CommonResultResp.SUCCESS;
	}

}
