package com.open.lcp.biz.passport.service.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.open.lcp.biz.passport.UserAccountType;
import com.open.lcp.biz.passport.dto.UserAccountTicketDTO;
import com.open.lcp.biz.passport.dto.PassportUserAccountDTO;
import com.open.lcp.biz.passport.service.AbstractAccountService;
import com.open.lcp.biz.passport.service.AccountTicketService;
import com.open.lcp.biz.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.open.lcp.biz.passport.service.dao.entity.PassportUserAccountEntity;
import com.open.lcp.biz.passport.ticket.Ticket;
import com.open.lcp.biz.passport.util.AccountUtil;
import com.open.lcp.core.api.info.CoreFeatureUserAccountTicketInfo;

@Service
public class AccountTicketServiceImpl extends AbstractAccountService implements AccountTicketService {

	private final Log logger = LogFactory.getLog(AccountTicketServiceImpl.class);

	@Override
	public CoreFeatureUserAccountTicketInfo validateTicket(String t) {
		try {
			Ticket couple = super.checkTicket(t);

			UserAccountTicketDTO dto = new UserAccountTicketDTO();
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
	public PassportUserAccountDTO getUserInfo(String t) {
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
