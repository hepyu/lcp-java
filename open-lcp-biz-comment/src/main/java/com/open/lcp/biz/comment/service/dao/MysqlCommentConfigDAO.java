package com.open.lcp.biz.comment.service.dao;

import java.util.List;

import com.open.lcp.biz.comment.service.dao.entity.CommentConfigEntity;
import com.open.lcp.orm.jade.annotation.DAO;
import com.open.lcp.orm.jade.annotation.SQL;

@DAO(catalog = "lcp_biz_comment")
public interface MysqlCommentConfigDAO {

	@SQL("select app_id,app_comment_id,type,level,floor_level from comment_config")
	List<CommentConfigEntity> listCommentConfig();

	@SQL("insert into comment_config value(:1.appId,:1.appCommentId,:1.type,:1.level,:1.floorLevel,:1.addTime) on duplicate key update app_comment_id=:1.appCommentId,type=:1.type,level=:1.level,floor_level=:1.floorLevel")
	int saveCommentConfig(CommentConfigEntity commentConfig);

}
