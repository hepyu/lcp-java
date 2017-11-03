package com.open.lcp.biz.comment.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import com.open.lcp.biz.comment.CommentConstant;
import com.open.lcp.biz.comment.CommentErrorCode;
import com.open.lcp.biz.comment.service.dao.db.MysqlCommentConfigDAO;
import com.open.lcp.biz.comment.service.dao.db.entity.CommentConfigEntity;
import com.open.lcp.core.framework.api.ApiException;
import com.open.lcp.core.framework.loader.TimerLoader;

/**
 * 5min更新评论共享配置
 */
@Component
public class AppCommentConfigService implements TimerLoader {

	private static final Log logger = LogFactory.getLog(AppCommentConfigService.class);

	private Map<Integer, CommentConfigEntity> appCommentConf;

	@Resource
	private MysqlCommentConfigDAO commentMySqlDao;

	@Override
	public boolean initLoad() {
		return true;
	}

	@Override
	public boolean reloadable(int hour, int minute, long minuteOfAll) {
		return minute % 5 == 0;
	}

	@Override
	public String reload() {
		try {
			List<CommentConfigEntity> commentConfigs = commentMySqlDao.listCommentConfig();
			Map<Integer, CommentConfigEntity> newMap = new HashMap<Integer, CommentConfigEntity>();
			for (CommentConfigEntity commentConfig : commentConfigs) {
				newMap.put(commentConfig.getAppId(), commentConfig);
			}
			this.appCommentConf = newMap;
			return CommentConstant.OK;
		} catch (Exception e) {
			logger.error("reload commentConfig error", e);
		}
		return CommentConstant.ERROR;
	}

	CommentConfigEntity getCommentConf(int appId) {
		final Map<Integer, CommentConfigEntity> appCommentConfFinal = this.appCommentConf;
		if (appCommentConfFinal == null || appCommentConfFinal.size() == 0 || appCommentConfFinal.get(appId) == null) {
			throw new ApiException(CommentErrorCode.COMMENT_CODE_COMMENT_NOT_CONFIGED.code(),
					"cant find commentConf，appId:" + appId);
		}
		return appCommentConfFinal.get(appId);
	}
}
