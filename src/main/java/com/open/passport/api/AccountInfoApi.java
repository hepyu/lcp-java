package com.open.passport.api;

import java.util.List;

import com.open.common.enums.Gender;
import com.open.passport.UserAccountType;
import com.open.passport.dto.CheckTicket;
import com.open.passport.dto.PassportUserAccountDTO;
import com.open.passport.dto.RequestUploadAvatarResultDTO;

public interface AccountInfoApi {

	public CheckTicket validateTicket(String t);

	public boolean suicide(String t);

	public PassportUserAccountDTO getUserInfoByTicket(String t);

	public PassportUserAccountDTO getUserInfoByUserId(Long userId);

	public List<PassportUserAccountDTO> getOAuthAccountListByXlUserId(Long userId);

	public int unbindAccount(Long userId, UserAccountType userAccountType);

	public boolean updateGender(Long userId, Gender gender);

	public boolean updateNickName(Long userId, String nickName);

	public boolean updateDescription(Long userId, String description);

	public boolean updateNickNameType(Long userId, UserAccountType userAccountType);

	public RequestUploadAvatarResultDTO requestUploadAvatar(String prefix, Long userId);

	public String commitUploadAvatar(String prefix, Long userId);

}
