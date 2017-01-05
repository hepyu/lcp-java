package com.open.lcp.framework.core.api.service.impl;

import java.util.ResourceBundle;

import org.springframework.stereotype.Service;

import com.open.lcp.framework.core.api.service.ApiExceptionMessageService;

@Service
public class ApiExceptionMessageServiceImpl implements ApiExceptionMessageService {

	private static final ResourceBundle rsMessage = ResourceBundle.getBundle("api_result_code_messages");

	@Override
	public String getMessage(int code) {
		return rsMessage.getString(String.format("api.result.msg.%s", code));
	}

}
