package com.open.lcp.core.feature.user.api.rpc;

import com.open.lcp.core.feature.user.api.UserType;
import com.open.lcp.core.feature.user.api.dto.GetUserIdResultDTO;
import com.open.lcp.core.feature.user.api.dto.UserDetailInfoDTO;

public interface UserDubboRPC {

	public UserDetailInfoDTO getUserDetailInfo(Long userId);

	public GetUserIdResultDTO getUserId(String openId, UserType userType);

}
