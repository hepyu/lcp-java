package com.open.passport.service;

import java.util.List;

import com.open.common.enums.Gender;
import com.open.passport.UserAccountType;
import com.open.passport.dto.PassportOAuthAccountDTO;
import com.open.passport.dto.PassportUserAccountDTO;
import com.open.passport.dto.RequestUploadAvatarResultDTO;
import com.open.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.open.passport.service.dao.entity.PassportUserAccountEntity;

public interface AccountInfoService {

	public void createAccount(String prefix, PassportUserAccountEntity passportUserAccountEntity,
			PassportOAuthAccountEntity passportOAuthAccountEntity);

	public void updatedAccount(String prefix, PassportUserAccountEntity passportUserAccountEntity,
			PassportOAuthAccountEntity passportOAuthAccountEntity);

	public PassportUserAccountDTO getUserInfo(Long userId);

	public List<PassportOAuthAccountDTO> getOAuthAccountList(Long userId);

	public int unbindAccount(Long userId, UserAccountType userAccountType);

	public int updateGender(Long userId, Gender gender);

	public int updateNickName(Long userId, String nickName);

	public int updateDescription(Long userId, String description);

	public RequestUploadAvatarResultDTO requestUploadAvatar(String prefix, Long userId);

	public String commitUploadAvatar(String prefix, Long userId);

	public RequestUploadAvatarResultDTO requestUploadAvatar(String prefix, Long userId, UserAccountType accountType);

	public String commitUploadAvatar(String prefix, Long userId, UserAccountType accountType);

	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId);

	public String commitUploadAvatar(Long userId);

	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId, UserAccountType accountType);

	public String commitUploadAvatar(Long userId, UserAccountType accountType);

}
