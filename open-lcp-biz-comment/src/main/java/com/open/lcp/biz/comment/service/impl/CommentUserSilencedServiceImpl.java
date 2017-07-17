package com.open.lcp.biz.comment.service.impl;

import com.bchbc.dbs.cache.SSDBX;
import com.xunlei.mcp.model.UserInfo;
import com.xunlei.xlmc.comment.dao.CommentUserSilencedDAO;
import com.xunlei.xlmc.comment.dao.CommentUserSilencedLogDAO;
import com.xunlei.xlmc.comment.domain.CommentUserSilencedDTO;
import com.xunlei.xlmc.comment.domain.CommentUserSilencedLog;
import com.xunlei.xlmc.comment.facade.req.CommentUserSilenceReq;
import com.xunlei.xlmc.comment.facade.req.CommentUserSilencedListReq;
import com.xunlei.xlmc.comment.facade.req.CommentUserSilencedLogReq;
import com.xunlei.xlmc.comment.facade.resp.CommentUserSilencedResp;
import com.xunlei.xlmc.comment.service.CommentUserSilencedService;
import com.xunlei.xlmc.useraccount.service.UserAccountService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

import static com.xunlei.xlmc.comment.config.CommentSSDBConfig.SSDB_COMMENT_NEW;
import static com.xunlei.xlmc.comment.util.CommentConstant.COMMENT_SILENCED_KEY;
import static com.xunlei.xlmc.comment.util.CommentConstant.FOR_EVER;
import static com.xunlei.xlmc.comment.util.CommentConstant.ONE_DAY_MSEC;

/**
 * @author Alex
 * @time 2017/1/6 15:36
 */
@Service
public class CommentUserSilencedServiceImpl implements CommentUserSilencedService {

    private static final Logger logger = LoggerFactory.getLogger(CommentUserSilencedServiceImpl.class);

    @Resource
    private CommentUserSilencedDAO commentUserSilencedDAO;
    @Resource
    private CommentUserSilencedLogDAO commentUserSilencedLogDAO;
    @Resource
    private UserAccountService userAccountService;
    @Resource(name = SSDB_COMMENT_NEW)
    SSDBX ssdbx;

    public long saveCommentUserSilenced(CommentUserSilenceReq req) {
        long current = System.currentTimeMillis();
        req.setStart(current);
        req.setCtime(current);
        int silencedDays = req.getSilencedDays();
        long end = silencedDays;
        long userId = req.getUserId();
        if (silencedDays == 0) {
            commentUserSilencedDAO.deleteByUserId(userId);
        } else {
            if (silencedDays > 0) {
                end = current + ONE_DAY_MSEC * req.getSilencedDays();
            } else if (silencedDays == -1) {
                end = FOR_EVER;
            }
            req.setEnd(end);
            UserInfo userInfo = userAccountService.getUserInfo(req.getUserId());
            if (userInfo == null || StringUtils.isBlank(userInfo.getNickName())) {
                req.setNickName(String.valueOf(userId));
            } else {
                req.setNickName(userInfo.getNickName());
            }
            commentUserSilencedDAO.save(req);
        }
        updateCache(userId, end);
        return commentUserSilencedLogDAO.save(req);
    }

    private void updateCache(long userId, long end) {
        try {
            String silencedKey = String.format(COMMENT_SILENCED_KEY, userId);
            if (end == 0) {
                ssdbx.del(silencedKey);
            } else {
                ssdbx.set(silencedKey, end);
                if (end != FOR_EVER) {
                    ssdbx.expired(silencedKey, (end - System.currentTimeMillis()) / 1000);
                }
            }
        } catch (Exception e) {
            logger.warn("comment user silenced error userId {}, end {}", userId, end, e);
        }
    }

    @Override
    public CommentUserSilencedEntity findCommentUserSilenced(CommentUserSilencedListReq req) {

        List<CommentUserSilencedEntity> commentUserSilenceds;
        int offset = (req.getPageIndex() - 1) * req.getPageSize();
        long current = System.currentTimeMillis();
        if (StringUtils.isNotBlank(req.getOperator())) {
            commentUserSilenceds = commentUserSilencedDAO.findByOperator(req.getOperator(), current, offset, req.getPageSize());
        } else if (StringUtils.isNotBlank(req.getNickName())) {
            commentUserSilenceds = commentUserSilencedDAO.findByNickName(req.getNickName(), current, offset, req.getPageSize());
        } else if (req.getUserId() > 0) {
            commentUserSilenceds = commentUserSilencedDAO.findByUserId(req.getUserId(), current, offset, req.getPageSize());
        } else {
            commentUserSilenceds = commentUserSilencedDAO.findAll(current, offset, req.getPageSize());
        }
        CommentUserSilencedEntity commentUserSilencedResp = new CommentUserSilencedEntity();
        if (!CollectionUtils.isEmpty(commentUserSilenceds)) {
            for (CommentUserSilencedEntity commentUserSilencedDTO : commentUserSilenceds) {
                long end = commentUserSilencedDTO.getEnd();
                if (end == FOR_EVER) {
                    commentUserSilencedDTO.setSilencedType(-1);
                } else {
                    commentUserSilencedDTO.setSilencedType(1);
                }
                UserInfo userInfo = userAccountService.getUserInfo(commentUserSilencedDTO.getUserId());
                if (userInfo != null) {
                    commentUserSilencedDTO.setPortrait(userInfo.getPortrait());
                }
            }
            commentUserSilencedResp.setCommentUserSilenceds(commentUserSilenceds.toArray(new CommentUserSilencedEntity[0]));
        } else {
            commentUserSilencedResp.setCommentUserSilenceds(new CommentUserSilencedEntity[0]);
        }
        return commentUserSilencedResp;
    }

    public List<CommentUserSilencedLogEntity> findLogByUserId(CommentUserSilencedLogReq req) {
        int pageIndex = req.getPageIndex();
        int pageSize = req.getPageSize();
        if (pageIndex == 0) {
            pageIndex = 1;
        }
        if (pageSize == 0) {
            pageSize = 20;
        }
        int offset = (pageIndex - 1) * pageSize;
        return commentUserSilencedLogDAO.findByUserId(req.getUserId(), offset, pageSize);
    }
}
