package com.open.lcp.biz.comment.service.dao.hbase;

import java.util.List;

import com.open.lcp.biz.comment.service.dao.hbase.impl.column.CommentCheckColumn;

public interface HBaseCommentReviewDAO {

	public boolean addReviewComments(int typeId, List<CommentCheckColumn> CommentColumns);

	public boolean delReviewComments(int typeId, long... ids);
}
