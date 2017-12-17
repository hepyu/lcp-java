package com.open.lcp.biz.comment.facade;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.open.lcp.biz.comment.CommentRespCode;
import com.open.lcp.biz.comment.facade.req.CommentDelReq;
import com.open.lcp.biz.comment.facade.req.CommentReq;
import com.open.lcp.biz.comment.facade.resp.CommentAddResp;
import com.open.lcp.biz.comment.service.CommentService;
import com.open.lcp.biz.comment.util.CommentUtil;
import com.open.lcp.core.api.annotation.LcpHttpMethod;
import com.open.lcp.core.api.facade.ApiFacade;
import com.open.lcp.core.api.info.BasicAppInitInfo;
import com.open.lcp.core.api.info.BasicUserAccountInfo;
import com.open.lcp.core.framework.api.ApiException;
import com.open.lcp.core.framework.api.command.ApiCommandContext;
import com.open.lcp.core.framework.facade.CommonResultResp;

public class CommentFacade implements ApiFacade {

	private final static Logger logger = LoggerFactory.getLogger(CommentFacade.class);

	private static final String DEFAULT_VERSION = "1.0";

	private static final String DEFAULT_AUTHOR = "Alex";
	@Resource
	private CommentService commentService;

	// @Resource
	// private AccountInfoService accountInfoService;

	// @Resource
	// private FileService fileService;

	@LcpHttpMethod(name = "comment.add", ver = DEFAULT_VERSION, desc = "资源下添加用户评论,回复用户评论", loadAppInitData = true, logon = true, auths = {
			DEFAULT_AUTHOR })
	public CommentAddResp addComment(CommentReq req, ApiCommandContext ctx) {

		int appId = ctx.getAppInfo().getAppId();
		int typeId = req.getTypeId();
		String tid = req.getTid();
		Long cid = req.getCid();
		String comment = req.getComment();
		BasicAppInitInfo data = ctx.getAppInitInfo();
		String device = "";
		String sourceId = req.getSourceId();
		if (data != null) {
			device = data.getDevice();
		} else if (StringUtils.isNotBlank(req.getDevice())) {
			device = req.getDevice();
		}
		BasicUserAccountInfo user = ctx.getUserAccountInfo();
		if (user == null) {
			throw new ApiException(CommentRespCode.CODE_4000);
		}
		String ip = ctx.getClientIp();
		String triggerId = req.getTriggerId();
		String clientPort = req.getClientPort();
		String recommendPlatform = ctx.getAppInfo().getRecommendPlatform();
		String extParams = req.getExtParams();
		String extParamsJson = CommentUtil.transformExtParams(extParams, ctx.getStringParams());
		return commentService.addComment(appId, typeId, tid, cid, ip, device, comment, user, sourceId, triggerId,
				clientPort, recommendPlatform, req.getDownLoadSpeed(), req.isAnonymous(), req.getBandwidth(),
				extParamsJson);
	}

