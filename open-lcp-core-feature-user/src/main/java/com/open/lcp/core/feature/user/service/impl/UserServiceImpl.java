package com.open.lcp.core.feature.user.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.open.lcp.core.common.enums.Gender;
import com.open.lcp.core.common.util.IPUtil;
import com.open.lcp.core.feature.user.UserException;
import com.open.lcp.core.feature.user.api.UserCategory;
import com.open.lcp.core.feature.user.api.UserType;
import com.open.lcp.core.feature.user.api.dto.BindThirdAccountResultDTO;
import com.open.lcp.core.feature.user.api.dto.UserDetailInfoDTO;
import com.open.lcp.core.feature.user.api.dto.UserTicketDTO;
import com.open.lcp.core.feature.user.dto.UserOAuthInfoDTO;
import com.open.lcp.core.feature.user.service.AbstractUserService;
import com.open.lcp.core.feature.user.service.UserService;
import com.open.lcp.core.feature.user.service.dao.entity.UserDetailInfoEntity;
import com.open.lcp.core.feature.user.service.dao.entity.UserOAuthInfoEntity;
import com.open.lcp.core.feature.user.ticket.Ticket;
import com.open.lcp.core.feature.user.util.UserAvatarPlaceholderUtil;
import com.open.lcp.core.feature.user.util.UserUtil;

//TODO 可以优化
@Service
public class UserServiceImpl extends AbstractUserService implements UserService {

	private final Log logger = LogFactory.getLog(UserServiceImpl.class);

	// ************************(1).user-read**************************//

	@Override
	public UserDetailInfoDTO getUserDetailInfo(Long userId) {
		UserDetailInfoEntity entity = userDetailInfoDAO.getUserInfo(userId);
		UserDetailInfoDTO dto = null;
		if (entity != null) {
			dto = UserUtil.convertToUserDetailInfoDTO(entity);
		}
		return dto;
	}

	@Override
	public List<UserOAuthInfoDTO> getUserOAuthInfoList(Long userId) {
		List<UserOAuthInfoEntity> list = userOAuthInfoDAO.getOAuthAccountList(userId);
		UserOAuthInfoDTO dto = null;
		List<UserOAuthInfoDTO> dtolist = new ArrayList<UserOAuthInfoDTO>();
		for (UserOAuthInfoEntity entity : list) {
			dto = UserUtil.convertToUserOAuthInfoDTO(entity);
			dtolist.add(dto);
		}
		return dtolist;
	}

	@Override
	public String getUserCategory(Long userId) {
		UserDetailInfoEntity entity = userDetailInfoDAO.getUserInfo(userId);
		if (entity != null) {
			return entity.getUserCategory();
		} else {
			return UserCategory.ACCOUNT_CATEGORY_NO_EXIST;
		}
	}

	@Override
	public Long getUserId(String openId, UserType userType) {
		return userOAuthInfoDAO.getUserId(openId, userType.type());
	}

	// ************************(2).user-write**************************//

	@Override
	public int unbindUserOAuthInfo(Long userId, UserType userAccountType) {
		List<UserOAuthInfoEntity> list = userOAuthInfoDAO.getOAuthAccountInfo(userId, userAccountType.type());
		if (list == null || list.isEmpty()) {
			return 0;
		}
		UserOAuthInfoEntity entity = list.get(0);
		if (entity == null || StringUtils.isEmpty(entity.getOpenId())) {
			return 0;
		}
		int result = userOAuthInfoDAO.unbindOAuthAccount(userId, userAccountType.type());
		if (result > 0) {
			userCache.delOAuthAccountInfoByUserIdAndType(userId, userAccountType);
			userCache.delUserId(entity.getOpenId(), userAccountType);
		}
		return result;
	}

	@Override
	public int updateGender(Long userId, Gender gender) {
		return userDetailInfoDAO.updateGender(userId, gender.gender());
	}

	@Override
	public int updateNickName(Long userId, String nickName) {
		return userDetailInfoDAO.updateNickName(userId, nickName);
	}

