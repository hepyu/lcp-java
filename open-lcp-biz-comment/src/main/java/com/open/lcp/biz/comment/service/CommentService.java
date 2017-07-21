package com.open.lcp.biz.comment.service;

import com.open.lcp.biz.comment.facade.resp.CommentAddResp;
import com.open.lcp.core.base.info.BaseUserAccountInfo;

public interface CommentService {

//	CommentAddResp addComment(int appId, int typeId, String tid, Long cid, String ip, String device, String comment,
//			BaseUserAccountInfo user, String sourceId, String triggerId, String clientPort, String recommendPlatform,
//			String downLoadSpeed, boolean isAnonymous, String bandwidth, String extParamsJson);

	// int getRcount(int appId, int typeId, String tid);
	//
	// SecurityCommentReslut listCheckComments(int typeId, long pageId, int
	// pageSize,
	// int status, String orderBy, String keyword, String author,
	// long authorId, String checker, long startCkeckTime,
	// long endCkeckTime) throws IllegalArgumentException, IOException;
	//
	// boolean checkComments(int typeId, int status, String checkUser, long...
	// ids)
	// throws IllegalArgumentException, IOException;
	//
	// boolean setConf(int appId, CommentConfigReq req) throws IOException;
	//
	// CommentListResp listComment(int appId, int typeId, String tid, long
	// lastId,
	// int pageSize, String loadType, String category, Long uid, Long startTime,
	// String deviceId)
	// throws IOException;
	//
	// long count(int appId, int typeId, String tid, long cid, int type,
	// UserInfo userInfo, String sourceId, String deviceId, String userAgent)
	// throws IOException;
	//
	// void addPraiserLog(UserInfo userInfo, String deviceId, String tid, long
	// cid, int appId);
	//
	// boolean del(int appId, int typeId, String tid, Long commentId, long
	// userId)
	// throws IOException;
	//
	// CommentUserListResp listUserComments(long uid, boolean islogin, long
	// lastId, int len)
	// throws IllegalArgumentException, IOException;
	//
	// void report(int typeId, int appId, int type, String tid, long cid)
	// throws IllegalArgumentException, IOException;
	//
	// CommentResp get(int typeId, long cid) throws IllegalArgumentException,
	// IOException;
	//
	// CommentStatListResp commentCheckerStat(String checker, long
	// checkStartTime, long checkEndTime);
	//
	// int updateDevicePraiseToUser(Long userId, String deviceId);
	//
	// int commentCount(String tid, long videoId);
	//
	// CommentBatchVideoReq commentCountByVideoIds(CommentBatchVideoReq req);
	//
	// List<CommentUserListPcResp.CommentUserPc> listUserCommentsPc(long userId,
	// long lastId, int len) throws IOException;
	//
	// void refreshCommentCache();
	//
	// void refreshCommentEs(int typeId);
	//
	// void commentAuditReload(AdminCommentFlushReq req);
}
