package com.open.lcp.biz.comment.facade;

import com.open.lcp.core.api.annotation.LcpHttpMethod;
import com.open.lcp.core.api.facade.ApiFacade;
import com.open.lcp.core.framework.facade.CommonResultResp;

public class AdminCommentFacade implements ApiFacade {

	// @McpMethod(name = "admin.comment.del", ver = DEFAULT_VERSION, desc =
	// "通过req指定userId删除评论", auths = {
	// DEFAULT_AUTHOR })
	// public CommonResultResp delByReqUser(CommentDelReq req, CommandContext
	// ctx) throws IOException {
	// if (req.getUserId() <= 0) {
	// return CommonResultResp.FAILED;
	// }
	// if (commentService.del(ctx.getMcpAppInfo().getAppId(), req.getTypeId(),
	// req.getTid(), req.getCid(),
	// req.getUserId())) {
	// return CommonResultResp.SUCCESS;
	// }
	// return CommonResultResp.FAILED;
	// }
	//
//	@LcpHttpMethod(name = "admin.comment.set", ver = 1.0, desc = "配置应用评论参数", auths = { "hpy" })
//	public CommonResultResp setConf(CommentConfigReq req, CommandContext ctx) throws IOException {
//
//		if (commentService.setConf(ctx.getMcpAppInfo().getAppId(), req)) {
//			return CommonResultResp.SUCCESS;
//		}
//		return CommonResultResp.FAILED;
//	}
	//
	// @McpMethod(name = "admin.comment.get", ver = DEFAULT_VERSION, desc =
	// "查询评论详情", auths = { DEFAULT_AUTHOR })
	// public CommentResp get(CommentGetReq req, CommandContext ctx) throws
	// IOException {
	// return commentService.get(req.getTypeId(), req.getCid());
	// }
	//
	// @McpMethod(name = "admin.comment.add", ver = DEFAULT_VERSION, desc =
	// "运营后台增加评论，传uid指定用户", auths = {
	// DEFAULT_AUTHOR })
	// public CommentAddResp adminCommentAdd(CommentAdminAddReq req,
	// CommandContext ctx) throws IOException {
	// UserInfo user = userService.getUserInfo(req.getUid());
	// if (user == null) {
	// CommentAddResp emptyUser = new CommentAddResp();
	// emptyUser.setResult(4011);
	// return emptyUser;
	// }
	// int appId = ctx.getMcpAppInfo().getAppId();
	// int typeId = req.getTypeId();
	// String tid = req.getTid();
	// Long cid = req.getCid();
	// String comment = req.getComment();
	// String sourceId = req.getSourceId();
	// String device = req.getDevice();
	// String ip = req.getClientIp();
	// String triggerId = req.getTriggerId();
	// String clientPort = req.getClientPort();
	// String downLoadSpeed = req.getDownLoadSpeed();
	// boolean anonymous = req.isAnonymous();
	// String extParams = req.getExtParams();
	// String exeParamsJson = CommentUtil.transformExtParams(extParams,
	// ctx.getStringParams());
	// CommentAddResp resp = commentService.addComment(appId, typeId, tid, cid,
	// ip, device, comment, user, sourceId,
	// triggerId, clientPort, "yyht", downLoadSpeed, anonymous,
	// req.getBandwidth(), exeParamsJson);
	// // 管理后台发的评论自动审核通过
	// if (resp != null && resp.getCid() != null && resp.getCid() > 0)
	// commentService.checkComments(typeId, 6, "auto", resp.getCid());
	// return resp;
	// }

}
