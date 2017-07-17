package com.open.lcp.biz.comment.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.xunlei.mcp.model.ApiException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.xunlei.xlmc.api.service.AutoReloadMinutely;
import com.xunlei.xlmc.comment.dao.CommentMySqlDao;
import com.xunlei.xlmc.comment.domain.CommentConfig;

import static com.xunlei.xlmc.comment.util.CommentConstant.OK;
import static com.xunlei.xlmc.comment.util.CommentConstant.ERROR;


/**
 * 5min更新评论共享配置
 */
@Component
public class AppCommentConfigService implements AutoReloadMinutely {

	private static final Log logger = LogFactory.getLog(AppCommentConfigService.class);

	private Map<Integer, CommentConfigEntity> appCommentConf;

	@Resource
	private CommentConfigDAO commentMySqlDao;

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
			Map<Integer, CommentConfigEntity> newMap = new HashMap<>();
			for (CommentConfigEntity commentConfig : commentConfigs) {
				newMap.put(commentConfig.getAppId(), commentConfig);
			}
			this.appCommentConf = newMap;
			return OK;
		} catch (Exception e) {
            logger.error("reload commentConfig error", e);
		}
		return ERROR;
	}

    CommentConfigEntity getCommentConf(int appId) {
        final Map<Integer, CommentConfigEntity> appCommentConfFinal = this.appCommentConf;
        if (appCommentConfFinal == null || appCommentConfFinal.size()==0 || appCommentConfFinal.get(appId)==null) {
            throw new ApiException(4003, "cant find commentConf，appId:"+appId);
        }
        return appCommentConfFinal.get(appId);
    }
}
