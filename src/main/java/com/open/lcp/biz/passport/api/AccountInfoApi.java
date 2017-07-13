package com.open.lcp.biz.passport.api;

import java.util.List;

import com.open.lcp.biz.passport.UserAccountType;
import com.open.lcp.biz.passport.dto.CheckTicket;
import com.open.lcp.biz.passport.dto.PassportOAuthAccountDTO;
import com.open.lcp.biz.passport.dto.PassportUserAccountDTO;
import com.open.lcp.biz.passport.dto.RequestUploadAvatarResultDTO;
import com.open.lcp.common.enums.Gender;

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
