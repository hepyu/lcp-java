package com.open.lcp.framework.core.api.service.dao.entity;

import com.open.lcp.framework.core.annotation.LcpRequired;

public class LcpTimeSwitcherEntity {

	@LcpRequired(value = true, min = 0, desc = "开关编号")
	private int tsid;
	@LcpRequired(value = false, desc = "开关名称")
	private String tsname;
	@LcpRequired(value = false, desc = "开关开始")
	private long tsbegin;
	@LcpRequired(value = false, desc = "开关结束")
	private long tsend;
	@LcpRequired(value = false, desc = "开关其它设置")
	private String tsext;

	public int getTsid() {
		return tsid;
	}

	public void setTsid(int tsid) {
		this.tsid = tsid;
	}

	public String getTsname() {
		return tsname;
	}

	public void setTsname(String tsname) {
		this.tsname = tsname;
	}

	public long getTsbegin() {
		return tsbegin;
	}

	public void setTsbegin(long tsbegin) {
		this.tsbegin = tsbegin;
	}

	public long getTsend() {
		return tsend;
	}

	public void setTsend(long tsend) {
		this.tsend = tsend;
	}

	public String getTsext() {
		return tsext;
	}

	public void setTsext(String tsext) {
		this.tsext = tsext;
	}

}