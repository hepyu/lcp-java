package com.open.lcp.biz.comment.service.dao.db;

import com.open.lcp.biz.comment.service.dao.db.entity.CommentUserSilencedEntity;
import com.open.lcp.orm.jade.annotation.DAO;
import com.open.lcp.orm.jade.annotation.SQL;

import java.util.List;

@DAO(catalog = "lcp_biz_comment")
public interface MysqlCommentUserSilencedDAO {

	@SQL("insert into comment_user_silenced (user_id, nick_name, reason, start, end, operator, ctime) values (:1.userId, :1.nickName, :1.reason, :1.start, :1.end, :1.operator, :1.ctime) on duplicate key update nick_name=:1.nickName, reason=:1.reason, start=:1.start, end=:1.end, operator=:1.operator, ctime=:1.ctime")
	long save(CommentUserSilencedEntity entity);

	@SQL("delete from comment_user_silenced where user_id=:1")
	int deleteByUserId(long userId);

	@SQL("select user_id, nick_name, end, operator, ctime, operate_time from comment_user_silenced where instr(operator, :1)>0 and end>:2 limit :3, :4")
	List<CommentUserSilencedEntity> findByOperator(String operator, long current, int offset, int len);

	@SQL("select user_id, nick_name, end, operator, ctime, operate_time from comment_user_silenced where instr(nick_name, :1)>0 and end>:2 limit :3, :4")
	List<CommentUserSilencedEntity> findByNickName(String nickName, long current, int offset, int len);

	@SQL("select user_id, nick_name, end, operator, ctime, operate_time from comment_user_silenced where user_id=:1 and end>:2 limit :3, :4")
	List<CommentUserSilencedEntity> findByUserId(long userId, long current, int offset, int len);

	@SQL("select user_id, nick_name, end, operator, ctime, operate_time from comment_user_silenced where end>:1 limit :2, :3")
	List<CommentUserSilencedEntity> findAll(long current, int offset, int pageSize);
}
