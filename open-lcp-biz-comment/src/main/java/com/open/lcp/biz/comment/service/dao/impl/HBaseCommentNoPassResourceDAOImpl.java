package com.open.lcp.biz.comment.service.dao.impl;

import org.apache.hadoop.hbase.util.Bytes;

import com.open.lcp.biz.comment.service.dao.HBaseCommentNoPassResourceDAO;

public class HBaseCommentNoPassResourceDAOImpl implements HBaseCommentNoPassResourceDAO {

	/**
	 * 审核不通过评论表（PC下载资源的评论）
	 */
	private static final byte[] TABLE_COMMENT_NOPASS_RESOURCE = Bytes.toBytes("commentNoPassResource");
}
