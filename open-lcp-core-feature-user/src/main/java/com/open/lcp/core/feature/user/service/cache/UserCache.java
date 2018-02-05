package com.open.lcp.core.feature.user.service.cache;

import com.open.lcp.core.feature.user.service.dao.entity.UserOAuthInfoEntity;
import com.open.lcp.core.feature.user.api.UserType;
import com.open.lcp.core.feature.user.service.dao.entity.UserDetailInfoEntity;
import com.open.lcp.core.feature.user.ticket.Ticket;

public interface UserCache {

	// 1.secretKey set and get

	public Boolean setTicket(Ticket ticket, String t);

	public Ticket getTicket(String t);

	// 5.set or get userId by openId and type

	public Long getUserId(String openId, UserType type);

	public Boolean setUserId(String openId, UserType type, Long userId);

	public Long delUserId(String openId, UserType type);

	// 7.set or get OAuthAccountInfo By UserIdAndType

	public UserOAuthInfoEntity getOAuthAccountInfoByUserIdAndType(Long userId, UserType type);

	public Boolean setOAuthAccountInfoByUserIdAndType(Long userId, UserType type,
			UserOAuthInfoEntity passportOAuthAccountEntity);

	public Long delOAuthAccountInfoByUserIdAndType(Long userId, UserType type);

	// 8.set or get PassportUserAccountEntity By UserId

	public UserDetailInfoEntity getUserInfoByUserId(Long userId);

	public Boolean setUserInfoByUserId(Long userId, UserDetailInfoEntity passportUserAccountEntity);

	public Long delUserInfoByUserId(Long userId);

}
