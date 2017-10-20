package com.open.lcp.biz.comment.service.dao.impl;

import org.apache.hadoop.hbase.util.Bytes;

import com.open.lcp.biz.comment.service.dao.HBaseCommentTitleDAO;

public class HBaseCommentTitleDAOImpl implements HBaseCommentTitleDAO {

	/**
	 * 评论表,family title
	 */
	private static final byte[] TABLE_COMMENT_TITLE = Bytes.toBytes("title");
}