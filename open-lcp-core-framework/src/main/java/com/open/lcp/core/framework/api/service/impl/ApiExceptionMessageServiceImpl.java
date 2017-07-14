package com.open.lcp.core.framework.api.service.impl;

import java.util.ResourceBundle;

import org.springframework.stereotype.Service;

import com.open.lcp.core.framework.api.service.ApiExceptionMessageService;

@Service
public class ApiExceptionMessageServiceImpl implements ApiExceptionMessageService {

	private static final ResourceBundle rsMessage = ResourceBundle.getBundle("api_result_code_messages");

	@Override
	public String getMessage(int code) {
		return rsMessage.getString(String.format("api.result.msg.%s", code));
	}

}
