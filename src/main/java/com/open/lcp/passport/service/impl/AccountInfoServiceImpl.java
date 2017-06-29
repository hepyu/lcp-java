package com.open.lcp.passport.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.open.common.enums.Gender;
import com.open.lcp.passport.UserAccountType;
import com.open.lcp.passport.cache.PassportCache;
import com.open.lcp.passport.dto.PassportOAuthAccountDTO;
import com.open.lcp.passport.dto.PassportUserAccountDTO;
import com.open.lcp.passport.dto.RequestUploadAvatarResultDTO;
import com.open.lcp.passport.service.AbstractPassportAccount;
import com.open.lcp.passport.service.AccountInfoService;
import com.open.lcp.passport.service.dao.PassportOAuthAccountDao;
import com.open.lcp.passport.service.dao.PassportUserAccountDao;
import com.open.lcp.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.open.lcp.passport.service.dao.entity.PassportUserAccountEntity;

@Service
public class AccountInfoServiceImpl extends AbstractPassportAccount implements AccountInfoService {

	@Autowired
	private PassportCache passportCache;

	@Autowired
	private PassportUserAccountDao passportUserAccountDao;

	@Autowired
	private PassportOAuthAccountDao passportOAuthAccountDao;

	@Override
	public PassportUserAccountDTO getUserInfo(Long userId) {
		if (userId == null || userId <= 0) {
			return null;
		}

		PassportUserAccountEntity userAccount = passportCache.getUserInfoByUserId(userId);
		if (userAccount == null) {
			userAccount = passportUserAccountDao.getUserInfoByUserId(userId);

			if (userAccount != null) {
				passportCache.setUserInfoByUserId(userId, userAccount);
			}
		}

		PassportUserAccountDTO dto = null;
		if (userAccount != null) {
			dto = new PassportUserAccountDTO();
			dto.setAvatar(userAccount.getAvatar());
			dto.setDescription(userAccount.getDescription());
			dto.setGender(Gender.get(userAccount.getGender()));
			// dto.setMobile();
			dto.setNickName(userAccount.getNickName());
			dto.setRegistIp(userAccount.getRegistIp());
			dto.setRegistTime(userAccount.getRegistTime());
			dto.setUpdateIp(userAccount.getUpdateIp());
			dto.setUpdateTime(userAccount.getUpdateTime());
			dto.setUserId(userAccount.getUserId());
			dto.setUserName(userAccount.getUserName());
		}
		return dto;
	}

	@Override
	public List<PassportOAuthAccountDTO> getOAuthAccountList(Long userId) {
		List<PassportOAuthAccountEntity> list = passportOAuthAccountDao.getOAuthAccountListByUserId(userId);
		
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

}
