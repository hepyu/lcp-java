package com.open.lcp.biz.passport.api;

import java.util.List;
import com.open.lcp.biz.passport.dto.PassportOAuthAccountDTO;
import com.open.lcp.core.api.info.BasicUserAccountInfo;
import com.open.lcp.core.api.info.BasicUserAccountTicketInfo;

public interface AccountInfoApi {

	public BasicUserAccountTicketInfo validateTicket(String t);

	public BasicUserAccountInfo getUserInfo(String ticket);

	public BasicUserAccountInfo getUserInfo(Long userId);

	public List<PassportOAuthAccountDTO> getOAuthAccountList(Long userId);

}
