package com.open.lcp.biz.passport.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.open.lcp.core.api.annotation.LcpHttpMethod;
import com.open.lcp.core.api.annotation.LcpHttpRequest;
import com.open.lcp.core.api.command.CommandContext;
import com.open.lcp.core.api.facade.ApiFacade;
import com.open.lcp.core.feature.user.api.dto.UserDetailInfoDTO;
import com.open.lcp.core.feature.user.api.rpc.UserDubboRPC;

@Component
public class AdminPassportFacade implements ApiFacade {

	@Autowired
	private UserDubboRPC userRPC;

	@LcpHttpMethod(name = "admin.passport.user.getUserDetailInfo", ver = "1.0", desc = "手机号登录", logon = true)
	public UserDetailInfoDTO getUserDetailInfo(
			@LcpHttpRequest(desc = "userId", name = "userId", required = true) Long userId, CommandContext context) {
		return userRPC.getUserDetailInfo(userId);
	}
}
