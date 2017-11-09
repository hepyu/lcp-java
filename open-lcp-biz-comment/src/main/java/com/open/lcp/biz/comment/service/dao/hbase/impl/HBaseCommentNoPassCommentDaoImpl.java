package com.open.lcp.biz.comment.service.dao.hbase.impl;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.nutz.dao.entity.annotation.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.open.lcp.biz.comment.service.dao.hbase.HBaseCommentNoPassCommentDao;
import com.open.lcp.biz.comment.service.dao.hbase.impl.column.CommentCheckColumn;

@Repository
public class HBaseCommentNoPassCommentDaoImpl extends HBaseAbstractDao implements HBaseCommentNoPassCommentDao {

	private static final Logger logger = LoggerFactory.getLogger(HBaseCommentNoPassCommentDaoImpl.class);

	@Resource
	private Connection connection;

	/**
	 * 审核不通过评论表（短视频的评论）
	 */
	private static final byte[] TABLE_COMMENT_NOPASS = Bytes.toBytes("commentNoPass");

	@Override
	public boolean addCheckNoPassComments(int typeId, List<CommentCheckColumn> CommentColumns) throws IOException {
		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT_NOPASS))) {
			return addCheckComments(table, CommentColumns);
		} catch (Exception e) {
			logger.error("addCheckNoPassComments", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public boolean delCheckNoPassComments(int typeId, long... ids) throws IllegalArgumentException, IOException {
		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT_NOPASS))) {
			return delCheckComments(table, ids);
		} catch (Exception e) {
			logger.error("delCheckPassComments", e);
			throw new RuntimeException(e.getMessage());
		}

	}

}
