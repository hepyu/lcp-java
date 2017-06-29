package com.open.passport.cache;

import com.open.passport.MobileCodeType;
import com.open.passport.UserAccountType;
import com.open.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.open.passport.service.dao.entity.PassportUserAccountEntity;
import com.open.passport.ticket.Ticket;

public interface PassportCache {

	// 1.secretKey set and get

	public Boolean setTicket(Ticket ticket, String t);

	public Ticket getTicket(String t);

	// 3.手机验证码
	public Boolean setMobileCode(String mobile, String deviceId, int appId, MobileCodeType type, String mobileCode);

	public Boolean existMobileCode(String mobile, String deviceId, int appId, MobileCodeType type, String mobielCode);

	// 5.set or get userId by openId and type

	public Long getUserId(String openId, UserAccountType type);

	public Boolean setUserId(String openId, UserAccountType type, Long userId);

	public Long delUserId(String openId, UserAccountType type);

	// 7.set or get OAuthAccountInfo By UserIdAndType

	public PassportOAuthAccountEntity getOAuthAccountInfoByUserIdAndType(Long userId, UserAccountType type);

	public Boolean setOAuthAccountInfoByUserIdAndType(Long userId, UserAccountType type,
			PassportOAuthAccountEntity passportOAuthAccountEntity);

	public Long delOAuthAccountInfoByUserIdAndType(Long userId, UserAccountType type);

	// 8.set or get PassportUserAccountEntity By UserId

	public PassportUserAccountEntity getUserInfoByUserId(Long userId);

	public Boolean setUserInfoByUserId(Long userId, PassportUserAccountEntity passportUserAccountEntity);

	public Long delUserInfoByUserId(Long userId);

}
