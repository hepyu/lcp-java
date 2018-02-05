package com.open.lcp.core.feature.user.api.rpc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.open.lcp.core.feature.user.api.dto.UserDetailInfoDTO;
import com.open.lcp.core.feature.user.api.dto.UserTicketDTO;
import com.open.lcp.core.feature.user.api.rpc.UserTicketDubboRPC;
import com.open.lcp.core.feature.user.service.UserService;
import com.open.lcp.core.feature.user.ticket.Ticket;
import com.open.lcp.core.feature.user.ticket.TicketManager;

@Service
public class UserTicketDubboRPCImpl implements UserTicketDubboRPC {

	@Autowired
	private UserService userService;
	
	@Autowired
	private TicketManager ticketManager;

	@Override
	public UserTicketDTO validate(String t) {
		return userService.validate(t);
	}

	@Override
	public UserDetailInfoDTO getUserDetailInfo(String ticket) {
		return userService.getUserDetailInfo(ticket);
	}

	@Override
	public UserTicketDTO generateKey(int appId, Long userId) {
		Ticket ticket = ticketManager.generateKey(appId, userId);
		UserTicketDTO dto = new UserTicketDTO();
		dto.setUserId(userId);
		dto.setUserSecretKey(ticket.getUserSecretKey());
		return dto;
	}
	

}
