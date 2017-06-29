package com.open.lcp.passport.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.open.common.enums.Gender;
import com.open.lcp.passport.UserAccountType;
import com.open.lcp.passport.dto.PassportOAuthAccountDTO;
import com.open.lcp.passport.dto.PassportUserAccountDTO;
import com.open.lcp.passport.dto.RequestUploadAvatarResultDTO;
import com.open.lcp.passport.service.AbstractAccount;
import com.open.lcp.passport.service.AccountInfoService;
import com.open.lcp.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.open.lcp.passport.service.dao.entity.PassportUserAccountEntity;
import com.open.lcp.passport.util.AccountUtil;

@Service
public class AccountInfoServiceImpl extends AbstractAccount implements AccountInfoService {

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
	public RequestUploadAvatarResultDTO requestUploadAvatar(String prefix, Long userId) {
		String key = accountAvatarStorage.getUserAvatarKey(prefix, userId);
		String uploadToken = accountAvatarStorage.requestUploadToken(key);

		RequestUploadAvatarResultDTO result = new RequestUploadAvatarResultDTO();
		result.setKey(key);
		result.setUploadToken(uploadToken);
		return result;
	}

	@Override
	public String commitUploadAvatar(String prefix, Long userId) {
		String avatarUrl = accountAvatarStorage.getUserAvatarUrl(prefix, userId);
		int result = passportUserAccountDAO.updateAvatar(userId, avatarUrl);

		if (result > 0) {
			return avatarUrl;
		} else {
			return null;
		}
	}

	@Override
	public RequestUploadAvatarResultDTO requestUploadAvatar(String prefix, Long userId, UserAccountType accountType) {
		String key = accountAvatarStorage.getOAuthAvatarKey(prefix, userId, accountType);
		String uploadToken = accountAvatarStorage.requestUploadToken(key);

		RequestUploadAvatarResultDTO result = new RequestUploadAvatarResultDTO();
		result.setKey(key);
		result.setUploadToken(uploadToken);
		return result;
	}

	@Override
	public String commitUploadAvatar(String prefix, Long userId, UserAccountType accountType) {
		String avatarUrl = accountAvatarStorage.getOAuthAvatarUrl(prefix, userId, accountType);
		int result = passportUserAccountDAO.updateAvatar(userId, avatarUrl);
		if (result > 0) {
			return avatarUrl;
		} else {
			return null;
		}
	}

	@Override
	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId) {
		return requestUploadAvatar(null, userId);
	}

	@Override
	public String commitUploadAvatar(Long userId) {
		return commitUploadAvatar(null, userId);
	}

	@Override
	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId, UserAccountType accountType) {
		return requestUploadAvatar(null, userId, accountType);
	}

	@Override
	public String commitUploadAvatar(Long userId, UserAccountType accountType) {
		return commitUploadAvatar(null, userId, accountType);
	}

}
