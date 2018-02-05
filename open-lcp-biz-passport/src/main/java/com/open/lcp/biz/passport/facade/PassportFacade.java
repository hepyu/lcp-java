package com.open.lcp.biz.passport.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.open.lcp.biz.passport.dto.LoginResultDTO;
import com.open.lcp.biz.passport.facade.req.BindThirdAccountReq;
import com.open.lcp.biz.passport.facade.req.LoginReq;
import com.open.lcp.biz.passport.service.PassportService;
import com.open.lcp.core.api.annotation.LcpHttpMethod;
import com.open.lcp.core.api.command.CommandContext;
import com.open.lcp.core.api.facade.ApiFacade;
import com.open.lcp.core.feature.user.api.dto.BindThirdAccountResultDTO;
import com.open.lcp.core.feature.user.api.dto.UserDetailInfoDTO;
import com.open.lcp.core.feature.user.api.rpc.UserTicketDubboRPC;

@Component
public class PassportFacade implements ApiFacade {

	@Autowired
	private UserTicketDubboRPC userTicketDubboRPC;

	@Autowired
	private PassportService passportService;

	@LcpHttpMethod(name = "passport.user.getUserInfo", ver = "1.0", desc = "手机号登录", logon = true)
	public UserDetailInfoDTO getUserInfo(CommandContext context) {
		return userTicketDubboRPC.getUserDetailInfo(context.getTicket());
	}

	@LcpHttpMethod(name = "passport.user.login", ver = "1.0", desc = "第三方登录", logon = false)
	public LoginResultDTO loginByThird(LoginReq req, CommandContext context) {
		return passportService.login(context.getAppInfo().getAppId(), req.getThirdAppId(), req.getOpenId(),
				req.getAccessToken(), context.getDeviceId(), context.getClientIp(), req.getAccountType(),
				context.getUserAgent(), req.getMobileCode());
	}

	@LcpHttpMethod(name = "passport.user.bind.thirdAccount", ver = "1.0", desc = "绑定第三方账号", logon = true)
	public BindThirdAccountResultDTO bindThirdAccount(BindThirdAccountReq req, CommandContext context) {
		return passportService.bindThirdAccount(context.getAppInfo().getAppId(), req.getThirdAppId(), req.getOpenId(),
				req.getAccessToken(), context.getDeviceId(), context.getTicket(), req.getAccountType(),
				context.getClientIp(), req.getMobileCode());
	}

}
