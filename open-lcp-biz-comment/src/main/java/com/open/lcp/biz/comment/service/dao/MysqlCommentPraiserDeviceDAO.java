package com.open.lcp.biz.comment.service.dao;

import com.open.lcp.biz.comment.service.dao.entity.CommentPraiserDeviceEntity;
import com.open.lcp.orm.jade.annotation.DAO;
import com.open.lcp.orm.jade.annotation.SQL;

@DAO(catalog = "lcp_biz_comment")
public interface MysqlCommentPraiserDeviceDAO {

	@SQL("insert into comment_praiser_device (device_id, tid, cid, type_id, app_id, ctime) values (:1.deviceId, :1.tid, :1.cid, :1.typeId, :1.appId, :1.time) ON DUPLICATE KEY UPDATE ctime=:1.time")
	int save(CommentPraiserDeviceEntity praiserDeviceDetail);

	@SQL("select count(1) from comment_praiser_device where device_id=:1")
	int countByDeviceId(String deviceId);

	@SQL("delete from comment_praiser_device where device_id=:1")
	int deleteByDeviceId(String deviceId);

	@SQL("select cid from comment_praiser_device where device_id=:1")
	long[] queryAll(String deviceId);
}
