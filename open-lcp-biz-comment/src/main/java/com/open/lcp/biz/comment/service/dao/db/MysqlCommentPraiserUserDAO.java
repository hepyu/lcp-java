package com.open.lcp.biz.comment.service.dao.db;

import com.open.lcp.biz.comment.service.dao.db.entity.CommentPraiserUserEntity;
import com.open.lcp.core.env.LcpResource;
import com.open.lcp.orm.jade.annotation.DAO;
import com.open.lcp.orm.jade.annotation.SQL;

@DAO(catalog = LcpResource.dbAnnotationName_lcp_mysql_biz_comment_master)
public interface MysqlCommentPraiserUserDAO {

	@SQL("insert into comment_praiser_user (user_id, tid, cid, type_id, app_id, ctime) values (:1.userId, :1.tid, :1.cid, :1.typeId, :1.appId, :1.time) ON DUPLICATE KEY UPDATE ctime=:1.time")
	int save(CommentPraiserUserEntity praiserUserDetail);

	@SQL("insert into comment_praiser_user (user_id, tid, cid, type_id, app_id, ctime, from_device, trans_time) select :1, d.tid, d.cid, d.type_id, d.app_id, d.ctime, :2, d.ctime from comment_praiser_device d where device_id=:2 ON DUPLICATE KEY UPDATE USER_ID=:1")
	int saveFromDeviceDetail(long userId, String deviceId);

}
