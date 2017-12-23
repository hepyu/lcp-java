package com.open.lcp.biz.passport.service;

import com.open.lcp.biz.passport.dto.PassportUserAccountDTO;
import com.open.lcp.core.api.info.CoreFeatureUserAccountTicketInfo;
import com.open.lcp.core.api.service.CoreFeatureUserAccountTicketService;

public interface AccountTicketService extends CoreFeatureUserAccountTicketService {

	public CoreFeatureUserAccountTicketInfo validateTicket(String t);

	public boolean suicide(String t);

	public PassportUserAccountDTO getUserInfo(String ticket);

}
