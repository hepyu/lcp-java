package com.open.lcp.biz.passport.api;

import java.util.List;

import com.open.lcp.biz.passport.UserAccountType;
import com.open.lcp.biz.passport.dto.UserAccountTicket;
import com.open.lcp.biz.passport.dto.PassportOAuthAccountDTO;
import com.open.lcp.biz.passport.dto.RequestUploadAvatarResultDTO;
import com.open.lcp.common.enums.Gender;
import com.open.lcp.core.base.info.BaseUserAccountInfo;

public interface AccountInfoApi {

	public UserAccountTicket validateTicket(String t);

	public boolean suicide(String t);

	public BaseUserAccountInfo getUserInfoByTicket(String t);

	public BaseUserAccountInfo getUserInfoByUserId(Long userId);

	public List<PassportOAuthAccountDTO> getOAuthAccountList(Long userId);

	public int unbindAccount(Long userId, UserAccountType userAccountType);

	public boolean updateGender(Long userId, Gender gender);

	public boolean updateNickName(Long userId, String nickName);

	public boolean updateDescription(Long userId, String description);

	public RequestUploadAvatarResultDTO requestUploadAvatar(Long userId);

	public String commitUploadAvatar(Long userId);

}
