package com.open.lcp.biz.comment.service.dao.impl;

import org.apache.hadoop.hbase.util.Bytes;

import com.open.lcp.biz.comment.service.dao.HBaseCommentAIPassResourceDAO;

public class HBaseCommentAIPassResourceDAOImpl implements HBaseCommentAIPassResourceDAO {
	/**
	 * AI审核通过评论表 给评论复审用（PC下载资源的评论）
	 */
	private static final byte[] TABLE_COMMENT_AI_PASS_RESOURCE = Bytes.toBytes("commentAIPassResource");
}
