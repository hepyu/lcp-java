package com.open.lcp.biz.comment.service.dao.hbase;

import java.io.IOException;
import java.util.List;

import com.open.lcp.biz.comment.service.dao.hbase.impl.column.CommentCheckColumn;

public interface HBaseCommentNoPassCommentDao {

	public boolean addCheckNoPassComments(int typeId, List<CommentCheckColumn> CommentColumns) throws IOException;

	public boolean delCheckNoPassComments(int typeId, long... ids) throws IllegalArgumentException, IOException;

}
