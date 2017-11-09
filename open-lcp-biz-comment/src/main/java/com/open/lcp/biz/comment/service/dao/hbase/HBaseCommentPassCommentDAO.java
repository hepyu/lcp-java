package com.open.lcp.biz.comment.service.dao.hbase;

import java.io.IOException;

public interface HBaseCommentPassCommentDAO {

	public boolean delCheckPassComments(int typeId, long... ids) throws IllegalArgumentException, IOException;
}
