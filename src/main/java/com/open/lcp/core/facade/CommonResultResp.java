package com.open.lcp.core.facade;

import com.open.lcp.core.annotation.LcpDesc;
import com.open.lcp.core.annotation.LcpRequired;

/**
 * @author hepengyuan
 */
@LcpDesc("通用操作结果")
public class CommonResultResp {

	/** 默认的成功 */
	public static final transient CommonResultResp SUCCESS = CommonResultResp.build(0);

	/** 默认的失败 */
	public static final transient CommonResultResp FAILED = CommonResultResp.build(1);

	@LcpRequired(value = true, desc = "0为成功，其它值为失败")
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
