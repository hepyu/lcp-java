package com.open.lcp.plugin.passport.api;

import java.util.List;

import com.open.common.enums.Gender;
import com.open.lcp.plugin.passport.UserAccountType;
import com.open.lcp.plugin.passport.dto.CheckTicket;
import com.open.lcp.plugin.passport.dto.PassportOAuthAccountDTO;
import com.open.lcp.plugin.passport.dto.PassportUserAccountDTO;
import com.open.lcp.plugin.passport.dto.RequestUploadAvatarResultDTO;

public interface AccountInfoApi {

	public CheckTicket validateTicket(String t);

	public boolean suicide(String t);

	public PassportUserAccountDTO getUserInfoByTicket(String t);

	public PassportUserAccountDTO getUserInfoByUserId(Long userId);

	public List<PassportOAuthAccountDTO> getOAuthAccountList(Long userId);

	public int unbindAccount(Long userId, UserAccountType userAccountType);

	public boolean updateGender(Long userId, Gender gender);

	public boolean updateNickName(Long userId, String nickName);

	public boolean updateDescription(Long userId, String description);

	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId);

	public String commitUploadAvatar(Long userId);

}
