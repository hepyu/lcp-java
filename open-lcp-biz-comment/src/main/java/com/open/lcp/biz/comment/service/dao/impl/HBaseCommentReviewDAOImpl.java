package com.open.lcp.biz.comment.service.dao.impl;

import org.apache.hadoop.hbase.util.Bytes;

import com.open.lcp.biz.comment.service.dao.HBaseCommentReviewDAO;

public class HBaseCommentReviewDAOImpl implements HBaseCommentReviewDAO {

	/**
	 * 待审核评论表（短视频的评论）
	 */
	private static final byte[] TABLE_COMMENT_REVIEW = Bytes.toBytes("commentReview");
}
