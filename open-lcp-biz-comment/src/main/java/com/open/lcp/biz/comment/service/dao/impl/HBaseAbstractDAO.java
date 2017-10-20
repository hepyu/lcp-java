package com.open.lcp.biz.comment.service.dao.impl;

import org.apache.hadoop.hbase.util.Bytes;

import com.open.lcp.biz.comment.util.BytesUtil;

public abstract class HBaseAbstractDAO {

	/**
	 * 解析成 hbase comment表里的rowId
	 */
	protected byte[] parseCommentRowId(int appId, int typeId, String tid) {

		return BytesUtil.spliceBytes(Bytes.toBytes(appId), Bytes.toBytes(typeId), Bytes.toBytes(tid));
	}
}