	@Override
	public int updateDescription(Long userId, String description) {
		return userDetailInfoDAO.updateDescription(userId, description);
	}

	@Override
	public int updateAvatar(Long userId, String avatar) {
		return userDetailInfoDAO.updateAvatar(userId, avatar);
	}

	@Transactional
	@Override
	public BindThirdAccountResultDTO bindThirdAccount(int appId, String oauthAppId, String openId, String deviceId,
			String t, UserType accountType, String ip, String avatar, String nickName, String userName, Gender gender) {

		Ticket ticket = super.checkTicket(t);

		long now = System.currentTimeMillis();

		UserOAuthInfoEntity userOAuthInfoEntity = new UserOAuthInfoEntity();
		userOAuthInfoEntity.setBindIp(IPUtil.Ip2Int(ip));
		userOAuthInfoEntity.setBindTime(now);
		userOAuthInfoEntity.setAvatar(avatar);
		userOAuthInfoEntity.setNickName(nickName);
		userOAuthInfoEntity.setOpenId(openId);
		userOAuthInfoEntity.setGender(gender.gender());
		userOAuthInfoEntity.setType(accountType.type());
		userOAuthInfoEntity.setUpdateIp(IPUtil.Ip2Int(ip));
		userOAuthInfoEntity.setUpdateTime(now);
		userOAuthInfoEntity.setUserName(userName);
		userOAuthInfoEntity.setUserId(ticket.getUserId());
		long result = userOAuthInfoDAO.insertOrUpdate(userOAuthInfoEntity);

		if (result == 1) {
			UserDetailInfoEntity entity = new UserDetailInfoEntity();
			entity.setUserId(ticket.getUserId());
			entity.setAvatar(avatar);
			entity.setGender(gender.gender());
			entity.setUserName(userName);
			entity.setNickName(nickName);
			result = result + userDetailInfoDAO.insertOrUpdate(entity);
		}

		if (result != 2) {
			throw new UserException(UserException.EXCEPTION_BIND_ACCOUNT_HAS_EXIST_OR_SAME_TYPE_HAS_EXIST, null);
		}

		BindThirdAccountResultDTO dto = new BindThirdAccountResultDTO();
		dto.setBindSuccess(true);

		dto.setAvatar(avatar);
		dto.setGender(gender);
		dto.setUserName(userName);
		dto.setNickName(nickName);

		return dto;
	}

	@Transactional
	@Override
	public boolean suicide(String t) {
		Ticket couple = super.checkTicket(t);
		if (couple.getUserId() > 0) {
			Long userId = couple.getUserId();

			List<UserOAuthInfoEntity> oauthAccountList = userOAuthInfoDAO.getOAuthAccountList(userId);
			if (oauthAccountList != null) {
				for (UserOAuthInfoEntity oauthAccount : oauthAccountList) {
					String openId = oauthAccount.getOpenId() + "";
					UserType accountType = oauthAccount.getUserAccountType();

					userCache.delUserId(openId, accountType);
					userCache.delOAuthAccountInfoByUserIdAndType(userId, accountType);
				}
			}

			userCache.delUserInfoByUserId(userId);

			UserDetailInfoEntity userAccount = userDetailInfoDAO.getUserInfo(userId);
			if (userAccount != null) {
				userCache.delUserInfoByUserId(userId);
				userOAuthInfoDAO.delUserOAuthInfo(userId);
				userDetailInfoDAO.delUserDetailInfo(userId);
			}
			return true;
		} else {
			return false;
		}
	}

