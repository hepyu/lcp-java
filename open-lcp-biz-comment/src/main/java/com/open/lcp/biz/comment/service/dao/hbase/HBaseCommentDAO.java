package com.open.lcp.biz.comment.service.dao.hbase;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.hbase.util.Bytes;

import com.open.lcp.biz.comment.service.dao.hbase.impl.column.CommentCheckColumn;
import com.open.lcp.biz.comment.service.dao.hbase.impl.column.CommentColumn;

public interface HBaseCommentDAO {

	byte[] family = Bytes.toBytes("comment");
	byte[] comment = Bytes.toBytes("comment");
	byte[] user = Bytes.toBytes("user");
	byte[] id = Bytes.toBytes("id");
	byte[] count = Bytes.toBytes("count");
	byte[] ext = Bytes.toBytes("ext");
	byte[] content = Bytes.toBytes("content");
	byte[] authorq = Bytes.toBytes("author");
	byte[] authorIdq = Bytes.toBytes("authorId");
	byte[] checkerq = Bytes.toBytes("checker");
	byte[] checkTime = Bytes.toBytes("checkTime");

	boolean addComment(int appId, int typeId, String tid, long cid, String commentIdValue, String commentValue,
			String userValue, String countValue, String extValue) throws IOException;

	public CommentColumn getComment(int appId, int typeId, String tid, long cid);

	// void addUserComment(long uid, long cid, String userCommentValue)
	// throws IllegalArgumentException, IOException;
	//
	// boolean delUserComment(long uid, long cid)
	// throws IllegalArgumentException, IOException;
	//
	// List<String> listUserComment(long uid, long lastId, int len)
	// throws IllegalArgumentException, IOException;

	// List<CommentColumn> listComment(int appId, int typeId, String tid,
	// long lastId, int len) throws IOException;
	//
	// long getCommentCount(int appId, int typeId, String tid);
	//
	// List<CommentCheckColumn> getReviewComments(int typeId, long... ids)
	// throws IllegalArgumentException, IOException;
	//
	// List<CommentCheckColumn> getCheckPassComments(int typeId, long... ids)
	// throws IllegalArgumentException, IOException;
	//
	// List<CommentCheckColumn> getAIPassComments(int typeId, long... ids)
	// throws IllegalArgumentException, IOException;
	//
	// List<CommentCheckColumn> getCheckNoPassComments(int typeId, long... ids)
	// throws IllegalArgumentException, IOException;
	//
	//
	// boolean addCheckPassComments(int typeId, List<CommentCheckColumn>
	// commentColumns)
	// throws IOException;
	//
	// boolean addReviewComments(int typeId, List<CommentCheckColumn>
	// commentColumns)
	// throws IOException;
	//
	// List<CommentCheckColumn> listReviewComments(int typeId, long pageId,
	// int pageSize, String orderBy, String keyword, String author,
	// long authorId, String checker, long startckeckTime,
	// long endckeckTime) throws IllegalArgumentException, IOException;
	//
	// List<CommentCheckColumn> listCheckPassComments(int typeId, long pageId,
	// int pageSize, String orderBy, String keyword, String author,
	// long authorId, String checker, long startckeckTime,
	// long endckeckTime) throws IllegalArgumentException, IOException;
	//
	// List<CommentCheckColumn> listAIPassComments(int typeId, long pageId,
	// int pageSize, String orderBy, String keyword, String author,
	// long authorId, String checker, long startckeckTime,
	// long endckeckTime) throws IllegalArgumentException, IOException;
	//
	// List<CommentCheckColumn> listCheckNoPassComments(int typeId, long pageId,
	// int pageSize, String orderBy, String keyword, String author,
	// long authorId, String checker, long startckeckTime,
	// long endckeckTime) throws IllegalArgumentException, IOException;
	//
	// int countReviewComments(int typeId, String keyword, String author,
	// long authorId, String checker, long startckeckTime,
	// long endckeckTime) throws IllegalArgumentException, IOException;
	//
	// int countCheckPassComments(int typeId, String keyword, String author,
	// long authorId, String checker, long startckeckTime,
	// long endckeckTime) throws IllegalArgumentException, IOException;
	//
	// int countCheckNoPassComments(int typeId, String keyword, String author,
	// long authorId, String checker, long startckeckTime,
	// long endckeckTime) throws IllegalArgumentException, IOException;
	//
	// boolean delReviewComments(int typeId, long... ids)
	// throws IllegalArgumentException, IOException;
	//
	// boolean delAIPassComments(int typeId, long... ids)
	// throws IllegalArgumentException, IOException;
	//
	// boolean delCheckPassComments(int typeId, long... ids)
	// throws IllegalArgumentException, IOException;
	//
	// boolean delCheckNoPassComments(int typeId, long... ids)
	// throws IllegalArgumentException, IOException;
	//

	//
	// boolean countComment(int appId, int typeId, String tid, long cid,
	// String countColumnValue) throws IOException;
	//
	// boolean del(int appId, int typeId, String tid, long cid)
	// throws IOException;
	//
	// boolean addPraiser(long cid, Long rid, long uid, String userValue)
	// throws IllegalArgumentException, IOException;
	//
	// boolean addCommentReplyer(int appId, int typeId, String tid,
	// long cid, String extValue) throws IllegalArgumentException,
	// IOException;
	//
	// boolean delReplyComment(int appId, int typeId, String tid,
	// long cid, String contentColumn) throws IllegalArgumentException,
	// IOException;
	//
	// Map<String, List<CommentDTO>> scanComment(String StartTid, int len);
	//
	// boolean addAIPassComments(int typeId, List<CommentCheckColumn>
	// commentColumns) throws IOException;
	//
	// List<CommentCheckColumn> listCommentPass(int typeId, long startCid, int
	// len);
}
