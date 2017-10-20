package com.open.lcp.biz.comment.facade.resp;

import com.open.lcp.core.base.annotation.LcpParamRequired;

public class PointResultResp implements PointResult {

	@LcpParamRequired(value = true, desc = "操作结果,0为成功，其它值为失败")
	private int result;

	@LcpParamRequired(value = true, desc = "新增金币")
	private long newPoint;

	@LcpParamRequired(value = true, desc = "总金币")
	private long point;

	private String source;
	private String memo;

	public static PointResultResp success() {

		return PointResultResp.build(0);
	}

	public static PointResultResp failed() {
		return PointResultResp.build(1);
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public long getNewPoint() {
		return newPoint;
	}

	public void setNewPoint(long newPoint) {
		this.newPoint = newPoint;
	}

	public long getPoint() {
		return point;
	}

	public void setPoint(long point) {
		this.point = point;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public static final PointResultResp build(int result) {
		final PointResultResp resp = new PointResultResp();
		resp.setResult(result);
		return resp;
	}

}
