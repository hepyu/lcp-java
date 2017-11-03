package com.open.lcp.biz.comment.service.dao.hbase.impl;

import org.apache.hadoop.hbase.util.Bytes;

import com.open.lcp.biz.comment.service.dao.hbase.HBaseCommentAIPassDAO;

public class HBaseCommentAIPassDAOImpl implements HBaseCommentAIPassDAO {

	/**
	 * AI审核通过评论表 给评论复审用（短视频的评论）
	 */
	private static final byte[] TABLE_COMMENT_AI_PASS = Bytes.toBytes("commentAIPass");
}
