package com.open.lcp.biz.comment.service;

import java.util.List;

/**
 * @author Alex
 * @time 2017/1/6 15:35
 */
public interface CommentUserSilencedService {

    long saveCommentUserSilenced(CommentUserSilenceReq req);

    CommentUserSilencedEntity findCommentUserSilenced(CommentUserSilencedListReq req);

    List<CommentUserSilencedLogEntity> findLogByUserId(CommentUserSilencedLogReq req);
}
