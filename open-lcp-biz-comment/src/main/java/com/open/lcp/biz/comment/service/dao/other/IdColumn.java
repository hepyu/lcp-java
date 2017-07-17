package com.open.lcp.biz.comment.service.dao.other;

public class IdColumn {
	
	private int appId; 
	
	private int typeId; 
	
	private String tid;

	private long cid;
	
	private Long replyCid;
	
	private Long replyRid;
	
	private String ip;
	
	private String po;
	
	private String ci;
	
	private long time;
	
	private String device;
	
	private String sourceId;
	
	private int sourceAppId; 
	
	private String clientPort;

    private String commentType; //用户类型-来源类型-平台

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public Long getReplyCid() {
		return replyCid;
	}

	public void setReplyCid(Long replyCid) {
		this.replyCid = replyCid;
	}

	public Long getReplyRid() {
		return replyRid;
	}

	public void setReplyRid(Long replyRid) {
		this.replyRid = replyRid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPo() {
		return po;
	}

	public void setPo(String po) {
		this.po = po;
	}

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public int getSourceAppId() {
		return sourceAppId;
	}

	public void setSourceAppId(int sourceAppId) {
		this.sourceAppId = sourceAppId;
	}

	public String getClientPort() {
		return clientPort;
	}

	public void setClientPort(String clientPort) {
		this.clientPort = clientPort;
	}

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }
}
