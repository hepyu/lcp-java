package com.open.lcp.passport.service.impl;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.open.lcp.passport.UserAccountType;
import com.open.lcp.passport.dto.CheckTicket;
import com.open.lcp.passport.dto.PassportUserAccountDTO;
import com.open.lcp.passport.service.AbstractAccount;
import com.open.lcp.passport.service.AccountTicketService;
import com.open.lcp.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.open.lcp.passport.service.dao.entity.PassportUserAccountEntity;
import com.open.lcp.passport.ticket.Ticket;
import com.open.lcp.passport.util.AccountUtil;

public class AccountTicketServiceImpl extends AbstractAccount implements AccountTicketService {

	private final Log logger = LogFactory.getLog(AccountTicketServiceImpl.class);

	@Override
	public CheckTicket validateTicket(String t) {
		try {
			Ticket couple = super.checkTicket(t);

			CheckTicket dto = new CheckTicket();
			dto.setUserSecretKey(couple.getUserSecretKey());
			dto.setUserId(couple.getUserId());
			return dto;
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public boolean suicide(String t) {

		try {
			Ticket couple = super.checkTicket(t);
			if (couple.getUserId() > 0) {
				Long userId = couple.getUserId();

				List<PassportOAuthAccountEntity> oauthAccountList = passportOAuthAccountDao
						.getOAuthAccountListByUserId(userId);
				if (oauthAccountList != null) {
					for (PassportOAuthAccountEntity oauthAccount : oauthAccountList) {
						String openId = oauthAccount.getOpenId() + "";
						UserAccountType accountType = oauthAccount.getUserAccountType();

						passportCache.delUserId(openId, accountType);
						passportCache.delOAuthAccountInfoByUserIdAndType(userId, accountType);
					}
				}

				passportCache.delUserInfoByUserId(userId);

				PassportUserAccountEntity userAccountEntity = passportUserAccountDao.getUserInfoByUserId(userId);
				passportCache.delUserInfoByUserId(userAccountEntity.getUserId());

				passportOAuthAccountDao.delPassportOAuthAccountByUserId(userAccountEntity.getUserId());
				passportUserAccountDao.delPassportUserAccountByUserId(userAccountEntity.getUserId());

				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public PassportUserAccountDTO getUserInfoByTicket(String t) {
		try {
			Ticket ticket = super.checkTicket(t);
			Long xlUserId = ticket.getUserId();
			PassportUserAccountEntity entity = obtainPassportUserAccount(xlUserId);

			if (entity == null) {
				return null;
			}

			return AccountUtil.convertPassportUserAccoutEntity(entity);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return null;
		}
	}

}
