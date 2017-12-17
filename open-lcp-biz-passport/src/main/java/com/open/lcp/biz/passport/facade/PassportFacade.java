package com.open.lcp.biz.passport.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.open.lcp.biz.passport.api.AccountInfoApi;
import com.open.lcp.biz.passport.api.AccountMobileApi;
import com.open.lcp.biz.passport.api.AccountOAuthApi;
import com.open.lcp.biz.passport.dto.LoginByMobileResultDTO;
import com.open.lcp.biz.passport.dto.LoginByOAuthResultDTO;
import com.open.lcp.biz.passport.dto.PassportUserAccountDTO;
import com.open.lcp.biz.passport.facade.req.LoginMobileReq;
import com.open.lcp.biz.passport.facade.req.LoginThirdReq;
import com.open.lcp.core.api.annotation.LcpHttpMethod;
import com.open.lcp.core.api.command.CommandContext;
import com.open.lcp.core.api.facade.ApiFacade;

@Component
public class PassportFacade implements ApiFacade {

	@Autowired
	private AccountMobileApi accountMobileApi;

	@Autowired
	private AccountOAuthApi accountOAuthApi;

	@Autowired
	private AccountInfoApi accountInfoApi;

	@LcpHttpMethod(name = "passport.login.third", ver = "1.0", desc = "第三方登录", logon = false)
	public LoginByOAuthResultDTO loginThird(LoginThirdReq req, CommandContext context) {
		return accountOAuthApi.login(context.getAppInfo().getAppId(), req.getThirdAppId(), req.getOpenId(),
				req.getAccessToken(), req.getDeviceId(), req.getIp(), req.getAccountType(), req.getUa());
	}

	@LcpHttpMethod(name = "passport.login.mobile", ver = "1.0", desc = "手机号登录", logon = false)
	public LoginByMobileResultDTO loginMobile(LoginMobileReq req, CommandContext context) {
		return accountMobileApi.login(context.getAppInfo().getAppId(), req.getMobile(), req.getMobileCode(),
				req.getDeviceId(), req.getIp(), req.getUa());
	}

	@LcpHttpMethod(name = "passport.user.get", ver = "1.0", desc = "手机号登录", logon = true)
	public PassportUserAccountDTO getUser(CommandContext context) {
		return (PassportUserAccountDTO) accountInfoApi.getUserInfoByTicket(context.getTicket());
	}

}
