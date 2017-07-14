package com.open.lcp.core.framework.facade;

import com.open.lcp.core.base.annotation.LcpParamDesc;
import com.open.lcp.core.base.annotation.LcpParamRequired;

/**
 * @author hepengyuan
 */
@LcpParamDesc("通用操作结果")
public class CommonResultResp {

	/** 默认的成功 */
	public static final transient CommonResultResp SUCCESS = CommonResultResp.build(0);

	/** 默认的失败 */
	public static final transient CommonResultResp FAILED = CommonResultResp.build(1);

	@LcpParamRequired(value = true, desc = "0为成功，其它值为失败")
	private int result;

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public static final CommonResultResp build(int result) {
		final CommonResultResp resp = new CommonResultResp();
		resp.setResult(result);
		return resp;
	}
}
