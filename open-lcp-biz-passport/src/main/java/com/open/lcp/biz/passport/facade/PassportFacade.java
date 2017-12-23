package com.open.lcp.biz.passport.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.open.lcp.biz.passport.UserAccountType;
import com.open.lcp.biz.passport.dto.BindAccountResultDTO;
import com.open.lcp.biz.passport.dto.LoginByMobileResultDTO;
import com.open.lcp.biz.passport.dto.LoginByOAuthResultDTO;
import com.open.lcp.biz.passport.dto.PassportUserAccountDTO;
import com.open.lcp.biz.passport.dto.RequestUploadAvatarResultDTO;
import com.open.lcp.biz.passport.facade.req.BindMobileAccountReq;
import com.open.lcp.biz.passport.facade.req.BindThirdAccountReq;
import com.open.lcp.biz.passport.facade.req.LoginMobileAccountReq;
import com.open.lcp.biz.passport.facade.req.LoginThirdAccountReq;
import com.open.lcp.biz.passport.service.AccountInfoService;
import com.open.lcp.biz.passport.service.AccountTicketService;
import com.open.lcp.core.api.annotation.LcpHttpMethod;
import com.open.lcp.core.api.command.CommandContext;
import com.open.lcp.core.api.facade.ApiFacade;
import com.open.lcp.core.common.enums.Gender;
import com.open.lcp.core.framework.facade.CommonResultResp;

@Component
public class PassportFacade implements ApiFacade {

	@Autowired
	private AccountInfoService accountInfoService;

	@Autowired
	private AccountTicketService accountTicketService;

	@LcpHttpMethod(name = "passport.login.byThirdAccount", ver = "1.0", desc = "第三方登录", logon = false)
	public LoginByOAuthResultDTO loginByThird(LoginThirdAccountReq req, CommandContext context) {
		return accountInfoService.loginByThirdAccount(context.getAppInfo().getAppId(), req.getThirdAppId(),
				req.getOpenId(), req.getAccessToken(), context.getDeviceId(), context.getClientIp(),
				req.getAccountType(), context.getUserAgent());
	}

	@LcpHttpMethod(name = "passport.login.byMobileAccount", ver = "1.0", desc = "手机号登录", logon = false)
	public LoginByMobileResultDTO loginByMobile(LoginMobileAccountReq req, CommandContext context) {
		return accountInfoService.loginByMobileAccount(context.getAppInfo().getAppId(), req.getMobile(),
				req.getMobileCode(), context.getDeviceId(), context.getClientIp(), context.getUserAgent());
	}

	@LcpHttpMethod(name = "passport.user.getUserInfo", ver = "1.0", desc = "手机号登录", logon = true)
	public PassportUserAccountDTO getUserInfo(CommandContext context) {
		return accountTicketService.getUserInfo(context.getTicket());
	}

	// @LcpHttpMethod(name = "passport.user.unbind", ver = "1.0", desc = "解綁",
	// logon = true)
	// public int unbindAccount(Long userId, UserAccountType userAccountType,
	// CommandContext context) {
	// return accountInfoService.unbindAccount(userId, userAccountType);
	// }

	@LcpHttpMethod(name = "passport.user.bindMobileAccount", ver = "1.0", desc = "绑定手机", logon = true)
	public CommonResultResp bindMobileAccount(BindMobileAccountReq req, CommandContext context) {
		boolean result = accountInfoService.bindMobileAccount(context.getAppInfo().getAppId(), req.getMobile(),
				req.getMobileCode(), context.getDeviceId(), context.getTicket(), context.getClientIp());
		return result ? CommonResultResp.SUCCESS : CommonResultResp.FAILED;
	}

	@LcpHttpMethod(name = "passport.user.bindThirdAccount", ver = "1.0", desc = "绑定第三方账号", logon = true)
	public BindAccountResultDTO bindThirdAccount(BindThirdAccountReq req, CommandContext context) {
		return accountInfoService.bindThirdAccount(context.getAppInfo().getAppId(), req.getThirdAppId(),
				req.getOpenId(), req.getAccessToken(), context.getDeviceId(), context.getTicket(), req.getAccountType(),
				context.getClientIp());
	}

	// @LcpHttpMethod(name = "passport.user.updateGender", ver = "1.0", desc =
	// "更新性別", logon = true)
	// public CommonResultResp updateGender(Long userId, Gender gender) {
	// int result = accountInfoService.updateGender(userId, gender);
	// return result > 0 ? CommonResultResp.SUCCESS : CommonResultResp.FAILED;
	// }
	//
	// @LcpHttpMethod(name = "passport.user.updateNickName", ver = "1.0", desc =
	// "更新昵称", logon = true)
	// public CommonResultResp updateNickName(Long userId, String nickName) {
	// int result = accountInfoService.updateNickName(userId, nickName);
	// return result > 0 ? CommonResultResp.SUCCESS : CommonResultResp.FAILED;
	// }
	//
	// @LcpHttpMethod(name = "passport.user.updateDescription", ver = "1.0",
	// desc = "更新昵称", logon = true)
	// public CommonResultResp updateDescription(Long userId, String
	// description) {
	// int result = accountInfoService.updateDescription(userId, description);
	// return result > 0 ? CommonResultResp.SUCCESS : CommonResultResp.FAILED;
	// }
	//
	// @LcpHttpMethod(name = "passport.user.requestUploadAvatar", ver = "1.0",
	// desc = "获取上传头像的token", logon = true)
	// public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId) {
	// return accountInfoService.requestUploadAvatar(userId);
	// }
	//
	// @LcpHttpMethod(name = "passport.user.commitUploadAvatar", ver = "1.0",
	// desc = "提交头像URL", logon = true)
	// public CommonResultResp commitUploadAvatar(Long userId) {
	// String result = accountInfoService.commitUploadAvatar(userId);
	// return result == null ? CommonResultResp.FAILED :
	// CommonResultResp.SUCCESS;
	// }
	//
	// @LcpHttpMethod(name = "passport.user.getUserType", ver = "1.0", desc =
	// "获取用户类型", logon = true)
	// public String getUserType(Long userId) {
	// return accountInfoService.getUserType(userId);
	// }

}
