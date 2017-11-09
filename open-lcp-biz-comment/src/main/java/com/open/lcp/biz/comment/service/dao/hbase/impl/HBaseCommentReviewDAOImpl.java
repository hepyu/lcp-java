package com.open.lcp.biz.comment.service.dao.hbase.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.open.lcp.biz.comment.service.dao.hbase.HBaseCommentReviewDAO;
import com.open.lcp.biz.comment.service.dao.hbase.impl.column.CommentCheckColumn;

public class HBaseCommentReviewDAOImpl extends HBaseAbstractDao implements HBaseCommentReviewDAO {

	private static final Logger logger = LoggerFactory.getLogger(HBaseCommentReviewDAOImpl.class);

	@Resource
	private Connection connection;

	/**
	 * 待审核评论表（短视频的评论）
	 */
	private static final byte[] TABLE_COMMENT_REVIEW = Bytes.toBytes("commentReview");

	@Override
	public boolean addReviewComments(int typeId, List<CommentCheckColumn> CommentColumns) {
		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT_REVIEW))) {
			return addCheckComments(table, CommentColumns);
		} catch (Exception e) {
			logger.warn("addReviewComments", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public boolean delReviewComments(int typeId, long... ids) {
		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT_REVIEW))) {
			return delCheckComments(table, ids);
		} catch (Exception e) {
			logger.error("delReviewComments", e);
			throw new RuntimeException(e.getMessage());
		}
	}

}
