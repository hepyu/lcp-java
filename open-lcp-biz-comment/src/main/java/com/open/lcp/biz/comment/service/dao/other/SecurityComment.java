package com.open.lcp.biz.comment.service.dao.other;

public class SecurityComment {

	private long id;
	
	private String content;
	
	private String userName;
	
	private long time;
	
	private long uid;
	
	private Long replyUid;
	
	private String ip;
	
	private String checker;
	
	private long checkTime;
	
	private String gcid;
	
	private String videoId;
	
	private int appId;

    private String reportLog;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public Long getReplyUid() {
		return replyUid;
	}

	public void setReplyUid(Long replyUid) {
		this.replyUid = replyUid;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	public long getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(long checkTime) {
		this.checkTime = checkTime;
	}

	public String getGcid() {
		return gcid;
	}

	public void setGcid(String gcid) {
		this.gcid = gcid;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

    public String getReportLog() {
        return reportLog;
    }

    public void setReportLog(String reportLog) {
        this.reportLog = reportLog;
    }
}
