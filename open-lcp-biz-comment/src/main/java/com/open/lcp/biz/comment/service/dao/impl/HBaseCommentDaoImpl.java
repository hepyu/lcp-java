package com.open.lcp.biz.comment.service.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.open.lcp.biz.comment.service.dao.HBaseCommentDAO;
import com.open.lcp.biz.comment.util.BytesUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository("commentDao")
public class HBaseCommentDaoImpl extends HBaseAbstractDAO implements HBaseCommentDAO {

	private static final Logger logger = LoggerFactory.getLogger(HBaseCommentDaoImpl.class);
	/**
	 * 评论表
	 */
	private static final byte[] TABLE_COMMENT = Bytes.toBytes("comment");
	
	/**
	 * 评论表,family comment
	 */
	private static final byte[] TABLE_COMMENT_COMMENT = Bytes.toBytes("comment");


	@Resource
	private Connection connection;

//
//	/**
//	 * 解析成 hbase commentReply表里的rowId
//	 */
//	private byte[] parseCommentRowId(int appId, int typeId, String tid, long cid) {
//
//		return BytesUtil.spliceBytes(Bytes.toBytes(appId), Bytes.toBytes(typeId), Bytes.toBytes(tid),
//				Bytes.toBytes(cid));
//	}
//
//	@Override
//	public boolean addComment(int appId, int typeId, String tid, long cid, String commentId, String comment,
//			String user, String count, String ext) throws IOException {
//
//		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT))) {
//			byte[] rowId = parseCommentRowId(appId, typeId, tid);
//			return addComment(table, rowId, cid, commentId, comment, user, count, ext);
//		} catch (Exception e) {
//			logger.error("addComment", e);
//			throw new RuntimeException(e.getMessage());
//		}
//
//	}
//
//	private boolean addComment(Table table, byte[] rowId, long cid, String commentId, String comment, String user,
//			String count, String ext) throws IOException {
//
//		Put put = new Put(rowId);
//		byte[] commentIdBytes = Bytes.toBytes(cid);
//
//		int newComment = 0;
//		if (StringUtils.isNotBlank(commentId)) {
//			byte[] commentIdValue = Bytes.toBytes(commentId);
//			put.addColumn(TABLE_COMMENT_COMMENT, commentIdBytes, commentIdValue);
//			newComment++;
//		}
//
//		if (StringUtils.isNotBlank(comment)) {
//			byte[] commentColumn = BytesUtil.spliceBytes(commentIdBytes, Bytes.toBytes("comment"));
//			byte[] commentValue = Bytes.toBytes(comment);
//			put.addColumn(TABLE_COMMENT_COMMENT, commentColumn, commentValue);
//			newComment++;
//		}
//
//		if (StringUtils.isNotBlank(user)) {
//			byte[] userColumn = BytesUtil.spliceBytes(commentIdBytes, Bytes.toBytes("user"));
//			byte[] userValue = Bytes.toBytes(user);
//			put.addColumn(TABLE_COMMENT_COMMENT, userColumn, userValue);
//			newComment++;
//		}
//
//		if (StringUtils.isNotBlank(count)) {
//			byte[] countColumn = BytesUtil.spliceBytes(commentIdBytes, Bytes.toBytes("count"));
//			byte[] countValue = Bytes.toBytes(count);
//			put.addColumn(TABLE_COMMENT_COMMENT, countColumn, countValue);
//			newComment++;
//		}
//
//		if (StringUtils.isNotBlank(ext)) {
//			byte[] extColumn = BytesUtil.spliceBytes(commentIdBytes, Bytes.toBytes("ext"));
//			byte[] extValue = Bytes.toBytes(ext);
//			put.addColumn(TABLE_COMMENT_COMMENT, extColumn, extValue);
//			newComment++;
//		}
//
//		table.put(put);
//		if (newComment == 5) {
//			table.incrementColumnValue(rowId, TABLE_COMMENT_TITLE, Bytes.toBytes("rcount"), 1);
//		}
//		return true;
//	}
//
//	@Override
//	public List<CommentColumn> listComment(int appId, int typeId, String tid, long lastId, int len) throws IOException {
//
//		byte[] rowId = parseCommentRowId(appId, typeId, tid);
//		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT))) {
//			Get get = new Get(rowId);
//			get.addFamily(TABLE_COMMENT_COMMENT);
//			lastId++;
//			get.setFilter(new ColumnPaginationFilter(len, Bytes.toBytes(lastId)));
//			Result result = table.get(get);
//			List<Cell> cells = result.listCells();
//			if (cells == null || cells.size() < 1) {
//				return null;
//			}
//			List<CommentColumn> commentColumns = new ArrayList<>(len);
//			for (int i = 0; i < cells.size();) {
//				int fromIndex = i;
//				int toIndex = i + 5;
//				if (toIndex > cells.size()) {
//					toIndex = cells.size();
//				}
//				List<Cell> subCells = cells.subList(fromIndex, toIndex);
//				CommentColumn comment = new CommentColumn();
//				for (Cell cell : subCells) {
//					if (cell.getQualifierLength() <= 8) {
//						long commentId = Bytes.toLong(CellUtil.cloneQualifier(cell));
//						comment.setCommentId(commentId);
//						comment.setIdColumnValue(Bytes.toString(CellUtil.cloneValue(cell)));
//					} else {
//						byte[] qualifierBytes = CellUtil.cloneQualifier(cell);
//						String qualifier = Bytes.toString(qualifierBytes, 8, qualifierBytes.length - 8);
//						if ("comment".equals(qualifier)) {
//							comment.setCommentColumnValue(Bytes.toString(CellUtil.cloneValue(cell)));
//						} else if ("user".equals(qualifier)) {
//							comment.setUserColumnValue(Bytes.toString(CellUtil.cloneValue(cell)));
//						} else if ("count".equals(qualifier)) {
//							comment.setCountColumnValue(Bytes.toString(CellUtil.cloneValue(cell)));
//						} else if ("ext".equals(qualifier)) {
//							comment.setExtColumnValue(Bytes.toString(CellUtil.cloneValue(cell)));
//						}
//					}
//				}
//				commentColumns.add(comment);
//				i = toIndex;
//			}
//			return commentColumns;
//		} catch (Exception e) {
//			logger.error("listComment", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	@Override
//	public long getCommentCount(int appId, int typeId, String tid) {
//		byte[] rowId = parseCommentRowId(appId, typeId, tid);
//		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT))) {
//			Get get = new Get(rowId);
//			get.addFamily(TABLE_COMMENT_TITLE);
//			Result result = table.get(get);
//			byte[] rcountBytes = result.getValue(TABLE_COMMENT_TITLE, Bytes.toBytes("rcount"));
//			if (rcountBytes == null) {
//				return 0;
//			}
//			return Bytes.toLong(rcountBytes);
//		} catch (Exception e) {
//			logger.error("getCommentCount", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	@Override
//	public boolean countComment(int appId, int typeId, String tid, long cid, String countColumnValue)
//			throws IOException {
//
//		byte[] rowId = parseCommentRowId(appId, typeId, tid);
//		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT))) {
//			byte[] cidBytes = Bytes.toBytes(cid);
//			Put put = new Put(rowId);
//			put.addColumn(TABLE_COMMENT_COMMENT, BytesUtil.spliceBytes(cidBytes, Bytes.toBytes("count")),
//					Bytes.toBytes(countColumnValue));
//			table.put(put);
//			return true;
//		} catch (Exception e) {
//			logger.error("countComment", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	@Override
//	public CommentColumn getComment(int appId, int typeId, String tid, long cid) {
//		byte[] rowId = parseCommentRowId(appId, typeId, tid);
//		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT))) {
//			List<CommentColumn> columns = getComments(table, rowId, cid);
//			if (columns != null && columns.size() > 0) {
//				return columns.get(0);
//			}
//			return null;
//		} catch (IOException e) {
//			logger.error("getComment", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	private List<CommentColumn> getComments(Table table, byte[] rowId, long... cids) throws IOException {
//
//		if (cids == null) {
//			return null;
//		}
//		Get get = new Get(rowId);
//		byte[] family = TABLE_COMMENT_COMMENT;
//		for (long cid : cids) {
//			byte[] cidBytes = Bytes.toBytes(cid);
//			byte[] commentQ = BytesUtil.spliceBytes(cidBytes, (Bytes.toBytes("comment")));
//			byte[] user = BytesUtil.spliceBytes(cidBytes, (Bytes.toBytes("user")));
//			byte[] ext = BytesUtil.spliceBytes(cidBytes, (Bytes.toBytes("ext")));
//			byte[] count = BytesUtil.spliceBytes(cidBytes, (Bytes.toBytes("count")));
//			get.addColumn(family, cidBytes);
//			get.addColumn(family, commentQ);
//			get.addColumn(family, user);
//			get.addColumn(family, ext);
//			get.addColumn(family, count);
//		}
//		Result result = table.get(get);
//		List<Cell> cells = result.listCells();
//		if (cells == null || cells.size() < 1) {
//			return null;
//		}
//		int cellSize = cells.size();
//		List<CommentColumn> commentColumns = new ArrayList<>(cids.length);
//		for (int i = 0; i < cellSize;) {
//			int fromIndex = i;
//			int toIndex = i + 5;
//			if (toIndex > cellSize) {
//				toIndex = cellSize;
//			}
//			List<Cell> subCells = cells.subList(fromIndex, toIndex);
//			CommentColumn comment = new CommentColumn();
//			for (Cell cell : subCells) {
//				if (cell.getQualifierLength() <= 8) {
//					long commentId = Bytes.toLong(CellUtil.cloneQualifier(cell));
//					comment.setCommentId(commentId);
//					comment.setIdColumnValue(Bytes.toString(CellUtil.cloneValue(cell)));
//				} else {
//					byte[] qualifierBytes = CellUtil.cloneQualifier(cell);
//					String qualifier = Bytes.toString(qualifierBytes, 8, qualifierBytes.length - 8);
//					if ("comment".equals(qualifier)) {
//						comment.setCommentColumnValue(Bytes.toString(CellUtil.cloneValue(cell)));
//					} else if ("user".equals(qualifier)) {
//						comment.setUserColumnValue(Bytes.toString(CellUtil.cloneValue(cell)));
//					} else if ("count".equals(qualifier)) {
//						comment.setCountColumnValue(Bytes.toString(CellUtil.cloneValue(cell)));
//					} else if ("ext".equals(qualifier)) {
//						comment.setExtColumnValue(Bytes.toString(CellUtil.cloneValue(cell)));
//					}
//				}
//			}
//			commentColumns.add(comment);
//			i = toIndex;
//		}
//		return commentColumns;
//	}
//
//	@Override
//	public boolean del(int appId, int typeId, String tid, long cid) throws IOException {
//
//		byte[] rowId = parseCommentRowId(appId, typeId, tid);
//		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT))) {
//			byte[] family = TABLE_COMMENT_COMMENT;
//			byte[] cidBytes = Bytes.toBytes(cid);
//			byte[] commentColumnBytes = BytesUtil.spliceBytes(cidBytes, (Bytes.toBytes("comment")));
//			byte[] userColumnBytes = BytesUtil.spliceBytes(cidBytes, (Bytes.toBytes("user")));
//			byte[] extColumnBytes = BytesUtil.spliceBytes(cidBytes, (Bytes.toBytes("ext")));
//			byte[] countColumnBytes = BytesUtil.spliceBytes(cidBytes, (Bytes.toBytes("count")));
//			Delete del = new Delete(rowId);
//			del.addColumns(family, cidBytes);
//			del.addColumns(family, commentColumnBytes);
//			del.addColumns(family, userColumnBytes);
//			del.addColumns(family, extColumnBytes);
//			del.addColumns(family, countColumnBytes);
//			table.delete(del);
//			table.incrementColumnValue(rowId, TABLE_COMMENT_TITLE, Bytes.toBytes("rcount"), -1);
//			return true;
//		} catch (Exception e) {
//			logger.error("setConf", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	@Override
//	public boolean addPraiser(long cid, Long rid, long uid, String userValue)
//			throws IllegalArgumentException, IOException {
//
//		if (rid != null) {
//			cid = rid;
//		}
//		byte[] rowId = Bytes.toBytes(uid);
//		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT_PRAISER))) {
//			Put put = new Put(rowId);
//			put.addColumn(Bytes.toBytes("praiser"), Bytes.toBytes(cid), Bytes.toBytes(userValue));
//			table.put(put);
//			return true;
//		} catch (Exception e) {
//			logger.error("addPraiser", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	private List<CommentCheckColumn> getCheckComments(Table table, long... ids)
//			throws IllegalArgumentException, IOException {
//		List<Get> gets = new ArrayList<Get>(ids.length);
//		for (long id : ids) {
//			System.out.println(id);
//			Get get = new Get(Bytes.toBytes(id));
//			gets.add(get);
//		}
//		Result[] results = table.get(gets);
//		if (results == null || results.length < 1) {
//			return null;
//		}
//		byte[] family = Bytes.toBytes("comment");
//		byte[] comment = Bytes.toBytes("comment");
//		byte[] user = Bytes.toBytes("user");
//		byte[] id = Bytes.toBytes("id");
//		byte[] count = Bytes.toBytes("count");
//		byte[] ext = Bytes.toBytes("ext");
//		byte[] author = Bytes.toBytes("author");
//		byte[] authorId = Bytes.toBytes("authorId");
//		byte[] content = Bytes.toBytes("content");
//		List<CommentCheckColumn> commentColumns = new ArrayList<>(results.length);
//		for (Result result : results) {
//			if (result == null || result.isEmpty()) {
//				continue;
//			}
//			CommentCheckColumn commentColumn = new CommentCheckColumn();
//			commentColumn.setCommentId(Bytes.toLong(result.getRow()));
//			commentColumn.setCommentColumnValue(Bytes.toString(result.getValue(family, comment)));
//			commentColumn.setIdColumnValue(Bytes.toString(result.getValue(family, id)));
//			commentColumn.setUserColumnValue(Bytes.toString(result.getValue(family, user)));
//			commentColumn.setExtColumnValue(Bytes.toString(result.getValue(family, ext)));
//			commentColumn.setCountColumnValue(Bytes.toString(result.getValue(family, count)));
//			commentColumn.setAuthor(Bytes.toString(result.getValue(family, author)));
//			commentColumn.setAuthorId(Bytes.toLong(result.getValue(family, authorId)));
//			commentColumn.setContent(Bytes.toString(result.getValue(family, content)));
//			byte[] checker = result.getValue(family, Bytes.toBytes("checker"));
//			byte[] checkTime = result.getValue(family, Bytes.toBytes("checkTime"));
//			if (checker != null) {
//				commentColumn.setChecker(Bytes.toString(checker));
//			}
//			if (checkTime != null) {
//				commentColumn.setCheckTime(Bytes.toLong(checkTime));
//			}
//			commentColumns.add(commentColumn);
//		}
//		return commentColumns;
//	}
//
//	@Override
//	public List<CommentCheckColumn> getReviewComments(int typeId, long... ids)
//			throws IllegalArgumentException, IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_REVIEW : TABLE_COMMENT_REVIEW_RESOURCE))) {
//			return getCheckComments(table, ids);
//		} catch (Exception e) {
//			logger.error("getReviewComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//
//	}
//
//	@Override
//	public List<CommentCheckColumn> getCheckPassComments(int typeId, long... ids)
//			throws IllegalArgumentException, IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_PASS : TABLE_COMMENT_PASS_RESOURCE))) {
//			return getCheckComments(table, ids);
//		} catch (Exception e) {
//			logger.error("getCheckPassComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	@Override
//	public List<CommentCheckColumn> getAIPassComments(int typeId, long... ids)
//			throws IllegalArgumentException, IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_AI_PASS : TABLE_COMMENT_AI_PASS_RESOURCE))) {
//			return getCheckComments(table, ids);
//		} catch (Exception e) {
//			logger.error("getAIPassComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	@Override
//	public List<CommentCheckColumn> getCheckNoPassComments(int typeId, long... ids)
//			throws IllegalArgumentException, IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_NOPASS : TABLE_COMMENT_NOPASS_RESOURCE))) {
//			return getCheckComments(table, ids);
//		} catch (Exception e) {
//			logger.error("getCheckNoPassComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//
//	}
//
//	private boolean addCheckComments(Table table, List<CommentCheckColumn> commentColumns) throws IOException {
//		List<Put> puts = new ArrayList<>(commentColumns.size());
//		byte[] family = Bytes.toBytes("comment");
//		byte[] comment = Bytes.toBytes("comment");
//		byte[] user = Bytes.toBytes("user");
//		byte[] id = Bytes.toBytes("id");
//		byte[] count = Bytes.toBytes("count");
//		byte[] ext = Bytes.toBytes("ext");
//		byte[] author = Bytes.toBytes("author");
//		byte[] authorId = Bytes.toBytes("authorId");
//		byte[] content = Bytes.toBytes("content");
//
//		for (CommentCheckColumn commentColumn : commentColumns) {
//			Put put = new Put(Bytes.toBytes(commentColumn.getCommentId()));
//			put.addColumn(family, id, Bytes.toBytes(commentColumn.getIdColumnValue()));
//			put.addColumn(family, user, Bytes.toBytes(commentColumn.getUserColumnValue()));
//			put.addColumn(family, comment, Bytes.toBytes(commentColumn.getCommentColumnValue()));
//			put.addColumn(family, count, Bytes.toBytes(commentColumn.getCountColumnValue()));
//			put.addColumn(family, ext, Bytes.toBytes(commentColumn.getExtColumnValue()));
//			if (StringUtils.isNotBlank(commentColumn.getAuthor())) {
//				put.addColumn(family, author, Bytes.toBytes(commentColumn.getAuthor()));
//			}
//			put.addColumn(family, authorId, Bytes.toBytes(commentColumn.getAuthorId()));
//			if (StringUtils.isNotBlank(commentColumn.getContent())) {
//				put.addColumn(family, content, Bytes.toBytes(commentColumn.getContent()));
//			}
//			if (StringUtils.isNotBlank(commentColumn.getChecker())) {
//				put.addColumn(family, Bytes.toBytes("checker"), Bytes.toBytes(commentColumn.getChecker()));
//			}
//			if (commentColumn.getCheckTime() != 0) {
//				put.addColumn(family, Bytes.toBytes("checkTime"), Bytes.toBytes(commentColumn.getCheckTime()));
//			}
//			puts.add(put);
//		}
//		table.put(puts);
//		return true;
//	}
//
//	@Override
//	public boolean addCheckNoPassComments(int typeId, List<CommentCheckColumn> CommentColumns) throws IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_NOPASS : TABLE_COMMENT_NOPASS_RESOURCE))) {
//			return addCheckComments(table, CommentColumns);
//		} catch (Exception e) {
//			logger.error("addCheckNoPassComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	@Override
//	public boolean addCheckPassComments(int typeId, List<CommentCheckColumn> CommentColumns) throws IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_PASS : TABLE_COMMENT_PASS_RESOURCE))) {
//			return addCheckComments(table, CommentColumns);
//		} catch (Exception e) {
//			logger.error("addCheckPassComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	@Override
//	public boolean addAIPassComments(int typeId, List<CommentCheckColumn> CommentColumns) throws IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_AI_PASS : TABLE_COMMENT_AI_PASS_RESOURCE))) {
//			return addCheckComments(table, CommentColumns);
//		} catch (Exception e) {
//			logger.error("addAIPassComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	@Override
//	public boolean addReviewComments(int typeId, List<CommentCheckColumn> CommentColumns) throws IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_REVIEW : TABLE_COMMENT_REVIEW_RESOURCE))) {
//			return addCheckComments(table, CommentColumns);
//		} catch (Exception e) {
//			logger.warn("addReviewComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	private boolean delCheckComments(Table table, long... ids) throws IOException {
//		List<Delete> dels = new ArrayList<>(ids.length);
//		for (long id : ids) {
//			Delete del = new Delete(Bytes.toBytes(id));
//			dels.add(del);
//		}
//		table.delete(dels);
//		return true;
//	}
//
//	@Override
//	public boolean delReviewComments(int typeId, long... ids) throws IllegalArgumentException, IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_REVIEW : TABLE_COMMENT_REVIEW_RESOURCE))) {
//			return delCheckComments(table, ids);
//		} catch (Exception e) {
//			logger.error("delReviewComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	@Override
//	public boolean delAIPassComments(int typeId, long... ids) throws IllegalArgumentException, IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_AI_PASS : TABLE_COMMENT_AI_PASS_RESOURCE))) {
//			return delCheckComments(table, ids);
//		} catch (Exception e) {
//			logger.error("delReviewComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	@Override
//	public boolean delCheckPassComments(int typeId, long... ids) throws IllegalArgumentException, IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_PASS : TABLE_COMMENT_PASS_RESOURCE))) {
//			return delCheckComments(table, ids);
//		} catch (Exception e) {
//			logger.error("delCheckPassComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//
//	}
//
//	@Override
//	public boolean delCheckNoPassComments(int typeId, long... ids) throws IllegalArgumentException, IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_NOPASS : TABLE_COMMENT_NOPASS_RESOURCE))) {
//			return delCheckComments(table, ids);
//		} catch (Exception e) {
//			logger.error("delCheckNoPassComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//
//	}
//
//	@Override
//	public boolean addCommentReplyer(int appId, int typeId, String tid, long cid, String extValue)
//			throws IllegalArgumentException, IOException {
//
//		byte[] rowId = parseCommentRowId(appId, typeId, tid);
//		Put put = new Put(rowId);
//		byte[] extColumn = BytesUtil.spliceBytes(Bytes.toBytes(cid), Bytes.toBytes("ext"));
//		put.addColumn(Bytes.toBytes("comment"), extColumn, Bytes.toBytes(extValue));
//		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT))) {
//			table.put(put);
//			return true;
//		} catch (Exception e) {
//			logger.error("addCommentReplyer", e);
//			throw new RuntimeException(e.getMessage());
//		}
//
//	}
//
//	public List<CommentCheckColumn> listCheckComments(Table table, long pageId, int pageSize, String orderBy,
//			String keyword, String author, long authorId, String checker, long startckeckTime, long endckeckTime)
//			throws IllegalArgumentException, IOException {
//		Scan scan = new Scan();
//		byte[] family = Bytes.toBytes("comment");
//		byte[] comment = Bytes.toBytes("comment");
//		byte[] user = Bytes.toBytes("user");
//		byte[] id = Bytes.toBytes("id");
//		byte[] count = Bytes.toBytes("count");
//		byte[] ext = Bytes.toBytes("ext");
//		byte[] content = Bytes.toBytes("content");
//		byte[] authorq = Bytes.toBytes("author");
//		byte[] authorIdq = Bytes.toBytes("authorId");
//		byte[] checkerq = Bytes.toBytes("checker");
//		byte[] checkTime = Bytes.toBytes("checkTime");
//		scan.addColumn(family, comment);
//		scan.addColumn(family, user);
//		scan.addColumn(family, id);
//		scan.addColumn(family, ext);
//		scan.addColumn(family, count);
//		scan.addColumn(family, checkerq);
//		scan.addColumn(family, checkTime);
//		scan.addColumn(family, authorq);
//		scan.addColumn(family, authorIdq);
//		scan.addColumn(family, content);
//		if ("desc".equals(orderBy)) {
//			if (pageId > 0)
//				scan.setStartRow(Bytes.toBytes(pageId + 1));
//			scan.setReversed(false);
//		} else {
//			if (pageId > 0)
//				scan.setStartRow(Bytes.toBytes(pageId - 1));
//			scan.setReversed(true);
//		}
//
//		FilterList filters = new FilterList(Operator.MUST_PASS_ALL);
//		if (StringUtils.isNotBlank(author)) {
//			SingleColumnValueFilter filter = new SingleColumnValueFilter(family, authorq, CompareOp.EQUAL,
//					Bytes.toBytes(author));
//			filter.setFilterIfMissing(true);
//			filters.addFilter(filter);
//		} else if (authorId != 0) {
//			SingleColumnValueFilter filter = new SingleColumnValueFilter(family, authorIdq, CompareOp.EQUAL,
//					Bytes.toBytes(authorId));
//			filter.setFilterIfMissing(true);
//			filters.addFilter(filter);
//		} else if (StringUtils.isNotBlank(keyword)) {
//			SubstringComparator sc = new SubstringComparator(keyword);
//			SingleColumnValueFilter filter = new SingleColumnValueFilter(family, content, CompareOp.EQUAL, sc);
//			filter.setFilterIfMissing(true);
//			filters.addFilter(filter);
//		} else if (StringUtils.isNotBlank(checker)) {
//			SingleColumnValueFilter filter = new SingleColumnValueFilter(family, checkerq, CompareOp.EQUAL,
//					Bytes.toBytes(checker));
//			filter.setFilterIfMissing(true);
//			filters.addFilter(filter);
//		}
//		if (startckeckTime > 0) {
//			SingleColumnValueFilter filter = new SingleColumnValueFilter(family, checkTime, CompareOp.GREATER_OR_EQUAL,
//					Bytes.toBytes(startckeckTime));
//			filter.setFilterIfMissing(true);
//			filters.addFilter(filter);
//		}
//		if (endckeckTime > 0) {
//			SingleColumnValueFilter filter = new SingleColumnValueFilter(family, checkTime, CompareOp.LESS_OR_EQUAL,
//					Bytes.toBytes(endckeckTime));
//			filter.setFilterIfMissing(true);
//			filters.addFilter(filter);
//		}
//		Filter pageFilter = new PageFilter(pageSize);
//		filters.addFilter(pageFilter);
//		scan.setFilter(filters);
//		scan.setCaching(500);
//		ResultScanner results = table.getScanner(scan);
//		List<CommentCheckColumn> commentColumns = new ArrayList<>(pageSize);
//		logger.debug("listCheckPassComments dao pageSize {}", pageSize);
//		for (Result result : results) {
//			CommentCheckColumn commentColumn = new CommentCheckColumn();
//			commentColumn.setCommentId(Bytes.toLong(result.getRow()));
//			commentColumn.setCommentColumnValue(Bytes.toString(result.getValue(family, comment)));
//			commentColumn.setIdColumnValue(Bytes.toString(result.getValue(family, id)));
//			commentColumn.setUserColumnValue(Bytes.toString(result.getValue(family, user)));
//			commentColumn.setExtColumnValue(Bytes.toString(result.getValue(family, ext)));
//			commentColumn.setCountColumnValue(Bytes.toString(result.getValue(family, count)));
//			byte[] checkerBytes = result.getValue(family, checkerq);
//			if (checkerBytes != null) {
//				commentColumn.setChecker(Bytes.toString(checkerBytes));
//			}
//			byte[] checkTimeBytes = result.getValue(family, checkTime);
//			if (checkTimeBytes != null) {
//				commentColumn.setCheckTime(Bytes.toLong(checkTimeBytes));
//			}
//			commentColumns.add(commentColumn);
//			// 定版上回莫名其妙的查出来pageSize*6的数据，先暂时手工截断 TODO
//			pageSize--;
//			if (pageSize == 0) {
//				break;
//			}
//		}
//		results.close();
//		return commentColumns;
//	}
//
//	@Override
//	public List<CommentCheckColumn> listReviewComments(int typeId, long pageId, int pageSize, String orderBy,
//			String keyword, String author, long authorId, String checker, long startCkeckTime, long endCkeckTime)
//			throws IllegalArgumentException, IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_REVIEW : TABLE_COMMENT_REVIEW_RESOURCE))) {
//			return listCheckComments(table, pageId, pageSize, orderBy, keyword, author, authorId, checker,
//					startCkeckTime, endCkeckTime);
//		} catch (Exception e) {
//			logger.error("listReviewComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//
//	}
//
//	@Override
//	public List<CommentCheckColumn> listCheckPassComments(int typeId, long pageId, int pageSize, String orderBy,
//			String keyword, String author, long authorId, String checker, long startCkeckTime, long endCkeckTime)
//			throws IllegalArgumentException, IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_PASS : TABLE_COMMENT_PASS_RESOURCE))) {
//			return listCheckComments(table, pageId, pageSize, orderBy, keyword, author, authorId, checker,
//					startCkeckTime, endCkeckTime);
//		} catch (Exception e) {
//			logger.error("listCheckPassComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	@Override
//	public List<CommentCheckColumn> listAIPassComments(int typeId, long pageId, int pageSize, String orderBy,
//			String keyword, String author, long authorId, String checker, long startCkeckTime, long endCkeckTime)
//			throws IllegalArgumentException, IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_AI_PASS : TABLE_COMMENT_AI_PASS_RESOURCE))) {
//			return listCheckComments(table, pageId, pageSize, orderBy, keyword, author, authorId, checker,
//					startCkeckTime, endCkeckTime);
//		} catch (Exception e) {
//			logger.error("listAIPassComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	@Override
//	public List<CommentCheckColumn> listCheckNoPassComments(int typeId, long pageId, int pageSize, String orderBy,
//			String keyword, String author, long authorId, String checker, long startCkeckTime, long endCkeckTime)
//			throws IllegalArgumentException, IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_NOPASS : TABLE_COMMENT_NOPASS_RESOURCE))) {
//			return listCheckComments(table, pageId, pageSize, orderBy, keyword, author, authorId, checker,
//					startCkeckTime, endCkeckTime);
//		} catch (Exception e) {
//			logger.error("listCheckNoPassComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//
//	}
//
//	@Override
//	public int countReviewComments(int typeId, String keyword, String author, long authorId, String checker,
//			long startckeckTime, long endckeckTime) throws IllegalArgumentException, IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_REVIEW : TABLE_COMMENT_REVIEW_RESOURCE))) {
//			return countCheckComments(table, keyword, author, authorId, checker, startckeckTime, endckeckTime);
//		} catch (Exception e) {
//			logger.error("countReviewComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//
//	}
//
//	@Override
//	public int countCheckPassComments(int typeId, String keyword, String author, long authorId, String checker,
//			long startckeckTime, long endckeckTime) throws IllegalArgumentException, IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_PASS : TABLE_COMMENT_PASS_RESOURCE))) {
//			return countCheckComments(table, keyword, author, authorId, checker, startckeckTime, endckeckTime);
//		} catch (Exception e) {
//			logger.error("countCheckPassComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//
//	}
//
//	@Override
//	public int countCheckNoPassComments(int typeId, String keyword, String author, long authorId, String checker,
//			long startckeckTime, long endckeckTime) throws IllegalArgumentException, IOException {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_NOPASS : TABLE_COMMENT_NOPASS_RESOURCE))) {
//			return countCheckComments(table, keyword, author, authorId, checker, startckeckTime, endckeckTime);
//		} catch (Exception e) {
//			logger.error("countCheckNoPassComments", e);
//			throw new RuntimeException(e.getMessage());
//		}
//
//	}
//
//	private int countCheckComments(Table table, String keyword, String author, long authorId, String checker,
//			long startckeckTime, long endckeckTime) throws IllegalArgumentException, IOException {
//		Scan scan = new Scan();
//		byte[] family = Bytes.toBytes("comment");
//		byte[] content = Bytes.toBytes("content");
//		byte[] authorq = Bytes.toBytes("author");
//		byte[] authorIdq = Bytes.toBytes("authorId");
//		byte[] checkerq = Bytes.toBytes("checker");
//		byte[] checkTime = Bytes.toBytes("checkTime");
//		scan.addColumn(family, checkerq);
//		scan.addColumn(family, checkTime);
//		scan.addColumn(family, authorq);
//		scan.addColumn(family, authorIdq);
//		scan.addColumn(family, content);
//		FilterList filters = new FilterList(Operator.MUST_PASS_ALL);
//		filters.addFilter(new KeyOnlyFilter());
//		if (StringUtils.isNotBlank(author)) {
//			SingleColumnValueFilter filter = new SingleColumnValueFilter(family, authorq, CompareOp.EQUAL,
//					Bytes.toBytes(author));
//			filter.setFilterIfMissing(true);
//			filters.addFilter(filter);
//		} else if (authorId != 0) {
//			SingleColumnValueFilter filter = new SingleColumnValueFilter(family, authorIdq, CompareOp.EQUAL,
//					Bytes.toBytes(authorId));
//			filter.setFilterIfMissing(true);
//			filters.addFilter(filter);
//		} else if (StringUtils.isNotBlank(keyword)) {
//			SubstringComparator sc = new SubstringComparator(keyword);
//			SingleColumnValueFilter filter = new SingleColumnValueFilter(family, content, CompareOp.EQUAL, sc);
//			filter.setFilterIfMissing(true);
//			filters.addFilter(filter);
//		} else if (StringUtils.isNotBlank(checker)) {
//			SingleColumnValueFilter filter = new SingleColumnValueFilter(family, checkerq, CompareOp.EQUAL,
//					Bytes.toBytes(checker));
//			filter.setFilterIfMissing(true);
//			filters.addFilter(filter);
//		}
//		if (startckeckTime > 0) {
//			Filter filter = new SingleColumnValueFilter(family, checkTime, CompareOp.GREATER_OR_EQUAL,
//					Bytes.toBytes(startckeckTime));
//			filters.addFilter(filter);
//		}
//		if (endckeckTime > 0) {
//			Filter filter = new SingleColumnValueFilter(family, checkTime, CompareOp.LESS_OR_EQUAL,
//					Bytes.toBytes(endckeckTime));
//			filters.addFilter(filter);
//		}
//		scan.setFilter(filters);
//		ResultScanner results = table.getScanner(scan);
//		int count = 0;
//		for (Result result : results) {
//			if (!result.isEmpty()) {
//				count++;
//			}
//		}
//		results.close();
//		return count;
//	}
//
//	@Override
//	public void addUserComment(long uid, long cid, String userCommentValue)
//			throws IllegalArgumentException, IOException {
//
//		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT_USERCOMMENTS))) {
//			Put put = new Put(Bytes.toBytes(uid));
//			put.addColumn(Bytes.toBytes("comment"), Bytes.toBytes(cid), Bytes.toBytes(userCommentValue));
//			table.put(put);
//		} catch (Exception e) {
//			logger.error("addUserComment", e);
//			throw new RuntimeException(e.getMessage());
//		}
//
//	}
//
//	@Override
//	public boolean delUserComment(long uid, long cid) throws IllegalArgumentException, IOException {
//
//		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT_USERCOMMENTS))) {
//			Delete del = new Delete(Bytes.toBytes(uid));
//			del.addColumns(Bytes.toBytes("comment"), Bytes.toBytes(cid));
//			table.delete(del);
//			return true;
//		} catch (Exception e) {
//			logger.error("delUserComment", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	@Override
//	public List<String> listUserComment(long uid, long lastId, int len) throws IllegalArgumentException, IOException {
//		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT_USERCOMMENTS))) {
//			Get get = new Get(Bytes.toBytes(uid));
//			get.addFamily(Bytes.toBytes("comment"));
//			lastId++;
//			get.setFilter(new ColumnPaginationFilter(len, Bytes.toBytes(lastId)));
//			Result result = table.get(get);
//			List<Cell> cells = result.listCells();
//			if (cells == null || cells.size() < 1) {
//				return null;
//			}
//			List<String> userComments = new ArrayList<>(cells.size());
//			for (Cell cell : cells) {
//				String userComment = Bytes.toString(CellUtil.cloneValue(cell));
//				userComments.add(userComment);
//			}
//			return userComments;
//		} catch (Exception e) {
//			logger.error("listUserComment", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	@Override
//	public boolean delReplyComment(int appId, int typeId, String tid, long cid, String contentColumn)
//			throws IOException {
//		try (Table table = connection.getTable(TableName.valueOf(TABLE_COMMENT))) {
//			byte[] rowId = parseCommentRowId(appId, typeId, tid);
//			Put put = new Put(rowId);
//			byte[] commentColumn = BytesUtil.spliceBytes(Bytes.toBytes(cid), Bytes.toBytes("comment"));
//			put.addColumn(TABLE_COMMENT_COMMENT, commentColumn, Bytes.toBytes(contentColumn));
//			table.put(put);
//			return true;
//		} catch (Exception e) {
//			logger.error("delReplyComment", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	@Override
//	public Map<String, List<CommentDTO>> scanComment(String StartTid, int len) {
//		byte[] startRow = parseCommentRowId(0, 0, StartTid);
//		byte[] stopRow = parseCommentRowId(4, 0, "0");
//		logger.info("startRow {}", Bytes.toStringBinary(startRow)); // \x00\x00\x00\x03\x00\x00\x00\x040000000000000000000000000000000000000000
//		Scan scan = new Scan();
//		scan.addFamily(Bytes.toBytes("comment"));
//		scan.setStartRow(startRow);
//		scan.setStopRow(stopRow);
//		scan.setCaching(len * 2);
//		scan.setFilter(new PageFilter(len));
//		Map<String, List<CommentDTO>> resultMap = new LinkedHashMap<>();
//		try (Table comment = connection.getTable(TableName.valueOf("comment"))) {
//			try (ResultScanner scanner = comment.getScanner(scan)) {
//				for (Result result : scanner) {
//					byte[] row = result.getRow();
//					if (row.length == 48) {
//						logger.info("rowKey {}", Bytes.toStringBinary(row));
//						String tid = Bytes.toStringBinary(Bytes.copy(row, 8, 40));
//						List<Cell> cells = result.listCells();
//						if (!CollectionUtils.isEmpty(cells)) {
//							int cellSize = cells.size();
//							if (cellSize % 5 == 0) {
//								List<CommentDTO> commentDTOs = new ArrayList<>(cellSize);
//								for (int i = 0; i < cellSize; i = i + 5) {
//									CommentDTO commentDTO = new CommentDTO();
//									commentDTO.setIdColumn(Bytes.toString(CellUtil.cloneValue(cells.get(i))));
//									commentDTO.setContentColumn(Bytes.toString(CellUtil.cloneValue(cells.get(i + 1))));
//									commentDTO.setExtColumn(Bytes.toString(CellUtil.cloneValue(cells.get(i + 3))));
//									commentDTO.setUserColumn(Bytes.toString(CellUtil.cloneValue(cells.get(i + 4))));
//									commentDTOs.add(commentDTO);
//								}
//								resultMap.put(tid, commentDTOs);
//							} else {
//								throw new ApiException(1);
//							}
//						}
//						logger.info("resultSize {}", result.size());
//						logger.info("cells {}", result.listCells().size());
//					} else {
//						logger.warn("tid error {}", Bytes.toStringBinary(row));
//					}
//				}
//				scanner.close();
//				logger.info(scanner.toString());
//			} catch (Exception e) {
//				logger.warn("scanComment result error");
//			}
//		} catch (Exception e) {
//			logger.warn("scanComment error");
//		}
//		return resultMap;
//	}
//
//	@Test
//	public void main() {
//		logger.error("adff");
//		System.out.println(Bytes.toStringBinary(parseCommentRowId(3, 4, "7240CE532D1FBB4605CC6187AA60057A766E2426")));
//		System.out.println(Bytes.toStringBinary(Bytes.toBytes(9189206464412515327L)));
//		testLogger(123L, 456L, 65556L);
//		String str = "\\x00\\x00\\x00\\x03\\x00\\x00\\x00\\x01016c69e3e3d15bb3dcc44a99040a457dcaf61ce6";
//		byte[] strBytes = Bytes.copy(str.getBytes(), 0, 32);
//		System.out.println(str.getBytes().length);
//		System.out.println(Bytes.toStringBinary(strBytes));
//
//	}
//
//	private void testLogger(long... ids) {
//		StringBuilder sb = new StringBuilder();
//		for (long id : ids) {
//			sb.append(id).append(",");
//		}
//		logger.info("test {}", sb.toString());
//		logger.info("test info {}", ids);
//	}
//
//	@Override
//	public List<CommentCheckColumn> listCommentPass(int typeId, long pageId, int pageSize) {
//		try (Table table = connection
//				.getTable(TableName.valueOf(typeId == 1 ? TABLE_COMMENT_PASS : TABLE_COMMENT_PASS_RESOURCE))) {
//			return listCheckComments(table, pageId, pageSize);
//		} catch (Exception e) {
//			logger.error("listCommentPass", e);
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	private List<CommentCheckColumn> listCheckComments(Table table, long pageId, int pageSize)
//			throws IllegalArgumentException, IOException {
//		Scan scan = new Scan();
//		scan.addColumn(family, comment);
//		scan.addColumn(family, user);
//		scan.addColumn(family, id);
//		scan.addColumn(family, ext);
//		scan.addColumn(family, count);
//		scan.addColumn(family, checkerq);
//		scan.addColumn(family, checkTime);
//		scan.addColumn(family, authorq);
//		scan.addColumn(family, authorIdq);
//		scan.addColumn(family, content);
//		scan.setStartRow(Bytes.toBytes(pageId - 1));
//		scan.setReversed(true);
//
//		FilterList filters = new FilterList(FilterList.Operator.MUST_PASS_ALL);
//		Filter pageFilter = new PageFilter(pageSize);
//		filters.addFilter(pageFilter);
//		scan.setFilter(filters);
//		scan.setCaching(500);
//		ResultScanner results = table.getScanner(scan);
//		List<CommentCheckColumn> commentColumns = new ArrayList<>(pageSize);
//		logger.debug("listCheckPassComments dao pageSize {}", pageSize);
//		for (Result result : results) {
//			CommentCheckColumn commentColumn = new CommentCheckColumn();
//			commentColumn.setCommentId(Bytes.toLong(result.getRow()));
//			commentColumn.setCommentColumnValue(Bytes.toString(result.getValue(family, comment)));
//			commentColumn.setIdColumnValue(Bytes.toString(result.getValue(family, id)));
//			commentColumn.setUserColumnValue(Bytes.toString(result.getValue(family, user)));
//			commentColumn.setExtColumnValue(Bytes.toString(result.getValue(family, ext)));
//			commentColumn.setCountColumnValue(Bytes.toString(result.getValue(family, count)));
//			byte[] checkerBytes = result.getValue(family, checkerq);
//			if (checkerBytes != null) {
//				commentColumn.setChecker(Bytes.toString(checkerBytes));
//			}
//			byte[] checkTimeBytes = result.getValue(family, checkTime);
//			if (checkTimeBytes != null) {
//				commentColumn.setCheckTime(Bytes.toLong(checkTimeBytes));
//			}
//			commentColumns.add(commentColumn);
//			// 定版上回莫名其妙的查出来pageSize*6的数据，先暂时手工截断 TODO
//			pageSize--;
//			if (pageSize == 0) {
//				break;
//			}
//		}
//		results.close();
//		return commentColumns;
//	}
}
