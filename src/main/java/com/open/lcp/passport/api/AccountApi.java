package com.open.lcp.passport.api;

import java.util.List;

import com.open.common.enums.Gender;
import com.open.lcp.framework.security.CheckTicket;
import com.open.lcp.passport.UserAccountType;
import com.open.lcp.passport.dto.PassportOAuthAccountDTO;

public interface AccountApi {

	public CheckTicket validateTicket(String t);

	public boolean checkUserIdentify(String userIdentify);

	public boolean suicide(String t);

	/**
	 * 手雷验票通过返回对象信息，不通过返回null.
	 * 
	 * @param uid
	 * @param sid
	 * @return
	 */
	public Long validateMobileThunderUser(String uid, String sid, String ip);

	public Long validateMobileThunderUserMiss(String uid, String sid, String ip, boolean isMiss);

	public UserInfo getUserInfoByTicket(String t);

	public UserInfo getUserInfoByXlUserId(Long xlUserId);

	public UserInfo getMobileThunderUserWithoutValidation(Long xlUserId);

	public List<PassportOAuthAccountDTO> getOAuthAccountListByXlUserId(Long xlUserId);

	public int unbindAccount(Long xlUserId, UserAccountType userAccountType);

	public boolean updateGender(Long xlUserId, Gender gender);

	public boolean updateNickName(Long xlUserId, String nickName);

	public boolean updateDescription(Long xlUserId, String description);

	public boolean updateNickNameType(Long xlUserId, UserAccountType userAccountType);

	public RequestUploadAvatarResult requestUploadAvatar(String prefix, Long xlUserId);

	public String commitUploadAvatar(String prefix, Long xlUserId);

	public UserAccountTypeEnum getUserType(Long xlUserId);

}
