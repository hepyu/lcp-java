package com.open.lcp.core.feature.user.api.rpc;

import com.open.lcp.core.feature.user.api.dto.UserDetailInfoDTO;
import com.open.lcp.core.feature.user.api.dto.UserTicketDTO;

public interface UserTicketDubboRPC {

	public UserTicketDTO validate(String t);

	public UserDetailInfoDTO getUserDetailInfo(String ticket);
	
	public UserTicketDTO generateKey(int appId, Long userId);
}
