package com.open.lcp.passport.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.open.common.enums.Gender;
import com.open.lcp.passport.PassportException;
import com.open.lcp.passport.UserAccountType;
import com.open.lcp.passport.dto.PassportOAuthAccountDTO;
import com.open.lcp.passport.dto.PassportUserAccountDTO;
import com.open.lcp.passport.dto.RequestUploadAvatarResultDTO;
import com.open.lcp.passport.service.AbstractAccount;
import com.open.lcp.passport.service.AccountInfoService;
import com.open.lcp.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.open.lcp.passport.service.dao.entity.PassportUserAccountEntity;
import com.open.lcp.passport.util.AccountUtil;
import com.open.lcp.passport.util.PlaceholderAvatarUtil;

@Service
public class AccountInfoServiceImpl extends AbstractAccount implements AccountInfoService {

	@Override
	public void createAccount(PassportUserAccountEntity passportUserAccountEntity,
			PassportOAuthAccountEntity passportOAuthAccountEntity) {

		if (StringUtils.isEmpty(passportUserAccountEntity.getAvatar())) {
			passportUserAccountEntity.setAvatar(PlaceholderAvatarUtil.getPlaceholderAvatar());
		}

		if (StringUtils.isEmpty(passportOAuthAccountEntity.getAvatar())) {
			passportOAuthAccountEntity.setAvatar(passportUserAccountEntity.getAvatar());
		}

		if (StringUtils.isEmpty(passportUserAccountEntity.getNickName())
				|| StringUtils.isEmpty(passportUserAccountEntity.getUserName())
				|| StringUtils.isEmpty(passportUserAccountEntity.getAvatar())
				|| StringUtils.isEmpty(passportOAuthAccountEntity.getNickName())
				|| StringUtils.isEmpty(passportOAuthAccountEntity.getUserName())
				|| StringUtils.isEmpty(passportOAuthAccountEntity.getAvatar())) {
			throw new PassportException(PassportException.EXCEPTION_OBTAIN_PORTRAIT_FAILED,
					"EXCEPTION_OBTAIN_PORTRAIT_FAILED", null);
		}

		long ts = System.currentTimeMillis();
		passportUserAccountEntity.setLastLoginTime(ts);
		passportOAuthAccountEntity.setLastLoginTime(ts);

		Long createResult = passportUserAccountDAO.create(passportUserAccountEntity);
		if (createResult != null && createResult > 0) {
			passportUserAccountEntity.setUserId(createResult);
			passportOAuthAccountEntity.setUserId(createResult);
			passportOAuthAccountDAO.create(passportOAuthAccountEntity);
			// passportCache.delOAuthAccountInfoByUserIdAndType(userId, type);
			// passportCache.delUserInfoByUserId(userId);
			// passportCache.delUserId(openId, type);
		}
	}

	@Override
	public void bindAccount(PassportOAuthAccountEntity passportOAuthAccountEntity) {
		passportOAuthAccountDAO.create(passportOAuthAccountEntity);
	}

	@Override
	public void login(PassportUserAccountEntity passportUserAccountEntity,
			PassportOAuthAccountEntity passportOAuthAccountEntity) {
		if (passportUserAccountEntity == null || passportUserAccountEntity.getUserId() == null
				|| passportUserAccountEntity.getUserId() <= 0) {
			throw new PassportException(PassportException.EXCEPTION_LOGIN_FAILED, "EXCEPTION_LOGIN_FAILED", null);
		}
		passportUserAccountDAO.login(passportUserAccountEntity);
		passportOAuthAccountDAO.login(passportOAuthAccountEntity);
	}

	@Override
	public PassportUserAccountDTO getUserInfo(Long userId) {

		PassportUserAccountEntity entity = obtainPassportUserAccount(userId);
		PassportUserAccountDTO dto = null;
		if (entity != null) {
			dto = AccountUtil.convertPassportUserAccoutEntity(entity);
		}
		return dto;
	}

	@Override
	public List<PassportOAuthAccountDTO> getOAuthAccountList(Long userId) {
		List<PassportOAuthAccountEntity> list = passportOAuthAccountDAO.getOAuthAccountListByUserId(userId);
		PassportOAuthAccountDTO dto = null;
		List<PassportOAuthAccountDTO> dtolist = new ArrayList<PassportOAuthAccountDTO>();
		for (PassportOAuthAccountEntity entity : list) {
			dto = AccountUtil.convertPassportUserAccoutEntity(entity);
			dtolist.add(dto);
		}
		return dtolist;
	}

	@Override
	public int unbindAccount(Long userId, UserAccountType userAccountType) {
		List<PassportOAuthAccountEntity> list = passportOAuthAccountDAO.getOAuthAccountInfo(userId,
				userAccountType.value());
		if (list == null || list.isEmpty()) {
			return 0;
		}
		PassportOAuthAccountEntity entity = list.get(0);
		if (entity == null || StringUtils.isEmpty(entity.getOpenId())) {
			return 0;
		}
		int result = passportOAuthAccountDAO.unbindOAuthAccount(userId, userAccountType.value());
		if (result > 0) {
			passportCache.delOAuthAccountInfoByUserIdAndType(userId, userAccountType);
			passportCache.delUserId(entity.getOpenId(), userAccountType);
		}
		return result;
	}

	@Override
	public int updateGender(Long userId, Gender gender) {
		return passportUserAccountDAO.updateGender(userId, gender.gender());
	}

	@Override
	public int updateNickName(Long userId, String nickName) {
		return passportUserAccountDAO.updateNickName(userId, nickName);
	}

	@Override
	public int updateDescription(Long userId, String description) {
		return passportUserAccountDAO.updateDescription(userId, description);
	}

	@Override
	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId) {
		String key = accountAvatarStorage.getUserAvatarKey(userId);
		String uploadToken = accountAvatarStorage.requestUploadToken(key);

		RequestUploadAvatarResultDTO result = new RequestUploadAvatarResultDTO();
		result.setKey(key);
		result.setUploadToken(uploadToken);
		return result;
	}

	@Override
	public String commitUploadAvatar(Long userId) {
		String avatarUrl = accountAvatarStorage.getUserAvatarUrl(userId);
		int result = passportUserAccountDAO.updateAvatar(userId, avatarUrl);

		if (result > 0) {
			return avatarUrl;
		} else {
			return null;
		}
	}

	@Override
	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId, UserAccountType accountType) {
		String key = accountAvatarStorage.getOAuthAvatarKey(userId, accountType);
		String uploadToken = accountAvatarStorage.requestUploadToken(key);

		RequestUploadAvatarResultDTO result = new RequestUploadAvatarResultDTO();
		result.setKey(key);
		result.setUploadToken(uploadToken);
		return result;
	}

	@Override
	public String commitUploadAvatar(Long userId, UserAccountType accountType) {
		String avatarUrl = accountAvatarStorage.getOAuthAvatarUrl(userId, accountType);
		int result = passportUserAccountDAO.updateAvatar(userId, avatarUrl);
		if (result > 0) {
			return avatarUrl;
		} else {
			return null;
		}
	}

}
