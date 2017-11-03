package com.open.lcp.biz.comment.service.dao.hbase.impl.column;


public enum CommentCheckType {

	PASS(1,"Pass","AI审核通过"),
	NOPASS(2,"NoPass","AI审核不通过"),
	OFFLINE(3,"OffLine","下线"),
	ONLINE(4,"OnLine","上线"),
    CANTJUDGE(5,"CantJudge","AI无法判断"),
    FINALPASS(6,"FinalPass","人工审核通过");
	
	int type;//操作类型
	String action;//操作类型名称
	String desc;//描述
	
	CommentCheckType(int type, String action, String desc) {
		this.type = type;
		this.action = action;
		this.desc = desc;
	}
	
	public static CommentCheckType parseCommentCheckType(int type) {

		for(CommentCheckType value : CommentCheckType.values()) {
			if(type == value.getType()) {
				return value;
			}
		}
		
		return null;
	}

	public int getType() {
		return type;
	}

	public String getAction() {
		return action;
	}

	public String getDesc() {
		return desc;
	}
}
