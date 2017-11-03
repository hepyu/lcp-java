package com.open.lcp.biz.comment;

import java.util.Enumeration;
import java.util.ResourceBundle;

import com.open.lcp.core.framework.api.NZCode;
import com.open.lcp.core.framework.api.NZCodeHolder;

public class CommentErrorCode {

	public static final CommentErrorCode COMMENT_CODE_USER_NOT_FOUND = new CommentErrorCode(40000,
			NZCodeHolder.get(40000).getMessage(), NZCodeHolder.get(40000).getCause());

	public static final CommentErrorCode COMMENT_CODE_CONTENT_TOO_LONG = new CommentErrorCode(40001,
			NZCodeHolder.get(40001).getMessage(), NZCodeHolder.get(40001).getCause());

	public static final CommentErrorCode COMMENT_CODE_CONTENT_REPEAT = new CommentErrorCode(40002,
			NZCodeHolder.get(40002).getMessage(), NZCodeHolder.get(40002).getCause());

	public static final CommentErrorCode COMMENT_CODE_COMMENT_NOT_CONFIGED = new CommentErrorCode(40003,
			NZCodeHolder.get(40003).getMessage(), NZCodeHolder.get(40003).getCause());

	public static final CommentErrorCode COMMENT_CODE_ADD_REVIEW_COMMENT_ERROR = new CommentErrorCode(40003,
			NZCodeHolder.get(40005).getMessage(), NZCodeHolder.get(40005).getCause());

	public static final CommentErrorCode COMMENT_CODE_ADD_CHECK_NO_PASS_COMMENT_ERROR = new CommentErrorCode(40003,
			NZCodeHolder.get(40006).getMessage(), NZCodeHolder.get(40006).getCause());

	public static final CommentErrorCode COMMENT_CODE_AUDIT_NOT_PASS = new CommentErrorCode(40010,
			NZCodeHolder.get(40010).getMessage(), NZCodeHolder.get(40010).getCause());

	public static final CommentErrorCode COMMENT_CODE_COMMENT_USER_IS_SLIENCED = new CommentErrorCode(40011,
			NZCodeHolder.get(40011).getMessage(), NZCodeHolder.get(40011).getCause());

	public static final CommentErrorCode COMMENT_CODE_COMMENT_USER_IS_SLIENCED_FOREVER = new CommentErrorCode(40012,
			NZCodeHolder.get(40012).getMessage(), NZCodeHolder.get(40012).getCause());

	public static final CommentErrorCode COMMENT_CODE_CANT_FIND_COMMENT_FROM_REVIEW = new CommentErrorCode(40013,
			NZCodeHolder.get(40013).getMessage(), NZCodeHolder.get(40013).getCause());

	public static final CommentErrorCode COMMENT_CODE_CANT_FIND_COMMENT_FROM_NOCHECKPASS = new CommentErrorCode(40014,
			NZCodeHolder.get(40014).getMessage(), NZCodeHolder.get(40014).getCause());

	public static final CommentErrorCode COMMENT_CODE_CANT_FIND_COMMENT_FROM_CHECKPASS = new CommentErrorCode(40015,
			NZCodeHolder.get(40015).getMessage(), NZCodeHolder.get(40015).getCause());

	// cant comment cause of refriended
	public static final CommentErrorCode COMMENT_CODE_CANT_FIND_COMMENT_CAUSE_REFRIENDED = new CommentErrorCode(40016,
			NZCodeHolder.get(40016).getMessage(), NZCodeHolder.get(40016).getCause());

	// cant reply cause of refriended
	public static final CommentErrorCode COMMENT_CODE_CANT_REPLY_CAUSE_REFRIENDED = new CommentErrorCode(40017,
			NZCodeHolder.get(40017).getMessage(), NZCodeHolder.get(40017).getCause());

	// cant comment cause of anti-spam
	public static final CommentErrorCode COMMENT_CODE_CANT_COMMENT_CAUSE_ANTI_SPAM = new CommentErrorCode(40018,
			NZCodeHolder.get(40018).getMessage(), NZCodeHolder.get(40018).getCause());

	// cant comment cause of video lose
	public static final CommentErrorCode COMMENT_CODE_CANT_COMMENT_CAUSE_VIDEO_LOSE = new CommentErrorCode(40019,
			NZCodeHolder.get(40019).getMessage(), NZCodeHolder.get(40019).getCause());

	private int code;

	private String message;

	private String cause;

	private CommentErrorCode(int code, String message, String cause) {
		this.code = code;
		this.message = message;
		this.cause = cause;
	}

	public int code() {
		return this.code;
	}

	public String message() {
		return this.message;
	}

	public String cause() {
		return this.cause;
	}

	static {
		final ResourceBundle rsMessage = ResourceBundle.getBundle("api_result_code_messages_comment");
		final ResourceBundle rsCause = ResourceBundle.getBundle("api_result_code_cause_comment");
		Enumeration<String> keys = rsMessage.getKeys();
		final String KEY_PRE = "api.result.msg.";
		while (keys.hasMoreElements()) {
			final String key = keys.nextElement();
			if (!key.startsWith(KEY_PRE)) {
				continue;
			}
			final String znkey = key.substring(KEY_PRE.length());
			if (!znkey.matches("\\d{1,9}")) {
				continue;
			}
			final int nzcode = Integer.valueOf(znkey);
			final String message = rsMessage.getString(key);
			final String cause = rsCause.getString(key);
			NZCode nz = NZCodeHolder.get(nzcode);
			if (nz == null) {
				NZCodeHolder.set(nzcode, cause, message);
				continue;
			}
			nz.setMessage(message);
			if (nz.getCause() != null && !nz.getCause().isEmpty()) {
				continue;
			}
			if (cause != null && cause.length() > 0) {
				nz.setCause(cause);
			}
		}
	}
}