	// create new record, or update if exists.
	@Override
	public Long newUser(String openId, String ip, UserType accountType, String avatar, String nickName,
			String userName, Gender gender) {
		//TODO userId fetch to do
		Long userId = (long)Math.random()*1000000000;//暂时凑乎不报错，然后再改。算法(auto_increment)后的long值,保证唯一且是正整数.
		
		long now = System.currentTimeMillis();

		UserOAuthInfoEntity userOAuthInfoEntity = new UserOAuthInfoEntity();
		userOAuthInfoEntity.setBindIp(IPUtil.Ip2Int(ip));
		userOAuthInfoEntity.setBindTime(now);
		userOAuthInfoEntity.setAvatar(avatar);
		userOAuthInfoEntity.setNickName(nickName);
		userOAuthInfoEntity.setOpenId(openId);
		userOAuthInfoEntity.setGender(gender.gender());
		userOAuthInfoEntity.setType(accountType.type());
		userOAuthInfoEntity.setUpdateIp(IPUtil.Ip2Int(ip));
		userOAuthInfoEntity.setUpdateTime(now);
		userOAuthInfoEntity.setUserName(userName);
		userOAuthInfoEntity.setUserId(userId);

		UserDetailInfoEntity userDetailInfoEntity = new UserDetailInfoEntity();
		userDetailInfoEntity.setAvatar(avatar);
		userDetailInfoEntity.setNickName(nickName);
		userDetailInfoEntity.setRegistIp(IPUtil.Ip2Int(ip));
		userDetailInfoEntity.setRegistTime(now);
		userDetailInfoEntity.setGender(gender.gender());
		userDetailInfoEntity.setUpdateIp(IPUtil.Ip2Int(ip));
		userDetailInfoEntity.setUpdateTime(now);
		userDetailInfoEntity.setUserName(userName);
		userDetailInfoEntity.setUserId(userId);

		if (StringUtils.isEmpty(userDetailInfoEntity.getAvatar())) {
			userDetailInfoEntity.setAvatar(UserAvatarPlaceholderUtil.getPlaceholderAvatar());
		}

		if (StringUtils.isEmpty(userOAuthInfoEntity.getAvatar())) {
			userOAuthInfoEntity.setAvatar(userDetailInfoEntity.getAvatar());
		}

		if (StringUtils.isEmpty(userDetailInfoEntity.getNickName())
				|| StringUtils.isEmpty(userDetailInfoEntity.getUserName())
				|| StringUtils.isEmpty(userDetailInfoEntity.getAvatar())
				|| StringUtils.isEmpty(userOAuthInfoEntity.getNickName())
				|| StringUtils.isEmpty(userOAuthInfoEntity.getUserName())
				|| StringUtils.isEmpty(userOAuthInfoEntity.getAvatar())) {
			// TODO
			// throw new
			// PassportException(PassportException.EXCEPTION_OBTAIN_PORTRAIT_FAILED,
			// "EXCEPTION_OBTAIN_PORTRAIT_FAILED", null);
		}

		long ts = System.currentTimeMillis();
		userDetailInfoEntity.setLastLoginTime(ts);
		userOAuthInfoEntity.setLastLoginTime(ts);

		userDetailInfoEntity.setUserCategory(UserType.valueOf(userOAuthInfoEntity.getType()).category());

		Long createResult = userDetailInfoDAO.insertOrUpdate(userDetailInfoEntity);
		if (createResult != null && createResult > 0) {
			userDetailInfoEntity.setUserId(createResult);
			userOAuthInfoEntity.setUserId(createResult);
			userOAuthInfoDAO.insertOrUpdate(userOAuthInfoEntity);
			// passportCache.delOAuthAccountInfoByUserIdAndType(userId, type);
			// passportCache.delUserInfoByUserId(userId);
			// passportCache.delUserId(openId, type);
		}

		return userDetailInfoEntity.getUserId();
	}

	// ***********************(3).user-ticket***********************//

	@Override
	public UserTicketDTO validate(String t) {
		try {
			Ticket ticket = super.checkTicket(t);

			UserTicketDTO dto = new UserTicketDTO();
			dto.setUserSecretKey(ticket.getUserSecretKey());
			dto.setUserId(ticket.getUserId());
			return dto;
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public UserDetailInfoDTO getUserDetailInfo(String t) {
		try {
			Ticket ticket = super.checkTicket(t);
			Long userId = ticket.getUserId();
			UserDetailInfoEntity entity = userDetailInfoDAO.getUserInfo(userId);

			if (entity == null) {
				return null;
			}

			return UserUtil.convertToUserDetailInfoDTO(entity);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return null;
		}
	}

}
