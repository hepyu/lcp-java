package com.open.passport.api.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.open.common.enums.Gender;
import com.open.passport.UserAccountType;
import com.open.passport.api.AbstractAccountApi;
import com.open.passport.api.AccountInfoApi;
import com.open.passport.dto.CheckTicket;
import com.open.passport.dto.PassportUserAccountDTO;
import com.open.passport.dto.RequestUploadAvatarResultDTO;
import com.open.passport.ticket.Ticket;

@Component
public class SimpleAccountInfoApi extends AbstractAccountApi implements AccountInfoApi {

	@Override
	public CheckTicket validateTicket(String t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean suicide(String t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PassportUserAccountDTO getUserInfoByTicket(String t) {
		Ticket ticket = super.checkTicket(t);
		Long userId = ticket.getUserId();
		return obtainPassportUserAccount(userId);
	}

	@Override
	public PassportUserAccountDTO getUserInfoByUserId(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PassportUserAccountDTO> getOAuthAccountListByXlUserId(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int unbindAccount(Long userId, UserAccountType userAccountType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean updateGender(Long userId, Gender gender) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateNickName(Long userId, String nickName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateDescription(Long userId, String description) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateNickNameType(Long userId, UserAccountType userAccountType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RequestUploadAvatarResultDTO requestUploadAvatar(String prefix, Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String commitUploadAvatar(String prefix, Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

}
