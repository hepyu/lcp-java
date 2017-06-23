package com.open.lcp.passport.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.open.common.enums.Gender;
import com.open.lcp.passport.UserAccountType;
import com.open.lcp.passport.dto.PassportOAuthAccountDTO;
import com.open.lcp.passport.dto.PassportUserAccountDTO;
import com.open.lcp.passport.dto.RequestUploadAvatarResultDTO;
import com.open.lcp.passport.service.AbstractPassportAccount;
import com.open.lcp.passport.service.AccountInfoService;

@Service
public class AccountInfoServiceImpl extends AbstractPassportAccount implements AccountInfoService {

	@Override
	public PassportUserAccountDTO getUserInfo(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PassportOAuthAccountDTO> getOAuthAccountList(Long userId) {
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
	public RequestUploadAvatarResultDTO requestUploadAvatar(String prefix, Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String commitUploadAvatar(String prefix, Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserAccountType getUserType(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

}
