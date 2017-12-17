package com.open.lcp.biz.passport.api.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.open.lcp.biz.passport.UserAccountType;
import com.open.lcp.biz.passport.api.AbstractAccountApi;
import com.open.lcp.biz.passport.api.AccountInfoApi;
import com.open.lcp.biz.passport.dto.UserAccountTicket;
import com.open.lcp.biz.passport.dto.PassportOAuthAccountDTO;
import com.open.lcp.biz.passport.dto.PassportUserAccountDTO;
import com.open.lcp.biz.passport.dto.RequestUploadAvatarResultDTO;
import com.open.lcp.biz.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.open.lcp.biz.passport.service.dao.entity.PassportUserAccountEntity;
import com.open.lcp.biz.passport.ticket.Ticket;
import com.open.lcp.core.common.enums.Gender;
import com.open.lcp.core.api.info.BaseUserAccountInfo;

@Component
public class SimpleAccountInfoApi extends AbstractAccountApi implements AccountInfoApi {

	@Override
	public UserAccountTicket validateTicket(String t) {
		Ticket couple = super.checkTicket(t);

		UserAccountTicket dto = new UserAccountTicket();
		dto.setUserSecretKey(couple.getUserSecretKey());
		dto.setUserId(couple.getUserId());
		return dto;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean suicide(String t) {
		Ticket couple = super.checkTicket(t);
		if (couple.getUserId() > 0) {
			Long xlUserId = couple.getUserId();

			List<PassportOAuthAccountEntity> oauthAccountList = passportOAuthAccountDAO
					.getOAuthAccountListByUserId(xlUserId);
			if (oauthAccountList != null) {
				for (PassportOAuthAccountEntity oauthAccount : oauthAccountList) {
					String openId = oauthAccount.getOpenId() + "";
					UserAccountType accountType = oauthAccount.getUserAccountType();

					passportCache.delUserId(openId, accountType);
					passportCache.delOAuthAccountInfoByUserIdAndType(xlUserId, accountType);
				}
			}

			passportCache.delUserInfoByUserId(xlUserId);

			PassportUserAccountEntity userAccount = passportUserAccountDAO.getUserInfoByUserId(xlUserId);
			if (userAccount != null) {
				passportCache.delUserInfoByUserId(xlUserId);
				passportOAuthAccountDAO.delPassportOAuthAccountByUserId(xlUserId);
				passportUserAccountDAO.delPassportUserAccountByUserId(xlUserId);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public BaseUserAccountInfo getUserInfoByTicket(String t) {
		Ticket ticket = super.checkTicket(t);
		Long userId = ticket.getUserId();
		return obtainPassportUserAccount(userId);
	}

	@Override
	public BaseUserAccountInfo getUserInfoByUserId(Long userId) {

		if (userId == null || userId <= 0) {
			return null;
		}

		return obtainPassportUserAccount(userId);
	}

	@Override
	public List<PassportOAuthAccountDTO> getOAuthAccountList(Long userId) {
		return accountInfoService.getOAuthAccountList(userId);
	}

	@Override
	public int unbindAccount(Long userId, UserAccountType userAccountType) {
		return accountInfoService.unbindAccount(userId, userAccountType);
	}

	@Override
	public boolean updateGender(Long userId, Gender gender) {
		return accountInfoService.updateGender(userId, gender) > 0;
	}

	@Override
	public boolean updateNickName(Long userId, String nickName) {
		return accountInfoService.updateNickName(userId, nickName) > 0;
	}

	@Override
	public boolean updateDescription(Long userId, String description) {
		return accountInfoService.updateDescription(userId, description) > 0;
	}

	@Override
	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId) {
		return accountInfoService.requestUploadAvatar(userId);
	}

	@Override
	public String commitUploadAvatar(Long userId) {
		return accountInfoService.commitUploadAvatar(userId);
	}

	@Override
	public String getUserType(Long userId) {
		return accountInfoService.getUserType(userId);
	}

}
