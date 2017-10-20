package com.open.lcp.biz.comment.service.dao.impl;

import org.apache.hadoop.hbase.util.Bytes;

import com.open.lcp.biz.comment.service.dao.HBaseCommentReviewResourceDAO;

public class HBaseCommentReviewResourceDAOImpl implements HBaseCommentReviewResourceDAO {

	/**
	 * 待审核评论表（PC下载资源的评论）
	 */
	private static final byte[] TABLE_COMMENT_REVIEW_RESOURCE = Bytes.toBytes("commentReviewResource");
}