	@LcpHttpMethod(name = "comment.del", ver = DEFAULT_VERSION, desc = "撤销评论", logon = true, auths = { DEFAULT_AUTHOR })
	public CommonResultResp del(CommentDelReq req, ApiCommandContext ctx) {
		if (ctx.getUserAccountInfo() == null) {
			return CommonResultResp.FAILED;
		}
		if (commentService.del(ctx.getAppInfo().getAppId(), req.getTypeId(), req.getTid(), req.getCid(),
				ctx.getUserAccountInfo().getUserId())) {
			return CommonResultResp.SUCCESS;
		}
		return CommonResultResp.SUCCESS;
	}
	//
	// @McpMethod(name = "comment.list", ver = DEFAULT_VERSION, desc =
	// "查询主题下评论", auths = { DEFAULT_AUTHOR })
	// public CommentListResp listComments(CommentListReq req, CommandContext
	// ctx) throws IOException {
	// ctx.addStatExt("lcStart", System.currentTimeMillis());
	// long userId = ctx.getUserId();
	// Long uid = userId > 0 ? userId : null;
	// Long startTime = req.getStartTime();
	// if (req.getVideoId() != null)
	// startTime = req.getVideoId();
	// int pageSize = req.getPageSize();
	// int extraPageSize = pageSize + 1;
	// CommentListResp resp =
	// commentService.listComment(ctx.getMcpAppInfo().getAppId(),
	// req.getTypeId(), req.getTid(),
	// req.getLastId(), extraPageSize, req.getType(), req.getCategory(), uid,
	// startTime, ctx.getDeviceId());
	// List<CommentResp> commentResps = resp.getConmments();
	// logger.debug("comment list extraPageSize {}, actualSize {}",
	// extraPageSize, commentResps.size());
	// if (CollectionUtils.isEmpty(commentResps) || commentResps.size() <
	// extraPageSize) {
	// resp.setHasMore(false);
	// logger.debug("comment list hasMore false");
	// } else {
	// commentResps.remove(commentResps.size() - 1);
	// resp.setHasMore(true);
	// logger.debug("comment list hasMore true");
	// }
	// ctx.addStatExt("lcEnd", System.currentTimeMillis());
	// return resp;
	// }
	//
	// @McpMethod(name = "comment.list.allFields", ver = DEFAULT_VERSION, desc =
	// "查询主题下评论", auths = { DEFAULT_AUTHOR })
	// public CommentListAllFieldsResp listCommentsAllFields(CommentListReq req,
	// CommandContext ctx) throws IOException {
	// int appId = ctx.getMcpAppInfo().getAppId();
	// int typeId = req.getTypeId();
	// String tid = req.getTid();
	// long userId = ctx.getUserId();
	// Long uid = userId > 0 ? userId : null;
	// // 接上级指示暂时下线热门评论
	// if (appId != 14 && "hot".equalsIgnoreCase(req.getCategory())) {
	// CommentListAllFieldsResp resp = new CommentListAllFieldsResp();
	// resp.setTid(tid);
	// resp.setConmments(new ArrayList<CommentAllFieldsResp>());
	// return resp;
	// }
	// Long startTime;
	// if (req.getVideoId() != null)
	// startTime = req.getVideoId();
	// else
	// startTime = req.getStartTime();
	//
	// CommentListResp commentListResp = commentService.listComment(appId,
	// typeId, tid, req.getLastId(),
	// req.getPageSize(), req.getType(), req.getCategory(), uid, startTime,
	// ctx.getDeviceId());
	//
	// if (commentListResp != null && commentListResp.getConmments() != null) {
	// List<CommentResp> commentList = commentListResp.getConmments();
	// List<CommentAllFieldsResp> rtCommentList = new ArrayList<>();
	//
	// CommentAllFieldsResp commentAllFieldsResp;
	// for (CommentResp comment : commentList) {
	// commentAllFieldsResp = new CommentAllFieldsResp();
	// try {
	// BeanUtils.copyProperties(commentAllFieldsResp, comment);
	// } catch (Exception e) {
	// logger.error(e.getMessage(), e);
	// }
	//
	// List<CommentCheckColumn> commentCheckList =
	// commentDao.getCheckPassComments(req.getTypeId(),
	// comment.getCid());
	// if (commentCheckList != null && commentCheckList.size() > 0) {
	// commentAllFieldsResp.setCommentCheckColumn(commentCheckList.get(0));
	// }
	// rtCommentList.add(commentAllFieldsResp);
	// }
	//
	// CommentListAllFieldsResp resp = new CommentListAllFieldsResp();
	// resp.setGcount(commentListResp.getGcount());
	// resp.setRcount(commentListResp.getRcount());
	// resp.setTid(commentListResp.getTid());
	// resp.setConmments(rtCommentList);
	// return resp;
	// } else {
	// CommentListAllFieldsResp resp = new CommentListAllFieldsResp();
	// resp.setTid(tid);
	// resp.setConmments(new ArrayList<CommentAllFieldsResp>());
	// return resp;
	// }
	// }
	//
	// @McpMethod(name = "comment.batch", ver = DEFAULT_VERSION, desc =
	// "批量查询主题下面的评论数量", auths = { DEFAULT_AUTHOR })
	// public CommentBatchResp batchComments(CommentBatchReq req, CommandContext
	// ctx) throws IOException {
	// String[] tids = req.getTid();
	// if (tids != null) {
	// CommentBatchResp batchComments = new CommentBatchResp();
	// List<CommentListResp> comments = new ArrayList<>(tids.length);
	// for (String tid : tids) {
	// CommentListResp commentListResp = new CommentListResp();
	// commentListResp.setRcount(commentService.getRcount(3, req.getTypeId(),
	// tid));
	// commentListResp.setTid(tid);
	// comments.add(commentListResp);
	// }
	// batchComments.setData(comments);
	// return batchComments;
	// }
	// return null;
	// }
	//
	// @McpMethod(name = "comment.batch.video", ver = DEFAULT_VERSION, desc =
	// "批量查询短视频的评论数量", auths = { DEFAULT_AUTHOR })
	// public CommentBatchVideoReq commentBatchVideo(CommentBatchVideoReq req) {
	// return commentService.commentCountByVideoIds(req);
	// }
	//
	// @McpMethod(name = "comment.count", ver = DEFAULT_VERSION, desc = "评论点赞",
	// auths = { DEFAULT_AUTHOR })
	// public CommonResultResp count(CommentCountReq req, CommandContext ctx)
	// throws IOException {
	//
	// String deviceId = ctx.getDeviceId();
	//
	// long count = commentService.count(ctx.getMcpAppInfo().getAppId(),
	// req.getTypeId(), req.getTid(), req.getCid(),
	// req.getType(), ctx.getUserInfo(), req.getSourceId(), deviceId,
	// ctx.getUserAgent());
	// // 点赞日志
	// logger.info(String.format("comment.count:[deviceId %s, count %d]",
	// deviceId, count));
	// if (count > 0) {
	// // 点赞且存在设备id记录明细
	// if (req.getType() == 1 && StringUtils.isNotBlank(deviceId)) {
	// commentService.addPraiserLog(ctx.getUserInfo(), deviceId, req.getTid(),
	// req.getCid(),
	// ctx.getMcpAppInfo().getAppId());
	// }
	// }
	// return CommonResultResp.SUCCESS;
	// }
	//
	// @McpMethod(name = "comment.user", ver = DEFAULT_VERSION, desc =
	// "用户发表的评论", auths = { DEFAULT_AUTHOR })
	// public CommentUserListResp userComments(CommentUserReq req,
	// CommandContext ctx)
	// throws IllegalArgumentException, IOException {
	//
	// boolean isLogin = false;
	// if (ctx.getUserId() > 0) {
	// isLogin = true;
	// }
	// return commentService.listUserComments(req.getUid(), isLogin,
	// req.getLastId(), req.getLen());
	// }
	//
	// @McpMethod(name = "comment.user.pc", ver = DEFAULT_VERSION, desc =
	// "用户发表的评论", logon = true, auths = {
	// DEFAULT_AUTHOR })
	// public CommentUserListPcResp userCommentsPc(BaseListReq req,
	// CommandContext ctx)
	// throws IllegalArgumentException, IOException {
	// CommentUserListPcResp resp = new CommentUserListPcResp();
	// long userId = ctx.getUserId();
	// List<CommentUserListPcResp.CommentUserPc> commentUserPcs =
	// commentService.listUserCommentsPc(userId,
	// req.getLastId(), req.getLen());
	// resp.setCommentUserPcs(commentUserPcs.toArray(new
	// CommentUserListPcResp.CommentUserPc[commentUserPcs.size()]));
	// return resp;
	// }
	//
	// @McpMethod(name = "comment.report", ver = DEFAULT_VERSION, desc = "举报评论",
	// auths = { DEFAULT_AUTHOR })
	// public CommonResultResp report(CommentReportReq req, CommandContext ctx)
	// throws IllegalArgumentException, IOException {
	// if (logger.isDebugEnabled()) {
	// logger.debug("comment.report type {}, comment {}", req.getType(),
	// req.getCid());
	// }
	// commentService.report(req.getTypeId(), ctx.getMcpAppInfo().getAppId(),
	// req.getType(), req.getTid(),
	// req.getCid());
	// return CommonResultResp.SUCCESS;
	// }
	//
	// @McpMethod(name = "comment.author.del", desc = "视频作者删除评论", logon = true,
	// ver = DEFAULT_VERSION, auths = {
	// DEFAULT_AUTHOR })
	// public CommonResultResp commentAuthorDel(CommentAuthorDelReq req,
	// CommandContext ctx) {
	// long userId = ctx.getUserId();
	// long videoId = req.getVideoId();
	// int appId = ctx.getMcpAppInfo().getAppId();
	// HotVideoDTO hotVideoDTO = fileService.queryEnabled(appId, videoId,
	// CdnType.QINIU, false);
	// if (hotVideoDTO == null) {
	// logger.warn("commentAuthorDel error hotVideo null {}", videoId);
	// return CommonResultResp.FAILED;
	// }
	//
	// if (hotVideoDTO.getUserId() != userId) {
	// logger.warn("commentAuthorDel error not author {}, {}, {}", videoId,
	// userId, hotVideoDTO.getUserId());
	// return CommonResultResp.FAILED;
	// }
	//
	// int typeId = CommentTyleEnum.SHORT_VIDEO.getTypeId();
	// long cid = req.getCid();
	// try {
	// CommentResp commentResp = commentService.get(typeId, cid);
	// commentService.del(appId, CommentTyleEnum.SHORT_VIDEO.getTypeId(),
	// req.getTid(), commentResp.getCid(),
	// commentResp.getUid());
	// return CommonResultResp.SUCCESS;
	// } catch (IOException e) {
	// logger.warn("commentAuthorDel error userId:{}, cid:{}", userId, cid);
	// }
	// return CommonResultResp.FAILED;
	// }

}
