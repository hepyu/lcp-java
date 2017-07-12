package com.open.lcp.passport.service;

import java.util.List;

import com.open.common.enums.Gender;
import com.open.lcp.passport.UserAccountType;
import com.open.lcp.passport.dto.PassportOAuthAccountDTO;
import com.open.lcp.passport.dto.PassportUserAccountDTO;
import com.open.lcp.passport.dto.RequestUploadAvatarResultDTO;
import com.open.lcp.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.open.lcp.passport.service.dao.entity.PassportUserAccountEntity;

public interface AccountInfoService {

	public void createAccount(PassportUserAccountEntity passportUserAccountEntity,
			PassportOAuthAccountEntity passportOAuthAccountEntity);

	public void bindAccount(PassportOAuthAccountEntity passportOAuthAccountEntity);

	public void login(PassportUserAccountEntity passportUserAccountEntity,
			PassportOAuthAccountEntity passportOAuthAccountEntity);

	public PassportUserAccountDTO getUserInfo(Long userId);

	public List<PassportOAuthAccountDTO> getOAuthAccountList(Long userId);

	public int unbindAccount(Long userId, UserAccountType userAccountType);

	public int updateGender(Long userId, Gender gender);

	public int updateNickName(Long userId, String nickName);

	public int updateDescription(Long userId, String description);

	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId);

	public String commitUploadAvatar(Long userId);

	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId, UserAccountType accountType);

	public String commitUploadAvatar(Long userId, UserAccountType accountType);

}
