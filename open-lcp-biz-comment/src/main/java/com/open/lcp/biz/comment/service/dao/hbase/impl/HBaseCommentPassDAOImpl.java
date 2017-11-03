package com.open.lcp.biz.comment.service.dao.hbase.impl;

import org.apache.hadoop.hbase.util.Bytes;

import com.open.lcp.biz.comment.service.dao.hbase.HBaseCommentPassDAO;

public class HBaseCommentPassDAOImpl implements HBaseCommentPassDAO {

	/**
	 * 审核通过评论表（短视频的评论）
	 */
	private static final byte[] TABLE_COMMENT_PASS = Bytes.toBytes("commentPass");
}
