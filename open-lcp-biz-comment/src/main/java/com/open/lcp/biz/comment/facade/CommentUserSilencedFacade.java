package com.open.lcp.biz.comment.facade;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.open.lcp.biz.comment.CommentConstant;
import com.open.lcp.biz.comment.facade.req.CommentUserSilencedListReq;
import com.open.lcp.biz.comment.facade.req.ForbidCommentUserReq;
import com.open.lcp.biz.comment.facade.resp.CommentUserSilencedLogResp;
import com.open.lcp.biz.comment.facade.resp.CommentUserSilencedResp;
import com.open.lcp.biz.comment.service.CommentUserSilencedService;
import com.open.lcp.biz.comment.service.dao.db.entity.CommentUserSilencedLogEntity;
import com.open.lcp.biz.passport.api.AccountInfoApi;
import com.open.lcp.core.api.annotation.LcpHttpMethod;
import com.open.lcp.core.api.command.CommandContext;
import com.open.lcp.core.api.facade.ApiFacade;
import com.open.lcp.core.api.info.BaseUserAccountInfo;
import com.open.lcp.core.framework.facade.CommonResultResp;

import javax.annotation.Resource;
import java.util.List;

@Component
public class CommentUserSilencedFacade implements ApiFacade {

//	@Resource
//	private CommentUserSilencedService commentUserSilencedService;
//	
//	@Resource
//	private AccountInfoApi accountInfoApi;
//
//	@LcpHttpMethod(name = "admin.comment.user.forbid", desc = "禁言用户", ver = "1.0", auths = { "" }) // admin.comment.user.relive
//	public CommonResultResp forbidCommentUser(ForbidCommentUserReq req, CommandContext ctx) {
//		long current = System.currentTimeMillis();
//		req.setStart(current);
//		req.setCtime(current);
//		int silencedDays = req.getSilencedDays();
//		long end = silencedDays;
//		long userId = req.getUserId();
//		
//			if (silencedDays > 0) {
//				end = current + CommentConstant.ONE_DAY_MSEC * req.getSilencedDays();
//			} else if (silencedDays == -1) {
//				end = CommentConstant.FOR_EVER;
//			}
//			req.setEnd(end);
//			BaseUserAccountInfo userInfo = accountInfoApi.getu.getUserAccountInfo(req.getUserId());
//			if (userInfo == null || StringUtils.isBlank(userInfo.getNickName())) {
//				req.setNickName(String.valueOf(userId));
//			} else {
//				req.setNickName(userInfo.getNickName());
//			}
//		long result = commentUserSilencedService.gagCommentUser(silencedDays);
//		return result > 0 ? CommonResultResp.SUCCESS : CommonResultResp.FAILED;
//	}
//
//	@LcpHttpMethod(name = "admin.comment.user.relive", desc = "解除用户禁言", ver = "1.0", auths = { "" }) // admin.comment.user.relive
//	public CommonResultResp reliveCommentUser(ForbidCommentUserReq req, CommandContext ctx) {
//		commentUserSilencedDAO.deleteByUserId(userId);
//		return CommonResultResp.SUCCESS;
//	}
//
//	@LcpHttpMethod(name = "admin.comment.user.silenced.list", desc = "查询被禁言的用户", ver = "1.0", auths = { "" })
//	public CommentUserSilencedResp commentUserSilencedList(CommentUserSilencedListReq req) {
//		return commentUserSilencedService.findCommentUserSilenced(req);
//	}
//
//	@LcpHttpMethod(name = "admin.comment.user.silenced.log", desc = "查询用户的禁言记录", ver = "1.0", auths = { "" })
//	public CommentUserSilencedLogResp commentUserSilencedLog(CommentUserSilencedLogReq req) {
//		List<CommentUserSilencedLogEntity> logs = commentUserSilencedService.findLogByUserId(req);
//		CommentUserSilencedLogResp resp = new CommentUserSilencedLogResp();
//		if (!CollectionUtils.isEmpty(logs)) {
//			resp.setCommentUserSilencedLogs(logs.toArray(new CommentUserSilencedLogEntity[0]));
//		} else {
//			resp.setCommentUserSilencedLogs(new CommentUserSilencedLogEntity[0]);
//		}
//		return resp;
//	}
}
