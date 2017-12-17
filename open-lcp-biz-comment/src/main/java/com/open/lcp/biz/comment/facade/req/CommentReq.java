package com.open.lcp.biz.comment.facade.req;

import com.open.lcp.core.api.annotation.LcpClassDesc;
import com.open.lcp.core.api.annotation.LcpParamRequired;

@LcpClassDesc("添加评论")
public class CommentReq extends CommentBaseReq {

	@LcpParamRequired(value = false, desc = "评论id,如果该评论是回复某个用户的评论则需要传递该参数")
	private Long cid;

	@LcpParamRequired(desc = "评论内容")
	private String comment;

	@LcpParamRequired(value = false, desc = "设备类型")
	private String device;

	@LcpParamRequired(desc = "评论唯一标识，客户端计算，用来防止用户重发, 例如:MD5(tid+userId+comment+客户端当前时间戳精确到小时)")
	private String triggerId;

	@LcpParamRequired(desc = "评论来源编号,短视频则是videoId")
	private String sourceId;

	@LcpParamRequired(value = false, desc = "客户端端口号")
	private String clientPort;

	@LcpParamRequired(value = false, desc = "下载速度，给PC迅雷用")
	private String downLoadSpeed;

	@LcpParamRequired(value = false, desc = "是否匿名，给PC迅雷用")
	private boolean anonymous;

	@LcpParamRequired(value = false, desc = "带宽，给PC迅雷用")
	private String bandwidth;

	@LcpParamRequired(value = false, desc = "附加参数的key，格式为key1,key2,key3,需要另写参数传值")
	private String extParams;

	public String getExtParams() {
		return extParams;
	}

	public void setExtParams(String extParams) {
		this.extParams = extParams;
	}

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getTriggerId() {
		return triggerId;
	}

	public void setTriggerId(String triggerId) {
		this.triggerId = triggerId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getClientPort() {
		return clientPort;
	}

	public void setClientPort(String clientPort) {
		this.clientPort = clientPort;
	}

	public String getDownLoadSpeed() {
		return downLoadSpeed;
	}

	public void setDownLoadSpeed(String downLoadSpeed) {
		this.downLoadSpeed = downLoadSpeed;
	}

	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	public String getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(String bandwidth) {
		this.bandwidth = bandwidth;
	}

	@Override
	public String toString() {
		return "CommentReq{" + "cid=" + cid + ", comment='" + comment + '\'' + ", device='" + device + '\''
				+ ", triggerId='" + triggerId + '\'' + ", sourceId='" + sourceId + '\'' + ", clientPort='" + clientPort
				+ '\'' + ", downLoadSpeed='" + downLoadSpeed + '\'' + ", anonymous=" + anonymous + ", bandwidth='"
				+ bandwidth + '\'' + ", extParams='" + extParams + '\'' + '}';
	}
}
