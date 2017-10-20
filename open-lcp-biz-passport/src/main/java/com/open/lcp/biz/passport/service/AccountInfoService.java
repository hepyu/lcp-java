package com.open.lcp.biz.passport.service;

import java.util.List;

import com.open.lcp.biz.passport.UserAccountType;
import com.open.lcp.biz.passport.dto.PassportOAuthAccountDTO;
import com.open.lcp.biz.passport.dto.RequestUploadAvatarResultDTO;
import com.open.lcp.biz.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.open.lcp.biz.passport.service.dao.entity.PassportUserAccountEntity;
import com.open.lcp.common.enums.Gender;
import com.open.lcp.core.base.service.BaseUserAccountInfoService;

public interface AccountInfoService extends BaseUserAccountInfoService {

	public void createAccount(PassportUserAccountEntity passportUserAccountEntity,
			PassportOAuthAccountEntity passportOAuthAccountEntity);

	public void bindAccount(PassportOAuthAccountEntity passportOAuthAccountEntity);

	public void login(PassportUserAccountEntity passportUserAccountEntity,
			PassportOAuthAccountEntity passportOAuthAccountEntity);

	public List<PassportOAuthAccountDTO> getOAuthAccountList(Long userId);

	public int unbindAccount(Long userId, UserAccountType userAccountType);

	public int updateGender(Long userId, Gender gender);

	public int updateNickName(Long userId, String nickName);

	public int updateDescription(Long userId, String description);

	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId);

	public String commitUploadAvatar(Long userId);

	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId, UserAccountType accountType);

	public String commitUploadAvatar(Long userId, UserAccountType accountType);

	public String getUserType(Long userId);

}
