package com.open.lcp.biz.comment.service.dao.hbase.impl.column;

public class UserColumn {

	private Long uid;
	
	private String name;
	
	private String img;
	
	private String xlLevel;

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getXlLevel() {
		return xlLevel;
	}

	public void setXlLevel(String xlLevel) {
		this.xlLevel = xlLevel;
	}
	
}
