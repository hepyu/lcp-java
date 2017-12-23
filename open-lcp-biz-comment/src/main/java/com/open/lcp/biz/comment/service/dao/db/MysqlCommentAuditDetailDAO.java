package com.open.lcp.biz.comment.service.dao.db;

import java.util.List;

import com.open.lcp.biz.comment.service.dao.db.entity.CommentAuditDetailEntity;
import com.open.lcp.biz.comment.service.dao.db.entity.CommentAuditDetailStat;
import com.open.lcp.core.env.LcpResource;
import com.open.lcp.orm.jade.annotation.DAO;
import com.open.lcp.orm.jade.annotation.SQL;

@DAO(catalog = LcpResource.dbAnnotationName_lcp_mysql_biz_comment_master)
public interface MysqlCommentAuditDetailDAO {

	@SQL("insert into comment_audit_detail(comment_id,comment_checker,comment_check_time,comment_ckeck_type) value (:1.commentId,:1.commentChecker,:1.checkTime,:1.checkType)")
	public int batchInsertCommentAuditDetail(List<CommentAuditDetailEntity> entityList);

	@SQL("select comment_checker,count(comment_checker) as check_count, count(case when comment_ckeck_type='Pass' then 1 end) as pass_count, count(case when comment_ckeck_type='NoPass' then 1 end) as nopass_count, count(case when comment_ckeck_type='OnLine' then 1 end) as online_count, count(case when comment_ckeck_type='OffLine' then 1 end) as offline_count from comment_audit_detail where (:1 is null or comment_checker=:1) and comment_check_time between :2 and :3 group by comment_checker")
	List<CommentAuditDetailStat> listCommentDetail(String checker, long startTime, long endTime);
}
