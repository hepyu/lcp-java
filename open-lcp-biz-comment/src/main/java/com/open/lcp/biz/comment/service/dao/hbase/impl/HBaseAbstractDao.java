package com.open.lcp.biz.comment.service.dao.hbase.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.open.lcp.biz.comment.service.dao.hbase.impl.column.CommentCheckColumn;
import com.open.lcp.biz.comment.util.BytesUtil;

public abstract class HBaseAbstractDao {

	/**
	 * 解析成 hbase comment表里的rowId
	 */
	protected byte[] parseCommentRowId(int appId, int typeId, String tid) {

		return BytesUtil.spliceBytes(Bytes.toBytes(appId), Bytes.toBytes(typeId), Bytes.toBytes(tid));
	}

	protected boolean addCheckComments(Table table, List<CommentCheckColumn> commentColumns) throws IOException {
		List<Put> puts = new ArrayList<>(commentColumns.size());
		byte[] family = Bytes.toBytes("comment");
		byte[] comment = Bytes.toBytes("comment");
		byte[] user = Bytes.toBytes("user");
		byte[] id = Bytes.toBytes("id");
		byte[] count = Bytes.toBytes("count");
		byte[] ext = Bytes.toBytes("ext");
		byte[] author = Bytes.toBytes("author");
		byte[] authorId = Bytes.toBytes("authorId");
		byte[] content = Bytes.toBytes("content");

		for (CommentCheckColumn commentColumn : commentColumns) {
			Put put = new Put(Bytes.toBytes(commentColumn.getCommentId()));
			put.addColumn(family, id, Bytes.toBytes(commentColumn.getIdColumnValue()));
			put.addColumn(family, user, Bytes.toBytes(commentColumn.getUserColumnValue()));
			put.addColumn(family, comment, Bytes.toBytes(commentColumn.getCommentColumnValue()));
			put.addColumn(family, count, Bytes.toBytes(commentColumn.getCountColumnValue()));
			put.addColumn(family, ext, Bytes.toBytes(commentColumn.getExtColumnValue()));
			if (StringUtils.isNotBlank(commentColumn.getAuthor())) {
				put.addColumn(family, author, Bytes.toBytes(commentColumn.getAuthor()));
			}
			put.addColumn(family, authorId, Bytes.toBytes(commentColumn.getAuthorId()));
			if (StringUtils.isNotBlank(commentColumn.getContent())) {
				put.addColumn(family, content, Bytes.toBytes(commentColumn.getContent()));
			}
			if (StringUtils.isNotBlank(commentColumn.getChecker())) {
				put.addColumn(family, Bytes.toBytes("checker"), Bytes.toBytes(commentColumn.getChecker()));
			}
			if (commentColumn.getCheckTime() != 0) {
				put.addColumn(family, Bytes.toBytes("checkTime"), Bytes.toBytes(commentColumn.getCheckTime()));
			}
			puts.add(put);
		}
		table.put(puts);
		return true;
	}

	protected boolean delCheckComments(Table table, long... ids) throws IOException {
		List<Delete> dels = new ArrayList<>(ids.length);
		for (long id : ids) {
			Delete del = new Delete(Bytes.toBytes(id));
			dels.add(del);
		}
		table.delete(dels);
		return true;
	}
}
