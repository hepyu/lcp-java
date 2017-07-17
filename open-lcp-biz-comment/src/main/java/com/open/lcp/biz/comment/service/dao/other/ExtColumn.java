package com.open.lcp.biz.comment.service.dao.other;

import java.util.List;

public class ExtColumn {
	
	private int level;
	
	private String checkUser;
	
	private long checkTime;
	
	private List<Long> replyerCids;

    private String downLoadSpeed;

    private boolean isAnonymous;

    private String bandwidth;

    private String exeParamsJson;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getCheckUser() {
		return checkUser;
	}

	public void setCheckUser(String checkUser) {
		this.checkUser = checkUser;
	}

	public long getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(long checkTime) {
		this.checkTime = checkTime;
	}

	public List<Long> getReplyerCids() {
		return replyerCids;
	}

	public void setReplyerCids(List<Long> replyerCids) {
		this.replyerCids = replyerCids;
	}

    public String getDownLoadSpeed() {
        return downLoadSpeed;
    }

    public void setDownLoadSpeed(String downLoadSpeed) {
        this.downLoadSpeed = downLoadSpeed;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public String getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(String bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getExeParamsJson() {
        return exeParamsJson;
    }

    public void setExeParamsJson(String exeParamsJson) {
        this.exeParamsJson = exeParamsJson;
    }
}
