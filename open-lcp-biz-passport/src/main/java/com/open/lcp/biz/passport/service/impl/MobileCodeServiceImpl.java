package com.open.lcp.biz.passport.service.impl;

import org.springframework.stereotype.Service;

import com.open.lcp.biz.passport.PassportException;
import com.open.lcp.biz.passport.dto.ObtainMobileCodeResultDTO;
import com.open.lcp.biz.passport.service.AbstractAccountService;
import com.open.lcp.biz.passport.service.MobileCodeService;
import com.open.lcp.core.env.finder.EnvEnum;
import com.open.lcp.core.env.finder.EnvFinder;
import com.open.lcp.core.feature.user.api.MobileCodeType;

@Service
public class MobileCodeServiceImpl extends AbstractAccountService implements MobileCodeService{
	
	@Override
	public ObtainMobileCodeResultDTO obtainMobileCode(String ip, String deviceId, int appId, String mobile,
			MobileCodeType type) {
		EnvEnum env = EnvFinder.getProfile();
		String validateCode = null;

		if (env == EnvEnum.dev) {
			validateCode = "123456";
		} else if (env == EnvEnum.test) {
			validateCode = "123456";
		} else if (env == EnvEnum.pre || env == EnvEnum.product) {
			// TODO
			validateCode = "123456";
		} else {
			throw new PassportException(PassportException.EXCEPTION_SEND_MOBILE_CODE_FAILED, "invalid env.", null);
		}

		String msg = validateCode + "（动态验证码），请在30分钟内填写【LCP】";
		ObtainMobileCodeResultDTO dto = new ObtainMobileCodeResultDTO();
		dto.setMsg(msg);
		return dto;
	}
}
