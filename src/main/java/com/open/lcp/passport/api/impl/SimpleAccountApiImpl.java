package com.open.lcp.passport.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.open.lcp.passport.ticket.Ticket;
import com.xunlei.mcp.model.ApiException;
import com.xunlei.mcp.model.UserInfo;
import com.xunlei.xlmc.passport.IsRobotEnum;
import com.xunlei.xlmc.passport.SexEnum;
import com.xunlei.xlmc.passport.UserAccountTypeEnum;
import com.xunlei.xlmc.passport.api.AccountApi;
import com.xunlei.xlmc.passport.api.PassportApiException;
import com.xunlei.xlmc.passport.bean.CheckTicket;
import com.xunlei.xlmc.passport.bean.PassportOAuthAccount;
import com.xunlei.xlmc.passport.bean.PassportUserAccount;
import com.xunlei.xlmc.passport.bean.RequestUploadAvatarResult;
import com.xunlei.xlmc.passport.cache.PassportCache;
import com.xunlei.xlmc.passport.cache.PassportRedisCache;
import com.xunlei.xlmc.passport.component.safecenter.SafeCheckResult;
import com.xunlei.xlmc.passport.exception.ElementTooManyException;
import com.xunlei.xlmc.passport.sdk.UserPortrait;
import com.xunlei.xlmc.passport.service.dao.entity.DefaultAvatarSHA1Entity;
import com.xunlei.xlmc.passport.service.dao.entity.DefaultAvatarSHA1SourceEnum;
import com.xunlei.xlmc.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.xunlei.xlmc.passport.service.dao.entity.PassportUserAccountEntity;
import com.xunlei.xlmc.passport.thread.XlSessionidCheckThreadPool;
import com.xunlei.xlmc.passport.util.UserInfoUtil;
import com.xunlei.xlmc.passport.util.UserTicketMaker;

@Component
public class SimpleAccountApiImpl extends AbstractAccount implements AccountApi {

	private final Log logger = LogFactory.getLog(SimpleAccountApiImpl.class);

	@Autowired
	PassportRedisCache passportRedisCache;
	
	private static ThreadPoolExecutor threadPool = XlSessionidCheckThreadPool.threadPool;
	
	@Override
	public CheckTicket validateTicket(String t) {
		try {
			Ticket couple = super.checkTicket(t);

			CheckTicket dto = new CheckTicket();
			dto.setUserSecretKey(couple.getUserSecretKey());
			dto.setXlUserId(couple.getXlUserId());
			return dto;
		} catch (PassportApiException e) {
			log(e, logger);
			return null;
		}
	}

	@Override
	public UserInfo getUserInfoByTicket(String t) {
		try {
			Ticket ticket = super.checkTicket(t);
			Long xlUserId = ticket.getXlUserId();
			PassportUserAccountEntity entity = obtainPassportUserAccount(xlUserId);

			if (entity == null) {
				return null;
			}

			return UserInfoUtil.convertToUserInfo(entity);
		} catch (PassportApiException pae) {
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
		}
	}

	/**
	 * 此接口不需要验票，内部使用，用于类似于xx.list显示每个item归属的用户信息
	 * 
	 */
	@Override
	public UserInfo getUserInfoByXlUserId(Long xlUserId) {
		try {
			if (xlUserId == null || xlUserId <= 0) {
				return null;
			}

			PassportUserAccountEntity userEntity = obtainPassportUserAccount(xlUserId);

			if (userEntity == null || userEntity.getNickNameType() == UserAccountTypeEnum.mobileThunder.value()) {
				return this.getMobileThunderUserWithoutValidation(xlUserId);
			} else {
				return UserInfoUtil.convertToUserInfo(userEntity);
			}
		} catch (PassportApiException pae) {
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
		}
	}

	@Override
	public PassportUserAccountDTO getUserFromMcpPlatform(Long xlUserId) {
		try {
			PassportUserAccountEntity userEntity = obtainPassportUserAccount(xlUserId);
			if (userEntity == null) {
				return null;
			} else {
				PassportUserAccountDTO account = new PassportUserAccountDTO();
				account.setDescription(userEntity.getDescription());
				account.setGender(SexEnum.valueOf(userEntity.getSexEnum()));
				account.setHeadIconUrl(userEntity.getHeadIconUrl());
				account.setNickName(userEntity.getNickName());
				account.setNickNameType(userEntity.getNickNameType());
				account.setPassportUserId(userEntity.getPassportUserId());
				account.setRegistIp(userEntity.getRegistIp());
				account.setRegistTime(userEntity.getRegistTime());
				account.setUpdateIp(userEntity.getUpdateIp());
				account.setUpdateTime(userEntity.getUpdateTime());
				account.setUserName(userEntity.getUserName());
				account.setXlUserId(userEntity.getXlUserId());
				return account;
			}

		} catch (PassportApiException pae) {
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
		}
	}

