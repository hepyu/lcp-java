package com.open.lcp.biz.comment;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.open.lcp.core.base.LcpResource;

public class CommentConstant {

	public static final String OK = "ok";

	public static final String ERROR = "error";

	// 禁言
	public static final String COMMENT_SILENCED_KEY = "comment_silenced_%d";

	// 永久禁言
	public static final long FOR_EVER = 9999999999999L;

	public static final long ONE_DAY_MSEC = 1000 * 60 * 60 * 24;

	public static final String COMMENT_TRIGGER_KEY = "trigger-%d-%s";
	// 新浪IP库
	public static final String IP_SEARCH_SINA_PRE = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=";
	// 空资源使用这个key注册 减缓缓存击穿
	public static final String EMPTY_TID = "empty-tid-register-%s";

	public static final String COMMENT_COUNT = "comment-count-%s";

	public static final String COMMENT_REPORT_COUNT = "comment-report-%d";

	public static final String COMMENT_REPORT_LOG = "comment-report-log-%d";

	public static final String COMMENT_HOT_RESP = "comment-hot-%s";

	public static final String COMMENT_NEW_PAGE = "comment-new-page-1_%s";

	public static final String COMMENT_TID_COUNT_SNAPSHOT = "comment-tid-count-snapshot-%s";

	public static final int COMMENT_HOT_EXPIRE = 4 * 60 * 60;

	public static final int COMMENT_NEW_PAGE_EXPIRE = 4 * 60 * 60;

	public static final int COMMENT_USER_REVIEW_EXPIRE = 60 * 60 * 8;

	public static final String COMMENT_RECENT_PRAISER = "comment-recent-praise-%d";

	public static final Comparator<Map.Entry<Long, Long>> comparator = new Comparator<Map.Entry<Long, Long>>() {
		@Override
		public int compare(Map.Entry<Long, Long> o1, Map.Entry<Long, Long> o2) {
			if (o1.getValue().compareTo(o2.getValue()) == 0) {
				return (int) (o2.getKey() - o1.getKey());
			}
			return (int) (o2.getValue() - o1.getValue());
		}
	};

	public static final String COMMENT_SPEED_Z = "comment-speed-z-%s";

	public static final String COMMENT_TID_USER_Z = "comment-tid-user-z-%s-%d";

	public static final String COMMENT_USER_TYPE = "comment-user-type-%d-%d";

	public static final Set<Integer> NEED_HOT = new HashSet<Integer>() {
		{
			add(14);
			add(17);
			add(43);
		}
	};

	public static final String COMMENT_KV = "comment-kv-%d";
}
