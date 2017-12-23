package com.open.lcp.biz.passport.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.open.lcp.biz.passport.dto.PassportOAuthAccountDTO;
import com.open.lcp.biz.passport.service.AccountInfoService;
import com.open.lcp.core.api.annotation.LcpHttpMethod;
import com.open.lcp.core.api.annotation.LcpHttpRequest;
import com.open.lcp.core.api.command.CommandContext;
import com.open.lcp.core.api.facade.ApiFacade;

@Component
public class AdminPassportFacade implements ApiFacade {

	@Autowired
	private AccountInfoService accountInfoService;

	@LcpHttpMethod(name = "admin.passport.user.getUserInfo", ver = "1.0", desc = "手机号登录", logon = true)
	public List<PassportOAuthAccountDTO> getUserInfo(
			@LcpHttpRequest(desc = "userId", name = "userId", required = true) Long userId, CommandContext context) {
		return accountInfoService.getOAuthAccountList(userId);
	}
}