	@Override
	public UserInfo getUserInfoByXlUserIdFromXlUserCenter(Long xlUserId) {
		try {
			PassportUserAccountEntity entity = getPassportAccountService().getUserInfoByXlUserId(xlUserId);
			Long passportUserId = entity.getPassportUserId();
			UserPortrait userPortrait = getXunleiUserCenterSDK().validateAndObtainUserPortrait(null, null,
					UserTicketMaker.toKey(passportUserId), null);
			return UserInfoUtil.convertToUserInfo(userPortrait, xlUserId,
					UserAccountTypeEnum.valueOf(entity.getNickNameType()));
		} catch (PassportApiException pae) {
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
		}
	}

	@Override
	public boolean checkUserIdentify(String userIdentify) {

		try {
			boolean valid = getPassportCache().checkUserIdentify(userIdentify);
			if (!valid) {
				PassportUserAccountEntity account = getPassportAccountService()
						.getUserInfoByPassportUserId(UserTicketMaker.fromKey(userIdentify));
				if (account != null) {
					getPassportCache().setUserIdentify(userIdentify, true);
					return true;
				}
			}
			return valid;
		} catch (PassportApiException pae) {
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
		}
	}

	@Override
	public SafeCheckResult verifyImageCode(String url, String imageCodeValue) {
		return getSafeChecker().verifyImageCode(url, imageCodeValue);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean suicide(String t) {
		try {
			Ticket couple = super.checkTicket(t);
			PassportCache cache = getPassportCache();
			if (couple.getXlUserId() > 0) {
				Long xlUserId = couple.getXlUserId();

				List<PassportOAuthAccountEntity> oauthAccountList = getPassportOAuthAccountDao()
						.getOAuthAccountListByXlUserId(xlUserId);
				if (oauthAccountList != null) {
					for (PassportOAuthAccountEntity oauthAccount : oauthAccountList) {
						String openId = oauthAccount.getOpenId() + "";
						UserAccountTypeEnum accountType = oauthAccount.getUserAccountTypeEnum();

						cache.delXlUserId(openId, accountType);
						cache.delOAuthAccountInfoByXlUserIdAndType(xlUserId, accountType);
					}
				}

				cache.delUserInfoByXlUserId(xlUserId);

				List<PassportUserAccountEntity> list = getPassportUserAccountDao().getUserInfoByXlUserId(xlUserId);
				if (list != null && !list.isEmpty()) {
					PassportUserAccountEntity userAccount = list.get(0);
					Long passportUserId = userAccount.getPassportUserId();
					String userIdentify = UserTicketMaker.toKey(passportUserId);

					cache.delUserIdentify(userIdentify);
					cache.delPassportUserAccountByPassportUserId(passportUserId);
					cache.delUserInfoByXlUserId(xlUserId);

					getPassportOAuthAccountDao().delPassportOAuthAccountByXLUserId(xlUserId);
					getPassportUserAccountDao().delPassportUserAccountByXLUserId(xlUserId);
				}

				return true;
			} else {
				return false;
			}
		} catch (PassportApiException pae) {
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
		}
	}

	@Override
	public Long validateMobileThunderUser(String uid, String sid, String ip) {
		try {
			Long retUserId = passportRedisCache.getUserIdBySessionId(sid);
			if(retUserId==null){
				retUserId = innerValidateMobileThunderUser(uid, sid, ip);
				if(retUserId!=null){
					passportRedisCache.setUserIdBySessionId(sid, retUserId);
				}
			}
			return retUserId;
		} catch (Exception e) {
			logger.warn(e);
			return null;
		}
	}
	
	@Override
	public Long validateMobileThunderUserMiss(String uid, String sid,
			String ip, boolean isMiss) {
		try {
			Long retUserId = passportRedisCache.getUserIdBySessionId(sid);
			if(retUserId==null){
				if(!isMiss){
					retUserId = innerValidateMobileThunderUserNoMiss(uid, sid, ip);
				}else{//允许出错
					asyncInnerValidateMobileThunderUserNoMiss(uid, sid, ip);
				}
			}
			return retUserId;
		} catch (Exception e) {
			logger.warn(e);
			return null;
		}
	}
	
	private void asyncInnerValidateMobileThunderUserNoMiss(final String uid,final String sid,final String ip){
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				innerValidateMobileThunderUserNoMiss(uid, sid, ip);
			}
		});
	}
	
	
	private Long innerValidateMobileThunderUserNoMiss(String uid, String sid, String ip){
		Long retUserId = innerValidateMobileThunderUser(uid, sid, ip);
		if(retUserId!=null){
			passportRedisCache.setUserIdBySessionId(sid, retUserId);
		}
		return retUserId;
	}
	
	private Long innerValidateMobileThunderUser(String uid, String sid, String ip){
		try {
			
			UserPortrait userPortrait = getMobileThunderSDK().validateAndObtainUserPortrait(null, uid, sid, null);

			if (userPortrait == null) {
				return null;
			} else {
				Long xlUserIdFromXL = userPortrait.getXlUserId();
				if (xlUserIdFromXL == null || xlUserIdFromXL.longValue() <= 0
						|| xlUserIdFromXL.longValue() != Long.valueOf(uid).longValue()) {
					return null;
				} else {
					return Long.valueOf(uid);
				}
			}
		} catch (PassportApiException pae) {
			log(pae, logger);
			return null;
		}
	}

	@Override
	public UserInfo getMobileThunderUserWithoutValidation(Long xlUserId) {
		if (xlUserId == null || xlUserId <= 0) {
			return null;
		}

		try {
			final UserPortrait userPortrait = getMobileThunderSDK().getUserInfoWithoutValidation(null, xlUserId + "",
					null);
			return UserInfoUtil.convertToUserInfo(userPortrait, xlUserId, UserAccountTypeEnum.mobileThunder);
		} catch (PassportApiException pae) {
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
		}
	}

	@Override
	public List<PassportUserAccountDTO> getBigRobotList() {
		return getPassportAccountService().getBigRobotXlUserList();
	}

	@Override
	public PassportUserAccountDTO getBigRobotXlUserInfo(Long xlUserId) {
		try {
			return getPassportAccountService().getBigRobotXlUserInfo(xlUserId);
		} catch (PassportApiException pae) {
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
		}
	}

	@Override
	public IsRobotEnum isRobot(Long xlUserId) {
		UserInfo userInfo = this.getUserInfoByXlUserId(xlUserId);
		if (userInfo == null) {
			return IsRobotEnum.userNotExist;
		}
		PassportUserAccountDTO account = userInfo.getExt();
		if (account == null) {
			return IsRobotEnum.userNotExist;
		} else {
			return isRobot(account.getNickNameType().intValue());
			// int nickNameType = account.getNickNameType().intValue();
			// if (nickNameType == UserAccountTypeEnum.bigRobbot.value()
			// || nickNameType == UserAccountTypeEnum.robbot.value()) {
			// return IsRobotEnum.isRobot;
			// } else {
			// return IsRobotEnum.notRobot;
			// }
		}
	}
	
	@Override
	public IsRobotEnum isRobotCache(Long xlUserId) { 
         return getPassportAccountService().isRobot(xlUserId);
		
	}

	@Override
	public IsRobotEnum isRobot(Integer userAccountType) {
		if (userAccountType == UserAccountTypeEnum.bigRobbot.value()
				|| userAccountType == UserAccountTypeEnum.robbot.value()) {
			return IsRobotEnum.isRobot;
		} else {
			return IsRobotEnum.notRobot;
		}
	}

	@Override
	public Long randomRobotXlUserId() {
		List<PassportUserAccountDTO> list = getPassportAccountService().getRobotXlUserList();
		if (list == null) {
			return null;
		} else {
			int size = list.size();
			int index = (int) (Math.random() * size);
			return list.get(index).getXlUserId();
		}
	}

	@Override
	public List<PassportUserAccountDTO> getAllRobot() {
		return getPassportAccountService().getAllRobot();
	}

	@Override
	public List<PassportUserAccountDTO> getUserList(List<Long> xlUserIdList) throws ElementTooManyException {
		if (xlUserIdList == null || xlUserIdList.size() == 0) {
			return null;
		}
		int size = xlUserIdList.size();
		if (size > ElementTooManyException.GET_USER_LIST_SIZE_UP_LIMIT) {
			throw new ElementTooManyException(size);
		}
		return getPassportAccountService().getUserList(xlUserIdList);
	}

	@Override
	public List<PassportOAuthAccountDTO> getOAuthAccountListByXlUserId(Long xlUserId) {
		List<PassportOAuthAccountEntity> entityList = getPassportAccountService()
				.getOAuthAccountListByXlUserId(xlUserId);
		List<PassportOAuthAccountDTO> oauthAccountList = new ArrayList<PassportOAuthAccountDTO>();
		PassportOAuthAccountDTO oauthAccount = null;
		for (PassportOAuthAccountEntity entity : entityList) {
			oauthAccount = new PassportOAuthAccountDTO();
			oauthAccount.setBindIp(entity.getBindIp());
			oauthAccount.setBindTime(entity.getBindTime());
			oauthAccount.setHeadIconUrl(entity.getHeadIconUrl());
			oauthAccount.setNickName(entity.getNickName());
			oauthAccount.setOpenId(entity.getOpenId());
			oauthAccount.setGender(SexEnum.valueOf(SexEnum.valueOf(entity.getSex())));
			oauthAccount.setType(UserAccountTypeEnum.valueOf(entity.getType()));
			oauthAccount.setUpdateIp(entity.getUpdateIp());
			oauthAccount.setUpdateTime(entity.getUpdateTime());
			oauthAccount.setUserName(entity.getUserName());
			oauthAccount.setXlUserId(entity.getXlUserId());
			oauthAccountList.add(oauthAccount);
		}
		return oauthAccountList;
	}

	@Override
	public int unbindAccount(Long xlUserId, UserAccountTypeEnum userAccountType) {
		return getPassportAccountService().unbindAccount(xlUserId, userAccountType);
	}

	@Override
	public boolean updateGender(Long xlUserId, SexEnum sexEnum) {
		try {
			return getPassportAccountService().updateSex(xlUserId, sexEnum);
		} catch (PassportApiException e) {
			logger.error(e.getMessage(), e);
			throw new ApiException(e.getPassportCode());
		}
	}

	@Override
	public boolean updateNickName(Long xlUserId, String nickName) {
		try {
			return getPassportAccountService().updateNickName(xlUserId, nickName);
		} catch (PassportApiException e) {
			logger.error(e.getMessage(), e);
			throw new ApiException(e.getPassportCode());
		}
	}

	@Override
	public boolean updateDescription(Long xlUserId, String description) {
		try {
			return getPassportAccountService().updateDescription(xlUserId, description);
		} catch (PassportApiException e) {
			logger.error(e.getMessage(), e);
			throw new ApiException(e.getPassportCode());
		}
	}

	@Override
	public boolean updateNickNameType(Long xlUserId, UserAccountTypeEnum userAccountType) {
		try {
			return getPassportAccountService().updateNickNameType(xlUserId, userAccountType);
		} catch (PassportApiException e) {
			logger.error(e.getMessage(), e);
			throw new ApiException(e.getPassportCode());
		}
	}

	// @Override
	// public RequestUploadAvatarResult requestUploadAvatar(Long xlUserId) {
	// return requestUploadAvatar(null, xlUserId);
	// }
	//
	// @Override
	// public String commitUploadAvatar(Long xlUserId) {
	// return commitUploadAvatar(null, xlUserId);
	// }

	@Override
	public RequestUploadAvatarResult requestUploadAvatar(String prefix, Long xlUserId) {
		try {
			return getPassportAccountService().requestUploadAvatar(prefix, xlUserId);
		} catch (PassportApiException e) {
			logger.error(e.getMessage(), e);
			throw new ApiException(e.getPassportCode());
		}
	}

	@Override
	public String commitUploadAvatar(String prefix, Long xlUserId) {
		try {
			return getPassportAccountService().commitUploadAvatar(prefix, xlUserId);
		} catch (PassportApiException e) {
			logger.error(e.getMessage(), e);
			throw new ApiException(e.getPassportCode());
		}
	}

	@Override
	public UserAccountTypeEnum getUserType(Long xlUserId) {
		try {
			PassportUserAccountEntity userEntity = obtainPassportUserAccount(xlUserId);
			if (userEntity != null) {
				UserAccountTypeEnum userAccountType = UserAccountTypeEnum.valueOf(userEntity.getNickNameType());
				return userAccountType;
			} else {
				if (getMobileThunderUserWithoutValidation(xlUserId) != null) {
					return UserAccountTypeEnum.mobileThunder;
				} else {
					return UserAccountTypeEnum.unknow;
				}
			}
		} catch (PassportApiException pae) {
			log(pae, logger);
			throw new ApiException(pae.getPassportCode(), pae.getMessage());
		}
	}

	@Override
	public List<DefaultAvatarSHA1Entity> getAllDefaultAvatarSHA1() {
		return getPassportAccountService().getDefaultAvatarSHA1List();
	}

	@Override
	public boolean setDefaultAvatarSHA1(String avatarSHA1, String originAvatarUrl, int source) {
		return getPassportAccountService().setDefaultAvatar(avatarSHA1, originAvatarUrl,
				DefaultAvatarSHA1SourceEnum.valueOf(source));
	}

	@Override
	public DefaultAvatarSHA1Entity getDefaultAvatarSHA1(String avatarSHA1) {
		return getPassportAccountService().getDefaultAvatarSHA1(avatarSHA1);
	}

	@Override
	public boolean isDefaultAvatar(String avatarSHA1) {
		return getPassportAccountService().isDefaultAvatar(avatarSHA1);
	}

	@Override
	public boolean isDefaultHeadIcon(String headIconUrl) {
		return getPassportAccountService().isDefaultHeadIcon(headIconUrl);
	}



	

}
