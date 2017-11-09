package com.open.lcp.biz.comment.service.dao.hbase.impl;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.open.lcp.biz.comment.service.dao.hbase.HBaseCommentPassCommentDAO;

@Repository
public class HBaseCommentPassCommentDAOImpl extends HBaseAbstractDao implements HBaseCommentPassCommentDAO {

	private static final Logger logger = LoggerFactory.getLogger(HBaseCommentPassCommentDAOImpl.class);

	/**
	 * 审核通过评论表（短视频的评论）
	 */
	private static final byte[] TABLE_COMMENT_PASS = Bytes.toBytes("commentPass");

	@Resource
	private Connection connection;

	@Override
	public boolean delCheckPassComments(int typeId, long... ids) throws IllegalArgumentException, IOException {
		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT_PASS))) {
			return delCheckComments(table, ids);
		} catch (Exception e) {
			logger.error("delCheckPassComments", e);
			throw new RuntimeException(e.getMessage());
		}

	}
}
