package com.open.passport.service.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.open.passport.UserAccountType;
import com.open.passport.dto.CheckTicket;
import com.open.passport.dto.PassportUserAccountDTO;
import com.open.passport.service.AbstractAccount;
import com.open.passport.service.AccountTicketService;
import com.open.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.open.passport.service.dao.entity.PassportUserAccountEntity;
import com.open.passport.ticket.Ticket;
import com.open.passport.util.AccountUtil;

@Service
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

	@SuppressWarnings("deprecation")
	@Override
	public boolean suicide(String t) {

		try {
			Ticket couple = super.checkTicket(t);
			if (couple.getUserId() > 0) {
				Long userId = couple.getUserId();

				List<PassportOAuthAccountEntity> oauthAccountList = passportOAuthAccountDAO
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

				PassportUserAccountEntity userAccountEntity = passportUserAccountDAO.getUserInfoByUserId(userId);
				passportCache.delUserInfoByUserId(userAccountEntity.getUserId());

				passportOAuthAccountDAO.delPassportOAuthAccountByUserId(userAccountEntity.getUserId());
				passportUserAccountDAO.delPassportUserAccountByUserId(userAccountEntity.getUserId());

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
