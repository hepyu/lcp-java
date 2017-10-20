package com.open.lcp.biz.comment.service.dao.impl;

import org.apache.hadoop.hbase.util.Bytes;

import com.open.lcp.biz.comment.service.dao.HBaseCommentNoPassDAO;

public class HBaseCommentNoPassDAOImpl implements HBaseCommentNoPassDAO {

	/**
	 * 审核不通过评论表（短视频的评论）
	 */
	private static final byte[] TABLE_COMMENT_NOPASS = Bytes.toBytes("commentNoPass");
}
