package com.open.lcp.biz.comment.service.dao.impl;

import org.apache.hadoop.hbase.util.Bytes;

import com.open.lcp.biz.comment.service.dao.HBaseCommentPassResourceDAO;

public class HBaseCommentPassResourceDAOImpl implements HBaseCommentPassResourceDAO {

	/**
	 * 审核通过评论表（PC下载资源的评论）
	 */
	private static final byte[] TABLE_COMMENT_PASS_RESOURCE = Bytes.toBytes("commentPassResource");
}
