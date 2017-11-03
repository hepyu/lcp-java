package com.open.lcp.biz.comment.service.dao.db;

import java.util.List;

import com.open.lcp.biz.comment.service.dao.db.entity.CommentUserSilencedLogEntity;
import com.open.lcp.orm.jade.annotation.DAO;
import com.open.lcp.orm.jade.annotation.SQL;

/**
 */
@DAO(catalog = "lcp_biz_comment")
public interface MysqlCommentUserSilencedLogDAO {

	@SQL("insert into comment_user_silenced_log (user_id, silenced_days, reason, start, end, operator, ctime) values (:1.userId, :1.silencedDays, :1.reason, :1.start, :1.end, :1.operator, :1.ctime)")
	long save(CommentUserSilencedLogEntity req);

	@SQL("select comment_user_silenced_log_id, user_id, silenced_days, reason, start, end, operator, ctime from comment_user_silenced_log where user_id=:1 limit :2, :3")
	List<CommentUserSilencedLogEntity> findByUserId(long userId, int offset, int pageSize);
}
