package com.open.lcp.biz.comment.facade.resp;

import java.util.List;

import com.open.lcp.core.base.annotation.LcpClassDesc;
import com.open.lcp.core.base.annotation.LcpParamRequired;

@LcpClassDesc("评论列表")
public class CommentResp {

	@LcpParamRequired(desc = "来源app")
	private int appId;

	@LcpParamRequired(desc = "评论id")
	private long cid;

	@LcpParamRequired(desc = "评论内容")
	private String comment;

	@LcpParamRequired(desc = "评论时间")
	private long time;

	@LcpParamRequired(value = false, desc = "评论设备")
	private String device;

	@LcpParamRequired(value = false, desc = "资源源id")
	private String sourceId;

	@LcpParamRequired(value = false, desc = "所在省份")
	private String po;

	@LcpParamRequired(value = false, desc = "所在城市")
	private String ci;

	@LcpParamRequired(desc = "评论用户id")
	private long uid;

	@LcpParamRequired(value = false, desc = "评论用户昵称")
	private String userName;

	@LcpParamRequired(value = false, desc = "评论用户头像")
	private String userImg;

	@LcpParamRequired(value = false, desc = "红2用户级别")
	private int userType;

	@LcpParamRequired(value = false, desc = "红2用户级别")
	private List<Integer> userTypes;

	@LcpParamRequired(value = false, desc = "用户类型文案")
	private List<String> userTypeNames;

	@LcpParamRequired(value = false, desc = "是否待审核状态")
	private Boolean isPdRiew;

	@LcpParamRequired(value = false, desc = "当前用户是否点过赞")
	private Boolean isPraise;

	@LcpParamRequired(value = true, desc = "点赞数")
	private long gcount;

	@LcpParamRequired(value = true, desc = "分享数")
	private long scount;

	@LcpParamRequired(value = true, desc = "回复数")
	private long rcount;

	@LcpParamRequired(value = false, desc = "回复评论列表")
	private List<CommentReplyResp> replys;

	@LcpParamRequired(value = false, desc = "评论类型")
	private String commentType;

	@LcpParamRequired(value = false, desc = "下载速度")
	private String downLoadSpeed;

	@LcpParamRequired(value = false, desc = "是否匿名")
	private Boolean isAnonymous;

	@LcpParamRequired(value = false, desc = "带宽")
	private String bandwidth;

	@LcpParamRequired(value = false, desc = "附加参数的json对象", struct = LcpParamRequired.Struct.JSON)
	private String extParamsJson;
	//
	// @LcpParamRequired(value = false, desc = "被at的用户列表")
	// private List<AtList> atList;

	@LcpParamRequired(value = false, desc = "标识直播的礼物评论")
	private int liveGift;

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserImg() {
		return userImg;
	}

	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public Boolean getIsPraise() {
		return isPraise;
	}

	public Boolean getIsPdRiew() {
		return isPdRiew;
	}

	public void setIsPdRiew(Boolean isPdRiew) {
		this.isPdRiew = isPdRiew;
	}

	public void setIsPraise(Boolean isPraise) {
		this.isPraise = isPraise;
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

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getGcount() {
		return gcount;
	}

	public void setGcount(long gcount) {
		this.gcount = gcount;
	}

	public long getScount() {
		return scount;
	}

	public void setScount(long scount) {
		this.scount = scount;
	}

	public long getRcount() {
		return rcount;
	}

	public void setRcount(long rcount) {
		this.rcount = rcount;
	}

	public List<CommentReplyResp> getReplys() {
		return replys;
	}

	public void setReplys(List<CommentReplyResp> replys) {
		this.replys = replys;
	}

	public List<Integer> getUserTypes() {
		return userTypes;
	}

	public void setUserTypes(List<Integer> userTypes) {
		this.userTypes = userTypes;
	}

	public String getCommentType() {
		return commentType;
	}

	public void setCommentType(String commentType) {
		this.commentType = commentType;
	}

	public String getDownLoadSpeed() {
		return downLoadSpeed;
	}

	public void setDownLoadSpeed(String downLoadSpeed) {
		this.downLoadSpeed = downLoadSpeed;
	}

	public Boolean getAnonymous() {
		return isAnonymous;
	}

	public void setAnonymous(Boolean anonymous) {
		isAnonymous = anonymous;
	}

	public String getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(String bandwidth) {
		this.bandwidth = bandwidth;
	}

	public String getExtParamsJson() {
		return extParamsJson;
	}

	public void setExtParamsJson(String extParamsJson) {
		this.extParamsJson = extParamsJson;
	}

	public List<String> getUserTypeNames() {
		return userTypeNames;
	}

	public void setUserTypeNames(List<String> userTypeNames) {
		this.userTypeNames = userTypeNames;
	}

	// public List<AtList> getAtList() {
	// return atList;
	// }
	//
	// public void setAtList(List<AtList> atList) {
	// this.atList = atList;
	// }

	public int getLiveGift() {
		return liveGift;
	}

	public void setLiveGift(int liveGift) {
		this.liveGift = liveGift;
	}
}
