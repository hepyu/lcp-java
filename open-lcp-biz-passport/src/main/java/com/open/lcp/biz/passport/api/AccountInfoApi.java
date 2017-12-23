package com.open.lcp.biz.passport.api;

import java.util.List;
import com.open.lcp.biz.passport.dto.PassportOAuthAccountDTO;
import com.open.lcp.core.api.info.CoreFeatureUserAccountInfo;
import com.open.lcp.core.api.info.CoreFeatureUserAccountTicketInfo;

public interface AccountInfoApi {

	public CoreFeatureUserAccountTicketInfo validateTicket(String t);

	public CoreFeatureUserAccountInfo getUserInfo(String ticket);

	public CoreFeatureUserAccountInfo getUserInfo(Long userId);

	public List<PassportOAuthAccountDTO> getOAuthAccountList(Long userId);

}
