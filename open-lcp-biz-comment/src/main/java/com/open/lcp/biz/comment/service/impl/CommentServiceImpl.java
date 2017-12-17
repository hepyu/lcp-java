package com.open.lcp.biz.comment.service.impl;

import com.google.gson.Gson;
import com.open.lcp.biz.comment.CheckStatus;
import com.open.lcp.biz.comment.CommentConfiguration;
import com.open.lcp.biz.comment.CommentConstant;
import com.open.lcp.biz.comment.CommentErrorCode;
import com.open.lcp.biz.comment.config.AppCommentConfig;
import com.open.lcp.biz.comment.dto.CommentDTO;
import com.open.lcp.biz.comment.facade.resp.CommentAddResp;
import com.open.lcp.biz.comment.facade.resp.CommentReplyResp;
import com.open.lcp.biz.comment.facade.resp.CommentReviewResp;
import com.open.lcp.biz.comment.service.CommentService;
import com.open.lcp.biz.comment.service.dao.db.entity.CommentConfig;
import com.open.lcp.biz.comment.service.dao.db.entity.CommentConfigEntity;
import com.open.lcp.biz.comment.service.dao.db.entity.CommentLocation;
import com.open.lcp.biz.comment.service.dao.hbase.HBaseCommentDAO;
import com.open.lcp.biz.comment.service.dao.hbase.HBaseCommentNoPassCommentDao;
import com.open.lcp.biz.comment.service.dao.hbase.HBaseCommentPassCommentDAO;
import com.open.lcp.biz.comment.service.dao.hbase.HBaseCommentReviewDAO;
import com.open.lcp.biz.comment.service.dao.hbase.impl.column.CommentCheckColumn;
import com.open.lcp.biz.comment.service.dao.hbase.impl.column.CommentColumn;
import com.open.lcp.biz.comment.service.dao.hbase.impl.column.ContentColumn;
import com.open.lcp.biz.comment.service.dao.hbase.impl.column.CountColumn;
import com.open.lcp.biz.comment.service.dao.hbase.impl.column.ExtColumn;
import com.open.lcp.biz.comment.service.dao.hbase.impl.column.IdColumn;
import com.open.lcp.biz.comment.service.dao.hbase.impl.column.UserColumn;
import com.open.lcp.biz.passport.api.AccountInfoApi;
import com.open.lcp.biz.passport.service.dao.PassportUserAccountDAO;
import com.open.lcp.core.common.util.HttpUtil;
import com.open.lcp.core.api.info.BaseUserAccountInfo;
import com.open.lcp.core.framework.IdWorker;
import com.open.lcp.core.framework.api.ApiException;
import com.open.lcp.core.framework.consts.LcpConstants;
import com.open.lcp.dbs.cache.CacheX;
import com.open.lcp.dbs.cache.redis.RedisX;
import com.open.lcp.dbs.cache.ssdb.SSDBX;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class CommentServiceImpl implements CommentService {

	private final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

	private final static Gson gson = LcpConstants.gson;
	// // 推送消息给手雷用的线程池
	// private static final ExecutorService messageExecutor =
	// Executors.newCachedThreadPool();
	@Resource
	private HBaseCommentDAO commentDao;

	@Resource
	private HBaseCommentNoPassCommentDao hbaseCommentNoPassCommentDao;

	@Resource
	private HBaseCommentPassCommentDAO hbaseCommentPassCommentDAO;

	@Resource
	private HBaseCommentReviewDAO hbaseCommentReviewDao;

	// @Resource
	// private CommentConfigDAO commentMySqlDao;
	@Resource
	private IdWorker idWorker;
	@Resource
	private AppCommentConfig appCommentConfig;
	// @Resource(name = SSDB_COMMENT_OLD)
	// private SSDBX ssdbx;
	@Resource(name = CommentConfiguration.BEAN_NAME_COMMENT_DISTRIBUTED_CACHE)
	private CacheX cacheX;
	// @Resource(name = SSDB_COMMENT_FAST)
	// private SSDBX ssdbFast;
	// @Resource(name = COMMENT_LIST_REDIS)
	// private RedisServiceImpl redisService;
	@Resource
	private CloseableHttpClient client;

	@Resource
	private AccountInfoApi accountInfoApi;
	// @Resource
	// private UserMessageService userMessageService;
	// @Resource
	// private RaUserInfoDao raUserInfoDao;
	// @Resource
	// private FileService fileService;
	// @Resource
	// private RaUserService raUserService;
	// @Resource
	// private CommentPraiserDeviceDAO commentPraiserDeviceDao;
	// @Resource
	// private CommentPraiserUserDAO commentPraiserUserDao;
	// @Resource
	// private FeedService feedService;
	// @Resource(name = "adminSecureManager")
	// private SecureManager secureManager;
	// @Resource
	// private UserAccountService userAccountService;
	// // 发评论的时候存储kafka的线程池
	// private static final ExecutorService kafkaExecutor =
	// Executors.newCachedThreadPool();
	// @Resource(name = COMMENT_KAFKA_PRODUCER)
	// private Producer<String, String> commentKafkaProducer;
	// @Value("${fileservice.notify.userId}")
	// private long systemUserId;
	// @Value("${mobile.xunlei.send.message.url}")
	// private String messageUrl;
	// @Value("${act.appId}")
	// private int actAppId;
	// @Value("${act.appSecretKey}")
	// private String actAppSecretKey;

	@Override
	public CommentAddResp addComment(int appId, final int typeId, String tid, final Long cid, String ip, String device,
			String comment, BaseUserAccountInfo user, String sourceId, String triggerId, String clientPort,
			String recommendPlatform, String downLoadSpeed, boolean isAnonymous, String bandwidth,
			String extParamsJson) {
		long userId = user.getUserId();
		// 验证是否被禁言，是否是评论重入
		CommentAddResp commentAddResp = this.validateBeforeAddComment(userId, triggerId, comment);
		if (commentAddResp != null) {
			return commentAddResp;
		}
		CommentConfigEntity commentConf = appCommentConfig.getCommentConf(appId);
		int appCommentId = commentConf.getAppCommentId();

		// 评论id生成规则为从高到低递减,即最新的id最小
		long newCommentId = Long.MAX_VALUE - idWorker.nextId();

		// 评论id不为空时,该条评论属于回复评论,并且需要挖出被回复的评论信息，如果循环回复，则挖的层数取决于应用的配置
		boolean isReply = false;
		List<CommentReplyResp> replyResps = null;
		long replyCommentUid = 0L;
		if (cid != null) {
			isReply = true;
			// 查询被回复人的评论信息
			replyResps = listReply(appCommentId, typeId, tid, cid, commentConf);
			if (replyResps != null && replyResps.size() != 0) {
				// 这里存了一串 有回复的，有被回复的，傻傻分不清
				replyCommentUid = replyResps.get(replyResps.size() - 1).getUid();
			} else {
				// 被回复的评论未审核通过 这种情况在回复自己的时候发生
				isReply = false;
			}
		}
		// 查询用户类型设置评论类型
		String commentType = "";
		// 只有短视频需要打标给范刚计算有料值
		if (typeId == 1) {
			try {
				commentType = buildCommentTyle(newCommentId, appId, userId, isReply, recommendPlatform, replyCommentUid,
						sourceId);
			} catch (Exception e) {
				logger.warn("build comment type error", e);
			}
		}
		long time = System.currentTimeMillis();
		final IdColumn idColumn = createIdColumn(appCommentId, appId, typeId, tid, newCommentId, cid, null, ip, time,
				device, sourceId, clientPort, commentType);
		final ContentColumn contentColumn = createContentColumn(comment, replyResps);
		final UserColumn userColumn = createUserColumn(user);
		CountColumn countColumn = createCountColumn(0, 0, 0);
		ExtColumn extColumn = createExtColumn(commentConf.getLevel(), downLoadSpeed, isAnonymous, bandwidth,
				extParamsJson);

		String idColumnValue = gson.toJson(idColumn);
		String userColumnValue = gson.toJson(userColumn);
		String commentColumnValue = gson.toJson(contentColumn);
		String countColumnValue = gson.toJson(countColumn);
		String extColumnValue = gson.toJson(extColumn);

		List<CommentCheckColumn> commentColumns = new ArrayList<CommentCheckColumn>(1);
		CommentCheckColumn commentColumn = new CommentCheckColumn();
		commentColumn.setCommentId(newCommentId);
		commentColumn.setIdColumnValue(idColumnValue);
		commentColumn.setUserColumnValue(userColumnValue);
		commentColumn.setCommentColumnValue(commentColumnValue);
		commentColumn.setCountColumnValue(countColumnValue);
		commentColumn.setExtColumnValue(extColumnValue);
		commentColumn.setAuthor(user.getNickName());
		commentColumn.setAuthorId(user.getUserId());
		commentColumn.setContent(comment);

		CheckStatus checkStatus = verifyComment(comment);

		if (checkStatus == CheckStatus.YES) {
			commentColumn.setChecker("spam");
			commentColumn.setCheckTime(time);
			commentColumns.add(commentColumn);
			try {
				if (hbaseCommentNoPassCommentDao.addCheckNoPassComments(typeId, commentColumns)) {
					return commentAddResp(0, newCommentId, true);
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new ApiException(CommentErrorCode.COMMENT_CODE_ADD_CHECK_NO_PASS_COMMENT_ERROR.code());
			}
		} else {
			commentColumn.setChecker("SW");
			commentColumn.setCheckTime(time);
			commentColumns.add(commentColumn);
			try {
				if (hbaseCommentReviewDao.addReviewComments(typeId, commentColumns)) {
					addReviewCommentCache(idColumn, contentColumn, userColumn, extColumn);
					return commentAddResp(0, newCommentId, true);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new ApiException(CommentErrorCode.COMMENT_CODE_ADD_REVIEW_COMMENT_ERROR.code());
			}
		}
		return commentAddResp(4009, null, false);
	}

	@Override
	public boolean del(int appId, int typeId, String tid, Long cid, long userId) {

//		CommentConfigEntity commentConf = appCommentConfig.getCommentConf(appId);
//		int appCommentId = commentConf.getAppCommentId();
//
//		// 待审核评论删除
//		if (delReviewComment(appCommentId, typeId, tid, cid, userId)) {
//			return true;
//		}
//		// 审核通过评论删除
//		CommentColumn commentColumn = commentDao.getComment(appCommentId, typeId, tid, cid);
//		if (commentColumn == null) {
//			return false;
//		}
//		IdColumn idColumn = gson.fromJson(commentColumn.getIdColumnValue(), IdColumn.class);
//		if (logger.isDebugEnabled()) {
//			if (idColumn != null) {
//				logger.debug("comment del idColumn {}", idColumn);
//			} else {
//				logger.debug("comment del commentColumn {}", commentColumn.getIdColumnValue());
//			}
//		}
//		UserColumn userColumn = gson.fromJson(commentColumn.getUserColumnValue(), UserColumn.class);
//		if (userColumn.getUid() == userId) {
//			ExtColumn extColumn = gson.fromJson(commentColumn.getExtColumnValue(), ExtColumn.class);
//			if (commentDao.del(appCommentId, typeId, tid, cid)
//					&& hbaseCommentPassCommentDAO.delCheckPassComments(typeId, cid)
//					&& commentDao.delUserComment(userId, cid)) {
//				List<Long> replyerCids = extColumn.getReplyerCids();
//				if (replyerCids != null && replyerCids.size() > 0) {
//					for (Long replyerCid : replyerCids) {
//						CommentColumn replyCommentColumn = commentDao.getComment(appCommentId, typeId, tid, replyerCid);
//						if (replyCommentColumn != null) {
//							ContentColumn contentColumn = gson.fromJson(replyCommentColumn.getCommentColumnValue(),
//									ContentColumn.class);
//							List<CommentReplyResp> replys = contentColumn.getReply();
//							if (replys != null && replys.size() > 0) {
//								replys.get(0).setContent("该评论已删除!");
//								replys.get(0).setCid(-1L);
//								commentDao.delReplyComment(appCommentId, typeId, tid, replyerCid,
//										gson.toJson(contentColumn));
//							}
//						}
//					}
//				}
//				if (idColumn != null && idColumn.getTypeId() == 1) {
//					String sourceId = idColumn.getSourceId();
//					if (StringUtils.isNotBlank(sourceId) && NumberUtils.isDigits(sourceId)) {
//						cacheX.del(String.format(COMMENT_COUNT, tid));
//					}
//				}
//				if (delCache(appCommentId, typeId, tid, cid, extColumn.getReplyerCids())) {
//					if (typeId == 1 && idColumn != null && idColumn.getTypeId() == 1) {
//						String sourceId = idColumn.getSourceId();
//						if (NumberUtils.isDigits(sourceId)) {
//							long videoId = Long.parseLong(sourceId);
//							fileService.updateCommentNum(videoId, this.commentCount(tid, videoId));
//						}
//					} else if (typeId == 4) {
//						// 对下载资源的删除处理，更新排名，处理一个资源下同一用户的多个评论速度不同的情况
//						delCommentSpeedCache(userId, tid);
//					}
//				}
//			}
//		} else {
//			throw new ApiException(1, "cant del others comment");
//		}
		return false;
	}

	// private boolean delCache(int resourceAppId, int typeId, String tid, long
	// cid, List<Long> replyerCids) {
	// String hKey = getCommentHKey(resourceAppId, typeId, tid);
	// ssdbx.hdel(hKey, cid);
	// ssdbx.zdel(getCommentIdZKey(resourceAppId, typeId, tid, "new"), cid);
	// redisService.del(String.format(COMMENT_COUNT, tid));
	// delPageCache(tid);
	// ssdbx.zdel(getCommentIdZKey(resourceAppId, typeId, tid, "hot"), cid);
	// // 多层盖楼评论删除待开发
	// if (replyerCids != null && replyerCids.size() > 0) {
	// Long[] ids = new Long[replyerCids.size()];
	// replyerCids.toArray(ids);
	// Map<Long, CommentResp> comments = ssdbx.hmget(hKey, ids, Long.class,
	// CommentResp.class);
	// if (comments == null || comments.size() < 1) {
	// return true;
	// }
	// for (Entry<Long, CommentResp> e : comments.entrySet()) {
	// List<CommentReplyResp> cmReplys = e.getValue().getReplys();
	// if (cmReplys != null && cmReplys.size() > 0) {
	// cmReplys.get(0).setContent("该评论已删除!");
	// cmReplys.get(0).setCid(-1L);
	// ssdbx.hset(hKey, e.getKey(), e.getValue());
	// }
	// }
	// }
	// return true;
	// }

	// private void sendKafkaToAudit(String auditUserNmae,
	// CommentAuditStatusEnum commentAuditStatusEnum,
	// IdColumn idColumn, UserColumn userColumn, String content) {
	//
	// CommentAudit commentAudit = this.createCommentAudit(auditUserNmae,
	// commentAuditStatusEnum, idColumn, userColumn,
	// content);
	// String id = String.valueOf(commentAudit.getCid());
	// String value = gson.toJson(commentAudit);
	// logger.debug("kafka send id:{}, value:{}", id, value);
	// commentKafkaProducer.send(new ProducerRecord<>(COMMENT_TOPIC, id,
	// value));
	// }
	//
	// private CommentAudit createCommentAudit(String auditUserName,
	// CommentAuditStatusEnum commentAuditStatusEnum,
	// IdColumn idColumn, UserColumn userColumn, String content) {
	// CommentAudit commentAudit = new CommentAudit();
	// commentAudit.setTypeId(idColumn.getTypeId());
	// commentAudit.setCid(idColumn.getCid());
	// commentAudit.setIp(idColumn.getIp());
	// commentAudit.setAuditTime(System.currentTimeMillis());
	// commentAudit.setAuditUserName(auditUserName);
	// commentAudit.setAuthor(userColumn.getName());
	// commentAudit.setCommentTime(idColumn.getTime());
	// commentAudit.setContent(content);
	// commentAudit.setUid(userColumn.getUid());
	// commentAudit.setGcid(idColumn.getTid());
	// commentAudit.setForbiddenKeys(SecurityUtil.getKeyWords(secureManager,
	// content, SecurityUtil.commentBizNum));
	// commentAudit.setStatus(commentAuditStatusEnum.getStatus());
	// if (idColumn.getTypeId() == CommentTyleEnum.SHORT_VIDEO.getTypeId()) {
	// if (NumberUtils.isDigits(idColumn.getSourceId())) {
	// HotVideoDTO hotVideoDTO = fileService.queryEnabled(idColumn.getAppId(),
	// Long.parseLong(idColumn.getSourceId()), CdnType.QINIU, false);
	// commentAudit.setVideoId(Long.parseLong(idColumn.getSourceId()));
	// if (hotVideoDTO != null) {
	// commentAudit.setPlayUrl(hotVideoDTO.getPlayUrl());
	// }
	// }
	// }
	// return commentAudit;
	// }
	//
	// private AdminCommentDTO createAdminCommentDTO(IdColumn idColumn,
	// UserColumn userColumn,
	// ContentColumn contentColumn) {
	// AdminCommentDTO adminCommentDTO = new AdminCommentDTO();
	// adminCommentDTO.setRowkey(String.valueOf(idColumn.getCid()));
	// adminCommentDTO.setVideoId(idColumn.getSourceId());
	// adminCommentDTO.setIp(idColumn.getIp());
	// adminCommentDTO.setAuditTime(DateUtil.dateToString(new Date()));
	// adminCommentDTO.setAuditUserName(CommentAuditUserNameEnum.SW.getName());
	// adminCommentDTO.setAuthor(userColumn.getName());
	// adminCommentDTO.setCommentTime(DateUtil.dateToString(new
	// Date(idColumn.getTime())));
	// adminCommentDTO.setContent(contentColumn.getContent());
	// adminCommentDTO.setUid(String.valueOf(userColumn.getUid()));
	// adminCommentDTO.setGcid(idColumn.getTid());
	// List<CommentReplyResp> replys = contentColumn.getReply();
	// if (CollectionUtils.isNotEmpty(replys)) {
	// adminCommentDTO.setReplyUid(String.valueOf(replys.get(0).getUid()));
	// }
	// adminCommentDTO.setForbiddenKeys(
	// SecurityUtil.getKeyWords(secureManager, contentColumn.getContent(),
	// SecurityUtil.commentBizNum));
	// if (idColumn.getTypeId() == CommentTyleEnum.SHORT_VIDEO.getTypeId()) {
	// if (NumberUtils.isDigits(idColumn.getSourceId())) {
	// HotVideoDTO hotVideoDTO = fileService.queryEnabled(idColumn.getAppId(),
	// Long.parseLong(idColumn.getSourceId()), CdnType.QINIU, false);
	// if (hotVideoDTO != null) {
	// adminCommentDTO.setPlayUrl(hotVideoDTO.getPlayUrl());
	// }
	// }
	// }
	// return adminCommentDTO;
	// }
	//
	private CommentAddResp validateBeforeAddComment(long userId, String triggerId, String comment) {
		// 禁言验证
		Long end = cacheX.get(String.format(CommentConstant.COMMENT_SILENCED_KEY, userId), Long.class);
		if (end != null && end != 0) {
			if (end == CommentConstant.FOR_EVER) {
				return commentAddResp(CommentErrorCode.COMMENT_CODE_COMMENT_USER_IS_SLIENCED_FOREVER.code(), null,
						false);
			} else {
				return commentAddResp(CommentErrorCode.COMMENT_CODE_COMMENT_USER_IS_SLIENCED.code(), null, false);
			}
		}
		// 长度验证
		if (comment.length() > 140) {
			return commentAddResp(CommentErrorCode.COMMENT_CODE_CONTENT_TOO_LONG.code(), null, false);
		}
		// 重入验证
		String triggerKey = String.format(CommentConstant.COMMENT_TRIGGER_KEY, userId, triggerId);
		if (cacheX.setnx(triggerKey, String.valueOf(1)) < 1) {
			return commentAddResp(CommentErrorCode.COMMENT_CODE_CONTENT_REPEAT.code(), null, false);
		} else {
			cacheX.expire(triggerKey, 3600 * 24);
		}
		return null;
	}

	/**
	 * 构建评论类型
	 */
	private String buildCommentTyle(long newCommentId, int appId, long userId, boolean isReply,
			String recommendPlatform, long replyCommentUid, String sourceId) {
		// String commentType;
		// String userType = accountInfoApi.getUserType(userId);
		// String replyType = "other";
		// String isAuthor = "other";
		// if (isReply && userId == replyCommentUid) {
		// replyType = "self";
		// }
		// if (NumberUtils.isDigits(sourceId)) {
		// HotVideoDTO hotVideoDTO =
		// fileService.queryAllState(Long.parseLong(sourceId));
		// if (hotVideoDTO != null && userId == hotVideoDTO.getUserId()) {
		// isAuthor = "author";
		// }
		// }
		// logger.debug(
		// "commentType:newCommentId" + newCommentId + "userId:" + userId + "
		// replyCommentUid:" + replyCommentUid);
		// commentType = userType + "-" + (appId == 12 ? "pq" : isReply ? "hf" :
		// "pl") + "-" + recommendPlatform + "-"
		// + replyType + "-" + isAuthor;
		return "default";
	}

	private CommentAddResp commentAddResp(int result, Long cid, boolean isSucess) {
		CommentAddResp resp = new CommentAddResp();
		resp.setResult(result);
		resp.setCid(cid);
		resp.setIsPdRiew(isSucess);
		return resp;
	}

	//
	// /**
	// * 资源的总评论回复数
	// */
	// @Override
	// public int getRcount(int appId, int typeId, String tid) {
	// return this.commentCountTid(tid, typeId);
	// }
	// // 暂时封闭hbase回原
	// // private long loadCache(int resourceAppId, int typeId, String tid)
	// throws
	// // IOException {
	// // List<CommentResp> comments = getComments(resourceAppId, typeId, tid);
	// // if (comments == null || comments.size() < 1) {
	// // return 0;
	// // }
	// // long rcount = commentDao.getCommentCount(resourceAppId, typeId, tid);
	// // CommentResp[] resps = new CommentResp[comments.size()];
	// // comments.toArray(resps);
	// // addCommentCache(resourceAppId, typeId, tid, resps);
	// // return rcount;
	// // }
	//
	// private void addCommentCache(int resourceAppId, int typeId, String tid,
	// CommentResp... resps) {
	// if (resps == null) {
	// return;
	// }
	// String idNewKey = getCommentIdZKey(resourceAppId, typeId, tid, "new");
	// String hKey = getCommentHKey(resourceAppId, typeId, tid);
	//
	// Map<Long, Long> commentNewIdMap = new HashMap<>(resps.length);
	// Map<Long, CommentResp> commentMap = new HashMap<>(resps.length);
	//
	// for (CommentResp resp : resps) {
	// commentNewIdMap.put(resp.getCid(), resp.getCid());
	// commentMap.put(resp.getCid(), resp);
	// }
	// ssdbx.multi_zset(idNewKey, commentNewIdMap);
	// redisService.del(String.format(COMMENT_COUNT, tid));
	// ssdbCommentNew.del(String.format(EMPTY_TID, tid));
	// delPageCache(tid);
	// ssdbx.multi_hset(hKey, commentMap);
	//
	// // 更新资源的用户下载速度排序缓存
	// if (typeId == 4) {
	// for (CommentResp resp : resps) {
	// long userId = resp.getUid();
	// this.delCommentSpeedCache(userId, tid);
	// }
	// }
	// // 做cid的全量缓存备用
	// for (CommentResp resp : resps) {
	// ssdbCommentNew.set("comment-kv-" + resp.getCid(), resp);
	// }
	// }
	//
	// private void delCommentSpeedCache(long userId, String tid) {
	// redisService.del(String.format(COMMENT_SPEED_Z, tid));
	// redisService.del(String.format(COMMENT_TID_USER_Z, tid, userId));
	// }
	//

	private boolean delReviewComment(int resourceAppId, int typeId, String tid, Long cid, Long uid)
			throws IllegalArgumentException, IOException {
		String rKey = getCommentUserReviewKey(uid);
		long rCid = cacheX.zget(rKey, cid);
		if (rCid > 0) {
			cacheX.zdel(rKey, cid);
			cacheX.del(rKey);
			String hKey = getCommentHKey(resourceAppId, typeId, tid);
			cacheX.hdel(hKey, cid);
			hbaseCommentReviewDao.delReviewComments(typeId, cid);
			return true;
		}
		return false;
	}

	//
	/**
	 * 用户发的评论未经审核通过先存储在待审核列表，只显示在该用户资源的评论页里
	 */
	private void addReviewCommentCache(IdColumn idColumn, ContentColumn contentColumn, UserColumn userColumn,
			ExtColumn extColumn) {
		CommentReviewResp resp = new CommentReviewResp();
		resp.setAppId(idColumn.getAppId());
		resp.setTypeId(idColumn.getTypeId());
		resp.setTid(idColumn.getTid());
		resp.setCid(idColumn.getCid());
		resp.setComment(contentColumn.getContent());
		if (contentColumn.getReply() != null && contentColumn.getReply().size() > 0) {
			resp.setReplys(contentColumn.getReply());
		}
		resp.setDevice(idColumn.getDevice());
		resp.setSourceId(idColumn.getSourceId());
		resp.setPo(idColumn.getPo());
		resp.setCi(idColumn.getCi());
		resp.setGcount(0);
		resp.setRcount(0);
		resp.setScount(0);
		resp.setTime(idColumn.getTime());
		resp.setUid(userColumn.getUid());
		resp.setUserName(userColumn.getName());
		resp.setUserImg(userColumn.getImg());
		String key = getCommentUserReviewKey(userColumn.getUid());
		String hKey = getCommentHKey(idColumn.getAppId(), idColumn.getTypeId(), idColumn.getTid());
		if (extColumn != null) {
			resp.setDownLoadSpeed(extColumn.getDownLoadSpeed());
			resp.setAnonymous(extColumn.isAnonymous());
			resp.setBandwidth(extColumn.getBandwidth());
			resp.setExtParamsJson(extColumn.getExeParamsJson());
		}
		cacheX.zset(key, idColumn.getCid(), idColumn.getCid());
		cacheX.del(key);
		cacheX.hset(hKey, idColumn.getCid(), resp);
	}

	/**
	 * 删除用户待审核的评论
	 */
	private void delReviewCommentCache(long uid, long cid) {
		String key = getCommentUserReviewKey(uid);
		logger.debug("delReviewCommentCache start {} {}", uid, cid);
		cacheX.zdel(key, cid);
		if (cid == cacheX.zget(key, cid)) {
			logger.error("delReviewCommentCache ssdbxZdel error");
		} else {
			logger.info("delReviewCommentCache ssdbxZdel ok");
		}
		logger.debug("delReviewCommentCache end {} {}", uid, cid);
		cacheX.del(key);
	}

	/**
	 * 安全系统过滤评论
	 */
	private CheckStatus verifyComment(String comment) {
		return CheckStatus.YES;
		// CommentDTO commentDTO = new CommentDTO();
		// commentDTO.setContentColumn(comment);
		// commentDTO.setBizNum("00003");
		// RespDTO resp = secureManager.verfiyForbiddenKeyword(commentDTO);
		// return resp.getCheckStatus();
	}

	/**
	 * id 列 存储 ip 时间戳 设备信息
	 */
	private IdColumn createIdColumn(int appId, int sourceAppId, int typeId, String tid, long newCommentId,
			Long replyCid, Long replyRid, String ip, long time, String device, String sourceId, String clientPort,
			String commentType) {

		String po = "";
		String ci = "";
		CommentLocation location = this.transferIp(ip);
		if (location != null) {
			po = location.getProvince();
			ci = location.getCity();
		}
		IdColumn idColumn = new IdColumn();
		idColumn.setAppId(appId);
		idColumn.setTypeId(typeId);
		idColumn.setTid(tid);
		idColumn.setCid(newCommentId);
		idColumn.setReplyCid(replyCid);
		idColumn.setReplyRid(replyRid);
		idColumn.setIp(ip);
		idColumn.setPo(po);
		idColumn.setCi(ci);
		idColumn.setTime(time);
		idColumn.setDevice(device);
		idColumn.setSourceId(sourceId);
		idColumn.setSourceAppId(sourceAppId);
		idColumn.setClientPort(clientPort);
		idColumn.setCommentType(commentType);
		return idColumn;
	}

	private CommentLocation transferIp(String ip) {
		String url = CommentConstant.IP_SEARCH_SINA_PRE + ip;
		try {
			String result = HttpUtil.get(client, url);
			if (StringUtils.isNotBlank(result)) {
				return gson.fromJson(result, CommentLocation.class);
			}
		} catch (Exception e) {
			logger.warn("transferIp error {}", ip, e);
			return null;
		}
		return null;
	}

	/**
	 * 内容列
	 */
	private ContentColumn createContentColumn(String content, List<CommentReplyResp> replyResps) {
		ContentColumn contentColumnValue = new ContentColumn();
		contentColumnValue.setContent(content);
		if (replyResps != null && replyResps.size() > 0) {
			contentColumnValue.setReply(replyResps);
		}
		return contentColumnValue;
	}

	/**
	 * 评论用户列
	 */
	private UserColumn createUserColumn(BaseUserAccountInfo user) {
		UserColumn userColumn = new UserColumn();
		userColumn.setUid(user.getUserId());
		userColumn.setName(user.getNickName());
		userColumn.setImg(user.getAvatar());
		return userColumn;

	}

	/**
	 * 评论计数列
	 */
	private CountColumn createCountColumn(int gcount, int rcount, int scount) {
		CountColumn countCloumn = new CountColumn();
		countCloumn.setGcount(gcount);
		countCloumn.setRcount(rcount);
		countCloumn.setScount(scount);
		return countCloumn;
	}

	/**
	 * 评论扩展列
	 */
	private ExtColumn createExtColumn(int level, String downLoadSpeed, boolean isAnonymous, String bandwidth,
			String exeParamsJson) {
		ExtColumn extColumn = new ExtColumn();
		extColumn.setLevel(level);
		extColumn.setDownLoadSpeed(downLoadSpeed);
		extColumn.setAnonymous(isAnonymous);
		extColumn.setBandwidth(bandwidth);
		extColumn.setExeParamsJson(exeParamsJson);
		return extColumn;
	}

	/**
	 * 根据回复的评论id,拉取被回复的评论相关信息
	 */
	private List<CommentReplyResp> listReply(int resourceAppId, int typeId, String tid, Long replyCid,
			CommentConfigEntity commentConf) {
		CommentColumn column;
		column = commentDao.getComment(resourceAppId, typeId, tid, replyCid);
		if (column == null) {
			return null;
		}
		ContentColumn contentColumn = gson.fromJson(column.getCommentColumnValue(), ContentColumn.class);
		UserColumn userColumn = gson.fromJson(column.getUserColumnValue(), UserColumn.class);
		CommentReplyResp reply = new CommentReplyResp();
		reply.setContent(contentColumn.getContent());
		reply.setCid(replyCid);
		reply.setUid(userColumn.getUid());
		reply.setUser(userColumn.getName());
		reply.setUserImg(userColumn.getImg());
		List<CommentReplyResp> replyResps = contentColumn.getReply();
		if (replyResps == null) {
			replyResps = new ArrayList<CommentReplyResp>(1);
			replyResps.add(reply);
		} else {
			replyResps.add(0, reply);
			if (replyResps.size() > commentConf.getFloorLevel()) {
				replyResps = replyResps.subList(0, commentConf.getFloorLevel());
			}
		}
		return replyResps;
	}

	// /**
	// * 被回复人ext列里记录回复的评论id
	// */
	// private void addCommentReplyerId(int resourceAppId, int typeId, String
	// tid, long replyCid, long newCid)
	// throws IllegalArgumentException, IOException {
	//
	// CommentColumn column = commentDao.getComment(resourceAppId, typeId, tid,
	// replyCid);
	// if (column == null) {
	// return;
	// }
	// ExtColumn extColumn = gson.fromJson(column.getExtColumnValue(),
	// ExtColumn.class);
	// List<Long> replyerCids = extColumn.getReplyerCids();
	// if (replyerCids == null) {
	// replyerCids = new ArrayList<>(1);
	// }
	// replyerCids.add(newCid);
	// extColumn.setReplyerCids(replyerCids);
	// commentDao.addCommentReplyer(resourceAppId, typeId, tid, replyCid,
	// gson.toJson(extColumn));
	// }
	//
	// @Override
	// public CommentListResp listComment(int appId, int typeId, String tid,
	// long lastId, int pageSize, String loadType,
	// String category, Long uid, Long startTime, String deviceId) throws
	// IOException {
	// // 接上级要求，干掉所有最热的评论
	// if ("hot".equals(category) && !NEED_HOT.contains(appId)) {
	// return this.getEmpty(3, typeId, tid, uid, false);
	// }
	// // 获取评论app配置参数
	// CommentConfig commentConf =
	// appCommentConfigService.getCommentConf(appId);
	// int appCommentId = commentConf.getAppCommentId();
	// boolean isReviewAdd = true;
	// if ("hot".equals(category) || lastId > 0) {
	// isReviewAdd = false;
	// }
	// int totalZsize = this.commentCountTid(tid, typeId);
	// // 抛异常直接返回空不再访问hbase
	// if (totalZsize == -1) {
	// logger.warn("listComment return null cause of cache exception!");
	// return this.getEmpty(appCommentId, typeId, tid, uid, isReviewAdd);
	// }
	// if (totalZsize == 0) {
	// return this.getEmpty(appCommentId, typeId, tid, uid, isReviewAdd);
	// // // 缓存没有时 从hbase中加载最新的100条评论
	// // zsize = (int) loadCache(appCommentId, typeId, tid);
	// // if (zsize < 1) {
	// // return getEmpty(appCommentId, typeId, tid, uid, isReviewAdd);
	// // }else{
	// // redisService.setex(String.format(COMMENT_TID_COUNT, tid),
	// // COMMENT_TID_COUNT_EXPIRE, zsize);
	// // }
	// }
	// Map<Long, Long> idMap;
	// String idKey = getCommentIdZKey(appCommentId, typeId, tid, "new");
	// String commentfirstPageKey = String.format(COMMENT_NEW_PAGE, tid);
	// String commentTidCountKey = String.format(COMMENT_TID_COUNT_SNAPSHOT,
	// tid);
	// boolean refreshCache = false;
	// Long[] idArray = null;
	// final CommandContext ctx = McpThreadLocal.thCommandContext.get();
	// int videoZsize = 0;
	// if (typeId == 1) {
	// Long videoId = 0L;
	// if (startTime != null) {
	// videoId = startTime;
	// // 短视频的评论以videoId分段
	// videoZsize = this.commentCount(tid, videoId);
	// } else {
	// videoZsize = totalZsize;
	// }
	// if ("new".equals(category)) {
	// // 检查首页缓存 评论总数不变的情况下直接用缓存 评论总数变了就复写
	// if (lastId == 0) {
	// McpUtils.addStat("clGetPageS", System.currentTimeMillis());
	// CommentListResp resp = redisService.get(commentfirstPageKey,
	// CommentListResp.class);
	// McpUtils.addStat("clGetPageE", System.currentTimeMillis());
	// Integer commentTidCount = redisService.get(commentTidCountKey,
	// Integer.class);
	// McpUtils.addStat("clGetTidCountE", System.currentTimeMillis());
	// if (resp != null && commentTidCount != null && totalZsize ==
	// commentTidCount) {
	// McpUtils.addStat("clGetCacheS", System.currentTimeMillis());
	// List<CommentResp> commentResps = resp.getConmments();
	// this.timefilter(videoId, commentResps);
	// if (commentResps.size() > pageSize) {
	// commentResps = commentResps.subList(0, pageSize);
	// }
	// if (commentResps.size() <= pageSize) {
	// McpUtils.addStat("clGetCacheRS", System.currentTimeMillis());
	// addReviewComment(appCommentId, typeId, tid, uid, commentResps);
	// addPraiser(uid, commentResps, deviceId);
	// resp.setConmments(commentResps);
	// McpUtils.addStat("clGetCacheE", System.currentTimeMillis());
	// return resp;
	// }
	// }
	// // 按照最大pageSize回原
	// pageSize = 50;
	// refreshCache = true;
	// }
	// int offset = 0;
	// if ("loadmore".equals(loadType)) {
	// if (lastId > 0) {
	// offset = (int) ssdbx.zrank(idKey, lastId);
	// if (offset < 0) {
	// return getEmpty(appCommentId, typeId, tid, uid, isReviewAdd);
	// }
	// offset++;
	// }
	// }
	// McpUtils.addStat("clZrangeS", System.currentTimeMillis());
	// idMap = ssdbx.zrange(idKey, offset, pageSize, Long.class);
	// McpUtils.addStat("clZrangeE", System.currentTimeMillis());
	// if (idMap == null || idMap.size() < 1) {
	// return getEmpty(appCommentId, typeId, tid, uid, isReviewAdd);
	// }
	// timefilter(videoId, idMap);
	// idArray = new Long[idMap.size()];
	// idMap.keySet().toArray(idArray);
	// } else if ("hot".equals(category)) {
	// String commentHotKey = String.format(COMMENT_HOT_RESP, tid);
	// McpUtils.addStat("clGetHotCacheS", System.currentTimeMillis());
	// CommentListResp resp = redisService.get(commentHotKey,
	// CommentListResp.class);
	// McpUtils.addStat("clGetHotCacheE", System.currentTimeMillis());
	// if (resp != null) {
	// return resp;
	// } else {
	// McpUtils.addStat("clCalcuHotS", System.currentTimeMillis());
	// idArray = calculateHot(appId, videoZsize, appCommentId, typeId, tid);
	// McpUtils.addStat("clCalcuHotE", System.currentTimeMillis());
	// if (idArray == null) {
	// return getEmpty(appCommentId, typeId, tid, uid, videoZsize, isReviewAdd);
	// } else {
	// resp = new CommentListResp();
	// String hKey = getCommentHKey(appCommentId, typeId, tid);
	// McpUtils.addStat("clHotHmgetS", System.currentTimeMillis());
	// Map<Long, CommentResp> commentMap = ssdbx.hmget(hKey, idArray,
	// Long.class, CommentResp.class);
	// McpUtils.addStat("clHotHmgetE", System.currentTimeMillis());
	// if (commentMap == null || commentMap.size() < 1) {
	// return getEmpty(appCommentId, typeId, tid, uid, isReviewAdd);
	// }
	// Collection<CommentResp> commentValues = commentMap.values();
	// List<CommentResp> commentResps = new ArrayList<>(commentValues);
	// addPraiser(uid, idArray, commentResps, deviceId);
	// addUserType(commentResps);
	// resp.setConmments(commentResps);
	// resp.setRcount(videoZsize);
	// resp.setTid(tid);
	// McpUtils.addStat("clHotSetexS", System.currentTimeMillis());
	// redisService.setex(commentHotKey, COMMENT_HOT_EXPIRE, resp);
	// McpUtils.addStat("clHotSetexE", System.currentTimeMillis());
	// return resp;
	// }
	// }
	// } else {
	// return getEmpty(appCommentId, typeId, tid, uid, isReviewAdd);
	// }
	// } else if (typeId == 4) {
	// if ("new".equals(category)) {
	// // 检查首页缓存 评论总数不变的情况下直接用缓存 评论总数变了就复写
	// if (lastId == 0) {
	// McpUtils.addStat("clGetPageS", System.currentTimeMillis());
	// CommentListResp resp = redisService.get(commentfirstPageKey,
	// CommentListResp.class);
	// McpUtils.addStat("clGetPageE", System.currentTimeMillis());
	// Integer commentTidCount = redisService.get(commentTidCountKey,
	// Integer.class);
	// McpUtils.addStat("clGetTidCountE", System.currentTimeMillis());
	// if (resp != null && commentTidCount != null && totalZsize ==
	// commentTidCount) {
	// McpUtils.addStat("clGetCacheS", System.currentTimeMillis());
	// List<CommentResp> commentResps = resp.getConmments();
	// if (commentResps.size() > pageSize) {
	// commentResps = commentResps.subList(0, pageSize);
	// }
	// if (commentResps.size() <= pageSize) {
	// McpUtils.addStat("clGetCacheRS", System.currentTimeMillis());
	// addReviewComment(appCommentId, typeId, tid, uid, commentResps);
	// addPraiser(uid, commentResps, deviceId);
	// resp.setConmments(commentResps);
	// McpUtils.addStat("clGetCacheE", System.currentTimeMillis());
	// return resp;
	// }
	// }
	// // 按照最大pageSize回原
	// pageSize = 50;
	// refreshCache = true;
	// }
	// int offset = 0;
	// if ("loadmore".equals(loadType)) {
	// if (lastId > 0) {
	// offset = (int) ssdbx.zrank(idKey, lastId);
	// if (offset < 0) {
	// return getEmpty(appCommentId, typeId, tid, uid, isReviewAdd);
	// }
	// offset++;
	// }
	// }
	// idMap = ssdbx.zrange(idKey, offset, pageSize, Long.class);
	// if (idMap == null || idMap.size() < 1) {
	// return getEmpty(appCommentId, typeId, tid, uid, isReviewAdd);
	// }
	// idArray = new Long[idMap.size()];
	// idMap.keySet().toArray(idArray);
	// } else if ("hot".equals(category)) {
	// String commentHotKey = String.format(COMMENT_HOT_RESP, tid);
	// McpUtils.addStat("clGetHotCacheS", System.currentTimeMillis());
	// CommentListResp resp = redisService.get(commentHotKey,
	// CommentListResp.class);
	// McpUtils.addStat("clGetHotCacheE", System.currentTimeMillis());
	// if (resp != null) {
	// return resp;
	// } else {
	// McpUtils.addStat("clCalcuHotS", System.currentTimeMillis());
	// idArray = calculateHot(appId, totalZsize, appCommentId, typeId, tid);
	// McpUtils.addStat("clCalcuHotE", System.currentTimeMillis());
	// if (idArray == null) {
	// return getEmpty(appCommentId, typeId, tid, uid, totalZsize, isReviewAdd);
	// } else {
	// resp = new CommentListResp();
	// String hKey = getCommentHKey(appCommentId, typeId, tid);
	// McpUtils.addStat("clHotHmgetS", System.currentTimeMillis());
	// Map<Long, CommentResp> commentMap = ssdbx.hmget(hKey, idArray,
	// Long.class, CommentResp.class);
	// McpUtils.addStat("clHotHmgetE", System.currentTimeMillis());
	// if (commentMap == null || commentMap.size() < 1) {
	// return getEmpty(appCommentId, typeId, tid, uid, isReviewAdd);
	// }
	// Collection<CommentResp> commentValues = commentMap.values();
	// List<CommentResp> commentResps = new ArrayList<>(commentValues);
	// addPraiser(uid, idArray, commentResps, deviceId);
	// addUserType(commentResps);
	// resp.setConmments(commentResps);
	// resp.setRcount(totalZsize);
	// resp.setTid(tid);
	// McpUtils.addStat("clHotSetexS", System.currentTimeMillis());
	// redisService.setex(commentHotKey, COMMENT_HOT_EXPIRE, resp);
	// McpUtils.addStat("clHotSetexE", System.currentTimeMillis());
	// return resp;
	// }
	// }
	// } else {
	// return getEmpty(appCommentId, typeId, tid, uid, isReviewAdd);
	// }
	// }
	// if (idArray == null) {
	// return getEmpty(appCommentId, typeId, tid, uid, isReviewAdd);
	// }
	// String hKey = getCommentHKey(appCommentId, typeId, tid);
	// Map<Long, CommentResp> commentMap = ssdbx.hmget(hKey, idArray,
	// Long.class, CommentResp.class);
	// if (commentMap == null || commentMap.size() < 1) {
	// return getEmpty(appCommentId, typeId, tid, uid, isReviewAdd);
	// }
	// Collection<CommentResp> commentValues = commentMap.values();
	// List<CommentResp> commentResps = new ArrayList<>(commentValues);
	// McpUtils.addStat("clUserTypeS", System.currentTimeMillis());
	// addUserType(commentResps);
	// McpUtils.addStat("clUserTypeE", System.currentTimeMillis());
	// CommentListResp resp = new CommentListResp();
	// resp.setTid(tid);
	// resp.setRcount(typeId == 4 ? totalZsize : videoZsize);
	// resp.setConmments(commentResps);
	// if (commentResps.size() > resp.getRcount()) {
	// logger.warn(String.format(
	// "rcount error. appId: %s,typeId: %s,tid: %s,lastId: %s,pageSize:
	// %s,loadType: %s,category: %s,uid: %s,startTime: %s,deviceId: %s,"//
	// + "totalZsize: %s, videoZsize: %s, refreshCache: %s, sig: %s", //
	// appId, typeId, tid, lastId, pageSize, loadType, category, uid, startTime,
	// deviceId, //
	// totalZsize, videoZsize, refreshCache, ctx.getSig()));
	// }
	// // 写第一页缓存
	// if (refreshCache) {
	// redisService.setex(commentTidCountKey, COMMENT_NEW_PAGE_EXPIRE * 2,
	// totalZsize);
	// redisService.setex(commentfirstPageKey, COMMENT_NEW_PAGE_EXPIRE, resp);
	// }
	// if (lastId == 0 && isReviewAdd) {// 第一页返回未审核的评论
	// addReviewComment(appCommentId, typeId, tid, uid, commentResps);
	// }
	// addPraiser(uid, idArray, commentResps, deviceId);
	// McpUtils.addStat("clEnd", System.currentTimeMillis());
	// return resp;
	// }
	//
	// private Long[] calculateHot(int appId, long zsize, int appCommentId, int
	// typeId, String tid) {
	// int hotLimit = 6; // 限制热门视频返回量
	// if (appId == 14) {
	// hotLimit = 2; // 迅雷9前端要求只返回2个
	// }
	// double hotMath = zsize * 0.07; // 热门数量约定总数7%
	// if (hotMath > 0.5) {
	// String idNotKey = getCommentIdZKey(appCommentId, typeId, tid, "hot");
	// Map<Long, Long> idMap = cacheX.zrrange(idNotKey, 0, hotLimit,
	// Long.class);
	// if (idMap != null && idMap.size() > 0) {
	// scoureFilter(idMap);
	// if (idMap.size() > 0) {
	// List<Entry<Long, Long>> entries = new ArrayList<Entry<Long, Long>>();
	// entries.addAll(idMap.entrySet());
	// Collections.sort(entries, comparator);
	// int hotNum = (int) Math.ceil(hotMath);
	// if (hotNum > hotLimit) {
	// hotNum = hotLimit;
	// }
	// hotNum = Math.min(hotNum, entries.size());
	// Long[] idArray = new Long[hotNum];
	// for (int i = 0; i < hotNum; i++) {
	// idArray[i] = entries.get(i).getKey();
	// }
	// return idArray;
	// }
	// }
	// }
	// return null;
	// }

	private void scoureFilter(Map<Long, Long> idMap) {
		Set<Entry<Long, Long>> entrySet = idMap.entrySet();
		if (CollectionUtils.isNotEmpty(entrySet)) {
			Iterator<Entry<Long, Long>> it = entrySet.iterator();
			while (it.hasNext()) {
				if (it.next().getValue() < 3) {
					it.remove();
				}
			}
		}
	}

	private String getCommentIdZKey(int appId, int typeId, String tid, String category) {
		return "comment-z-" + appId + typeId + tid + "-" + category;
	}

	/**
	 * 缓存用户未审核的评论
	 */
	private String getCommentUserReviewKey(long uid) {
		return "comment-r-" + uid;
	}

	private String getCommentHKey(int appId, int typeId, String tid) {
		return "comment-h-" + appId + typeId + tid;
	}

	private String getCommentPrasierKey(long uid) {
		return "comment-p-" + uid;
	}

	private String getCommentPrasierKey(String deviceId) {
		return "comment-p-" + deviceId;
	}

	// private CommentListResp getEmpty(int appId, int typeId, String tid, Long
	// uid, int rcount, boolean isReviewAdd) {
	// CommentListResp resp = new CommentListResp();
	// resp.setTid(tid);
	// List<CommentResp> comments = new ArrayList<>();
	// if (isReviewAdd) {
	// addReviewComment(appId, typeId, tid, uid, comments);
	// }
	// Long[] cids = new Long[comments.size()];
	// for (int i = 0; i < cids.length; i++) {
	// cids[i] = comments.get(i).getCid();
	// }
	// addPraiser(uid, cids, comments, null);
	// resp.setConmments(comments);
	// resp.setRcount(rcount);
	// return resp;
	// }
	//
	// private CommentListResp getEmpty(int appId, int typeId, String tid, Long
	// uid, boolean isReviewAdd) {
	// return getEmpty(appId, typeId, tid, uid, 0, isReviewAdd);
	// }
	//
	// /**
	// * 添加红2用户级别
	// */
	// private void addUserType(List<CommentResp> commentResps) {
	// if (CollectionUtils.isNotEmpty(commentResps)) {
	// for (CommentResp commentResp : commentResps) {
	// int userType = raUserService.getUserType(commentResp.getUid());
	// commentResp.setUserType(userType);
	// commentResp.setUserTypes(raUserService.getUserTypes(commentResp.getUid()));
	// }
	// }
	// }
	//
	// /**
	// * 过滤时间在资源审核之前的评论 应用在缓存取出后 应第一个视频所属的评论数不够一页的情况
	// */
	// private void timefilter(long videoId, List<CommentResp> respList) {
	// if (CollectionUtils.isNotEmpty(respList)) {
	// long temp = Long.MAX_VALUE - videoId;
	// for (int i = respList.size() - 1; i >= 0; i--) {
	// if (respList.get(i).getCid() > temp) {
	// respList.remove(i);
	// }
	// }
	// }
	// }
	//
	// /**
	// * 过滤时间在资源审核之前的评论
	// */
	// private void timefilter(Long videoId, Map<Long, Long> idMaps) {
	// if (videoId != null) {
	// long temp = Long.MAX_VALUE - videoId;
	// Iterator<Entry<Long, Long>> it = idMaps.entrySet().iterator();
	// while (it.hasNext()) {
	// Entry<Long, Long> e = it.next();
	// if (e.getKey() > temp) {
	// it.remove();
	// }
	// }
	// }
	// }
	//
	// /**
	// * 将用户待审核的评论加入到用户评论页
	// */
	// private void addReviewComment(int appId, int typeId, String tid, Long
	// uid, List<CommentResp> comments) {
	// if (uid != null && uid > 0) {
	// if (comments == null) {
	// comments = new ArrayList<>();
	// }
	// String key = getCommentUserReviewKey(uid);
	// Integer userReviewKey = redisService.get(key, Integer.class);
	// if (userReviewKey == null || userReviewKey != 0) {
	// Map<Long, Long> ids = ssdbx.zrange(key, 0, -1, Long.class);
	// if (ids != null && ids.size() > 0) {
	// Long[] idsLong = new Long[ids.size()];
	// ids.keySet().toArray(idsLong);
	// String hKey = getCommentHKey(appId, typeId, tid);
	// Map<Long, CommentReviewResp> reviewResps = ssdbx.hmget(hKey, idsLong,
	// Long.class,
	// CommentReviewResp.class);
	// if (reviewResps != null && reviewResps.size() > 0) {
	// int i = 0;
	// for (Entry<Long, CommentReviewResp> e : reviewResps.entrySet()) {
	// CommentReviewResp commentReview = e.getValue();
	// if (appId == commentReview.getAppId() && typeId ==
	// commentReview.getTypeId()
	// && tid.equals(commentReview.getTid())) {
	// CommentResp commentResp = reviewComment2Comment(commentReview);
	// addPraiser(uid, commentResp);
	// comments.add(i, commentResp);
	// i++;
	// }
	// }
	// }
	// }
	// // 第一次击穿
	// if (userReviewKey == null) {
	// redisService.setex(key, COMMENT_USER_REVIEW_EXPIRE, comments.size());
	// }
	// }
	// }
	// }
	//
	// /**
	// * 添加评论是否点赞过
	// */
	// private void addPraiser(Long uid, Long[] cids, List<CommentResp>
	// comments, String deviceId) {
	// String pKey;
	// if (uid != null && uid > 0) {
	// pKey = getCommentPrasierKey(uid);
	// } else if (deviceId != null) {
	// pKey = getCommentPrasierKey(deviceId);
	// } else {
	// return;
	// }
	// if (CollectionUtils.isEmpty(comments)) {
	// return;
	// }
	// Map<Long, Long> praiser = ssdbx.multi_zget(pKey, cids, Long.class);
	// if (praiser == null || praiser.size() < 1) {
	// return;
	// }
	// for (CommentResp commentResp : comments) {
	// if (praiser.containsKey(commentResp.getCid())) {
	// commentResp.setIsPraise(true);
	// }
	// }
	// }
	//
	// /**
	// * 添加评论是否点赞过
	// */
	// private void addPraiser(Long uid, List<CommentResp> comments, String
	// deviceId) {
	// String pKey;
	// if (uid != null && uid > 0) {
	// pKey = getCommentPrasierKey(uid);
	// } else if (deviceId != null) {
	// pKey = getCommentPrasierKey(deviceId);
	// } else {
	// return;
	// }
	// if (CollectionUtils.isEmpty(comments)) {
	// return;
	// }
	// Long[] cids = new Long[comments.size()];
	// for (int i = 0; i < comments.size(); i++) {
	// cids[i] = comments.get(i).getCid();
	// }
	// Map<Long, Long> praiser = ssdbx.multi_zget(pKey, cids, Long.class);
	// if (praiser == null || praiser.size() < 1) {
	// return;
	// }
	// for (CommentResp commentResp : comments) {
	// if (praiser.containsKey(commentResp.getCid())) {
	// commentResp.setIsPraise(true);
	// }
	// }
	// }
	//
	// private void addPraiser(Long uid, CommentResp comment) {
	// if (uid != null && uid > 0) {
	// String pKey = getCommentPrasierKey(uid);
	// Long praise = ssdbx.zget(pKey, comment.getCid());
	// if (praise > 0) {
	// comment.setIsPraise(true);
	// }
	// }
	// }
	//
	// // private List<CommentResp> getComments(int appId, int typeId, String
	// tid)
	// // throws IOException {
	// //
	// // List<CommentColumn> columns = commentDao.listComment(appId, typeId,
	// tid,
	// // 0, 500);
	// // if (columns == null || columns.size() < 1) {
	// // return null;
	// // }
	// // return commntColumn2Resp(columns);
	// // }
	//
	// private CommentResp reviewComment2Comment(CommentReviewResp
	// commentReview) {
	// CommentResp resp = new CommentResp();
	// resp.setCid(commentReview.getCid());
	// resp.setComment(commentReview.getComment());
	// resp.setSourceId(commentReview.getSourceId());
	// resp.setReplys(commentReview.getReplys());
	// resp.setDevice(commentReview.getDevice());
	// resp.setPo(commentReview.getPo());
	// resp.setCi(commentReview.getCi());
	// resp.setGcount(commentReview.getGcount());
	// resp.setRcount(commentReview.getRcount());
	// resp.setScount(commentReview.getScount());
	// resp.setTime(commentReview.getTime());
	// resp.setUid(commentReview.getUid());
	// resp.setUserName(commentReview.getUserName());
	// resp.setUserImg(commentReview.getUserImg());
	// int userType = raUserService.getUserType(commentReview.getUid());
	// resp.setUserType(userType);
	// resp.setUserTypes(raUserService.getUserTypes(commentReview.getUid()));
	//
	// resp.setIsPdRiew(true);
	// resp.setAnonymous(commentReview.getAnonymous());
	// resp.setDownLoadSpeed(commentReview.getDownLoadSpeed());
	// resp.setBandwidth(commentReview.getBandwidth());
	// resp.setExtParamsJson(commentReview.getExtParamsJson());
	// return resp;
	// }
	//
	// // private List<CommentResp> commntColumn2Resp(List<CommentColumn>
	// columns)
	// // {
	// //
	// // List<CommentResp> commentList = new ArrayList<>();
	// // for (CommentColumn column : columns) {
	// // CommentResp comment = new CommentResp();
	// // comment.setCid(column.getCommentId());
	// // IdColumn idColumn = gson.fromJson(column.getIdColumnValue(),
	// // IdColumn.class);
	// // comment.setDevice(idColumn.getDevice());
	// // comment.setPo(idColumn.getPo());
	// // comment.setCi(idColumn.getCi());
	// // comment.setTime(idColumn.getTime());
	// // comment.setCommentType(idColumn.getCommentType());
	// //
	// // UserColumn userColumn = gson.fromJson(column.getUserColumnValue(),
	// // UserColumn.class);
	// // if (userColumn == null) {
	// // logger.warn("comment reload error userColumn null tid:{} cid:{}
	// sig:{}",
	// // idColumn.getTid(), idColumn.getCid(),
	// McpUtils.getSigFromThreadLocal());
	// // continue;
	// // }
	// // Long uid = userColumn.getUid();
	// // if (uid == null) {
	// // logger.warn("comment reload error uid null tid:{} cid:{} sig:{}",
	// // idColumn.getTid(), idColumn.getCid(),
	// McpUtils.getSigFromThreadLocal());
	// // uid = 0L;
	// // }
	// // comment.setUid(uid);
	// // comment.setUserName(userColumn.getName());
	// // comment.setUserImg(userColumn.getImg());
	// //
	// // ContentColumn contentColumn =
	// // gson.fromJson(column.getCommentColumnValue(), ContentColumn.class);
	// // comment.setComment(contentColumn.getContent());
	// // comment.setReplys(contentColumn.getReply());
	// //
	// // CountColumn countColumn = gson.fromJson(column.getCountColumnValue(),
	// // CountColumn.class);
	// // comment.setGcount(countColumn.getGcount());
	// // comment.setScount(countColumn.getScount());
	// //
	// // // PC迅雷加上额外字段
	// // int sourceAppId = idColumn.getSourceAppId();
	// // if (sourceAppId == 14 || sourceAppId == 30) {
	// // ExtColumn extColumn = gson.fromJson(column.getExtColumnValue(),
	// // ExtColumn.class);
	// // comment.setDownLoadSpeed(extColumn.getDownLoadSpeed());
	// // comment.setAnonymous(extColumn.isAnonymous());
	// // }
	// // int rcount = 0;
	// // if (contentColumn.getReply() != null) {
	// // rcount = contentColumn.getReply().size();
	// // }
	// // comment.setRcount(rcount);
	// //
	// // commentList.add(comment);
	// // }
	// // return commentList;
	// // }
	//
	// /**
	// * 点赞
	// */
	// @Override
	// public long count(int appId, int typeId, String tid, long cid, int type,
	// UserInfo userInfo, String sourceId,
	// String deviceId, String userAgent) throws IOException {
	// // 刷新首页缓存和最热缓存实时更新点赞状态
	// delPageCache(tid);
	// long count = 0;
	// // 获取评论app配置参数
	// CommentConfig commentConf =
	// appCommentConfigService.getCommentConf(appId);
	// int appCommentId = commentConf.getAppCommentId();
	// String hKey = getCommentHKey(appCommentId, typeId, tid);
	// String idHotKey = getCommentIdZKey(appCommentId, typeId, tid, "hot");
	// String idNewKey = getCommentIdZKey(appCommentId, typeId, tid, "new");
	// long score = 0;
	// long indexBefore = ssdbx.zrrank(idHotKey, cid);
	// if (ssdbx.zget(idNewKey, cid) > -1) {
	// score = ssdbx.zincr(idHotKey, cid, 1);
	// }
	// CommentReviewResp resp = ssdbx.hget(hKey, cid, CommentReviewResp.class);
	// if (resp == null) {
	// return count;
	// }
	// count = resp.getGcount() + 1;
	// resp.setGcount(count);
	// ssdbx.hset(hKey, cid, resp);
	// if (count % 10 == 0) {
	// CommentColumn commentColumn = commentDao.getComment(appCommentId, typeId,
	// tid, cid);
	// if (commentColumn != null) {
	// CountColumn countColumn =
	// gson.fromJson(commentColumn.getCountColumnValue(), CountColumn.class);
	// countColumn.setGcount((int) count);
	// if (commentDao.countComment(appCommentId, typeId, tid, cid,
	// gson.toJson(countColumn))) {
	// return count;
	// }
	// }
	// }
	// // 有料若产生热门评论则推送给评论发表用户
	// if (appId == 3 || appId == 20 || appId == 22) {
	//
	// // 视频所有人
	// HotVideoDTO hotVideoDTO = fileService.queryEnabled(0,
	// Long.valueOf(sourceId), null, false);
	// if (hotVideoDTO == null) {
	// throw new ApiException(4012);
	// }
	// long videoOwner = hotVideoDTO.getUserId();
	//
	// long size = this.commentCountTid(tid, typeId);
	// double math = size * 0.07;
	// if (math > 0.5) {
	// int hotNum = (int) (size * 0.07);
	// if (math % 1 > 0.5) {
	// hotNum++;
	// }
	// if (hotNum > 6) {
	// hotNum = 6;
	// }
	// long indexNow = ssdbx.zrrank(idHotKey, cid);
	// boolean beforeNotHot = false, nowHot = false;
	// if ((indexBefore > -1 && indexBefore > hotNum - 1) || score - 1 < 3) {
	// beforeNotHot = true;
	// }
	// if (indexNow > -1 && indexNow <= hotNum - 1 && score >= 3) {
	// nowHot = true;
	// }
	// if (beforeNotHot && nowHot) {
	// RaUserInfo raUserInfo = this.raUserInfoDao.getUserInfo(resp.getUid());
	// // send message
	// Map<String, Object> extra = new HashMap<>();
	// extra.put("videoId", NumberUtils.toLong(sourceId));
	// extra.put("gcid", tid);
	// if (raUserInfo != null && raUserInfo.videoPlayMessageOn()) {
	// // userMessageService.sendMessage(resp.getUid(),
	// // UserMessageService.MIPUSH_MESSAGE_TYPE_NEW_MESSAGE_106,
	// // "恭喜！你的1条评论上热门了，快点击查看！", extra);
	// // userMessageService.addCommentBeHotMessage(systemUserId,
	// // resp.getUid(), "你在这条视频下的评论上热门了，快来看一下吧！", typeId,
	// // sourceId, String.valueOf(cid), tid);
	// }
	// // userMessageService.sendMessage(videoOwner,
	// // UserMessageService.MIPUSH_MESSAGE_TYPE_NEW_MESSAGE_107,
	// // "你的视频下产生了1条热门评论，快来看是什么?", extra);
	// // userMessageService.addNewHotCommentMessage(systemUserId,
	// // videoOwner, "你的这条视频下产生了1条热门评论，快来看看是什么？", typeId,
	// // sourceId, String.valueOf(cid), tid);
	// }
	// }
	// }
	//
	// if (userInfo != null) {
	// // 有料消息中心
	// userMessageService.addPraiseCommentMessage(userInfo.getId(),
	// resp.getUid(), resp.getComment(), typeId,
	// sourceId, String.valueOf(cid), tid, resp.getComment());
	// // 给手雷消息中心发消息
	// final long initiator = userInfo.getId();
	// final long consumer = resp.getUid();
	// final String resourceId = resp.getSourceId();
	// final String gcid = resp.getTid();
	// final String original = resp.getComment();
	// final long mcid = resp.getCid();
	// messageExecutor.execute(new Runnable() {
	// @Override
	// public void run() {
	// Map<String, Object> messageMap = new HashMap<>();
	// messageMap.put("type", "like_comment");
	// messageMap.put("initiator", initiator);
	// messageMap.put("consumer", consumer);
	// messageMap.put("resource_id", resourceId);
	// messageMap.put("gcid", gcid);
	// messageMap.put("original", original);
	// messageMap.put("cid", mcid);
	// String messageJson = gson.toJson(messageMap);
	// HttpUtil.post(client, messageUrl, messageJson);
	// }
	// });
	// }
	//
	// // 有用户的情况
	// if (userInfo != null && userInfo.getId() > 0) {
	// CommentPraiser prasier = new CommentPraiser();
	// prasier.setAppId(appCommentId);
	// prasier.setTypeId(typeId);
	// prasier.setTid(tid);
	// prasier.setLevel(commentConf.getLevel());
	// prasier.setCid(cid);
	// prasier.setTime(System.currentTimeMillis());
	// String columnValue = gson.toJson(prasier);
	// long userId = userInfo.getId();
	// commentDao.addPraiser(cid, null, userId, columnValue);
	// String pKey = getCommentPrasierKey(userId);
	// ssdbx.zset(pKey, cid, cid);
	// // 记录最近三个点赞用户
	// CommentUserListPcResp.CommentUserPc.CommentUserPraiser commentUserPraiser
	// = new CommentUserListPcResp.CommentUserPc.CommentUserPraiser();
	// commentUserPraiser.setUserId(userId).setNickName(userInfo.getNickName())
	// .setPortraits(userInfo.getPortrait());
	// String commentUserPraiserStr = gson.toJson(commentUserPraiser);
	// String recentPraiserKey = String.format(COMMENT_RECENT_PRAISER, cid);
	// if (ssdbCommentNew.zsize(recentPraiserKey) >= 3) {
	// ssdbCommentNew.zpop_front(recentPraiserKey, 1, String.class);
	// }
	// ssdbCommentNew.zset(recentPraiserKey, commentUserPraiserStr,
	// System.currentTimeMillis());
	// } else { // 没用户按device记录
	// String pKey = getCommentPrasierKey(deviceId);
	// ssdbx.zset(pKey, cid, cid);
	// }
	// return count;
	// }
	//
	// private void delPageCache(String tid) {
	// redisService.del(String.format(COMMENT_NEW_PAGE, tid));
	// redisService.del(String.format(COMMENT_HOT_RESP, tid));
	// }
	//
	// /**
	// * 当用户未登录，根据deviceId记录点赞
	// *
	// * @param userInfo
	// * 用户信息
	// * @param deviceId
	// * 设备唯一码
	// * @param tid
	// * 资源id
	// * @param cid
	// * 评论id
	// * @param appId
	// * 终端类型
	// */
	// @Override
	// public void addPraiserLog(UserInfo userInfo, String deviceId, String tid,
	// long cid, int appId) {
	// // logger.info("addPraiserLog");
	// if (userInfo != null && userInfo.getId() > 0) {
	// CommentPraiserUser pud = new CommentPraiserUser();
	// pud.setUserId(userInfo.getId());
	// pud.setAppId(appId);
	// pud.setTid(tid);
	// pud.setCid(cid);
	// pud.setTime(System.currentTimeMillis());
	// commentPraiserUserDao.save(pud);
	// } else {
	// CommentPraiserDevice pdd = new CommentPraiserDevice();
	// pdd.setDeviceId(deviceId);
	// pdd.setAppId(appId);
	// pdd.setTid(tid);
	// pdd.setCid(cid);
	// pdd.setTime(System.currentTimeMillis());
	// commentPraiserDeviceDao.save(pdd);
	// }
	// }
	//
	// @Override
	// public boolean setConf(int appId, CommentConfigReq req) throws
	// IOException {
	//
	// CommentConfig commentConfig = new CommentConfig();
	// commentConfig.setAppId(appId);
	// commentConfig.setAppCommentId(req.getAppCommentId());
	// commentConfig.setType(req.getTypeId());
	// commentConfig.setLevel(req.getLevel());
	// commentConfig.setFloorLevel(req.getFloorLevel());
	// commentConfig.setAddTime(System.currentTimeMillis());
	//
	// return commentMySqlDao.saveCommentConfig(commentConfig) > 0;
	// }
	//
	// @Override
	// public CommentUserListResp listUserComments(long uid, boolean islogin,
	// long lastId, int len)
	// throws IllegalArgumentException, IOException {
	//
	// CommentUserListResp resp = new CommentUserListResp();
	// List<String> userComments = commentDao.listUserComment(uid, lastId, len);
	// if (userComments == null || userComments.size() < 1) {
	// resp.setComments(new ArrayList<CommentUserResp>(0));
	// return resp;
	// }
	// List<CommentUserResp> comments = new ArrayList<>(userComments.size());
	// for (String userComment : userComments) {
	// CommentUserResp comment = new CommentUserResp();
	// UserCommentColumn user = gson.fromJson(userComment,
	// UserCommentColumn.class);
	// String hKey = getCommentHKey(user.getAppId(), user.getTypeId(),
	// user.getTid());
	// CommentResp commentResp = ssdbx.hget(hKey, user.getCid(),
	// CommentResp.class);
	// if (commentResp == null) {
	// break;
	// }
	// if (islogin) {
	// addPraiser(uid, commentResp);
	// }
	// comment.setTypeId(user.getTypeId());
	// comment.setTid(user.getTid());
	// comment.setComment(commentResp);
	// comments.add(comment);
	// }
	// resp.setComments(comments);
	// return resp;
	// }
	//
	// @Override
	// public List<CommentUserListPcResp.CommentUserPc> listUserCommentsPc(long
	// userId, long lastId, int len)
	// throws IOException {
	// List<CommentUserListPcResp.CommentUserPc> comments = new ArrayList<>();
	// List<String> userComments = commentDao.listUserComment(userId, lastId,
	// len);
	// if (CollectionUtils.isNotEmpty(userComments)) {
	// for (String userComment : userComments) {
	// UserCommentColumn user = gson.fromJson(userComment,
	// UserCommentColumn.class);
	// int typeId = user.getTypeId();
	// int appId = user.getAppId();
	// String hKey = getCommentHKey(appId, typeId, user.getTid());
	// CommentResp commentResp = ssdbx.hget(hKey, user.getCid(),
	// CommentResp.class);
	// if (commentResp != null) {
	// CommentUserListPcResp.CommentUserPc comment = new
	// CommentUserListPcResp.CommentUserPc();
	// comment.setGcid(user.getTid());
	// comment.setContent(commentResp.getComment());
	// comment.setGcount((int) commentResp.getGcount());
	// long cid = commentResp.getCid();
	// comment.setCid(cid);
	// comment.setCommentUserPraisers(this.findCommentRecentPraise(cid));
	// comment.setTypeId(typeId);
	// if (typeId == 1) {
	// String sourceId = commentResp.getSourceId();
	// if (NumberUtils.isDigits(sourceId)) {
	// long videoId = Long.parseLong(sourceId);
	// comment.setVideoId(videoId);
	// HotVideo hotVideo = fileService.queryHotVideo(videoId);
	// if (hotVideo != null) {
	// comment.setVideoStatus(hotVideo.getStatus().getValue());
	// comment.setVideoTitle(hotVideo.getTitle());
	// } else {
	// logger.warn("queryHotVideo null {} {}", appId, videoId);
	// }
	// } else {
	// logger.warn("source id parse error {}", sourceId);
	// }
	// }
	// comments.add(comment);
	// }
	// }
	// }
	// return comments;
	// }
	//
	// private CommentUserListPcResp.CommentUserPc.CommentUserPraiser[]
	// findCommentRecentPraise(long cid) {
	// Map<String, Long> rangeMap =
	// ssdbCommentNew.zrange(String.format(COMMENT_RECENT_PRAISER, cid), 0, 3,
	// String.class);
	// if (rangeMap != null && rangeMap.size() > 0) {
	// List<CommentUserListPcResp.CommentUserPc.CommentUserPraiser> praisers =
	// new ArrayList<>();
	// for (String praiseStr : rangeMap.keySet()) {
	// CommentUserListPcResp.CommentUserPc.CommentUserPraiser praiser =
	// gson.fromJson(praiseStr,
	// CommentUserListPcResp.CommentUserPc.CommentUserPraiser.class);
	// praisers.add(praiser);
	// }
	// return praisers.toArray(new
	// CommentUserListPcResp.CommentUserPc.CommentUserPraiser[praisers.size()]);
	// }
	// return null;
	// }
	//
	// @Override
	// public void refreshCommentCache() {
	// logger.info("start refresh comment cache");
	// }
	//
	// @Override
	// public void refreshCommentEs(int typeId) {
	// logger.info("start refresh comment es pass");
	// Map<String, List<com.xunlei.xlmc.comment.domain.CommentDTO>> map =
	// commentDao.scanComment("", 10000000);
	// logger.info("total tid {}", map.size());
	// reloadCommentES(map);
	// try {
	// List<CommentCheckColumn> comments = commentDao.listCheckNoPassComments(1,
	// 1, 10000000, "desc", null, null,
	// 0, null, 0, 0);
	// sendKafkaToAuditList(CommentAuditStatusEnum.REJECT, comments);
	// comments = commentDao.listCheckNoPassComments(4, 1, 10000000, "desc",
	// null, null, 0, null, 0, 0);
	// sendKafkaToAuditList(CommentAuditStatusEnum.REJECT, comments);
	// commentDao.listReviewComments(1, 1, 10000000, "desc", null, null, 0,
	// null, 0, 0);
	// sendKafkaToAuditList(CommentAuditStatusEnum.TOPASS, comments);
	// commentDao.listReviewComments(4, 1, 10000000, "desc", null, null, 0,
	// null, 0, 0);
	// sendKafkaToAuditList(CommentAuditStatusEnum.TOPASS, comments);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// @Override
	// public void commentAuditReload(AdminCommentFlushReq req) {
	// String startKey = req.getStartKey();
	// int len = req.getLen();
	// long startCid = 0;
	// if (!StringUtils.isBlank(startKey)) {
	// startCid = Long.parseLong(startKey);
	// }
	// int loop = 0;
	// int count = 0;
	// List<CommentCheckColumn> list =
	// commentDao.listCommentPass(req.getTypeId(), startCid, len);
	// while (!org.springframework.util.CollectionUtils.isEmpty(list)) {
	// logger.info("commentAuditReload loop {}", loop++);
	// for (CommentCheckColumn commentCheckColumn : list) {
	// startCid = commentCheckColumn.getCommentId();
	// logger.info("commentAuditReload " + count++ + "," +
	// String.valueOf(commentCheckColumn.getCommentId()));
	// String auditUserName = commentCheckColumn.getChecker();
	// IdColumn idColumn = gson.fromJson(commentCheckColumn.getIdColumnValue(),
	// IdColumn.class);
	// UserColumn userColumn =
	// gson.fromJson(commentCheckColumn.getUserColumnValue(), UserColumn.class);
	// String content = commentCheckColumn.getContent();
	// this.sendKafkaToAudit(auditUserName, CommentAuditStatusEnum.PASSED,
	// idColumn, userColumn, content);
	// }
	// try {
	// Thread.sleep(1000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// list = commentDao.listCommentPass(req.getTypeId(), startCid, len);
	// }
	// }
	//
	// private void sendKafkaToAuditList(CommentAuditStatusEnum
	// commentAuditStatusEnum,
	// List<CommentCheckColumn> comments) {
	// for (CommentCheckColumn commentCheckColumn : comments) {
	// IdColumn idColumn = gson.fromJson(commentCheckColumn.getIdColumnValue(),
	// IdColumn.class);
	// UserColumn userColumn =
	// gson.fromJson(commentCheckColumn.getUserColumnValue(), UserColumn.class);
	// String content = commentCheckColumn.getContent();
	// // sendKafkaToAudit(commentAuditStatusEnum, idColumn, userColumn,
	// // content);
	// }
	// }
	//
	// private void reloadCommentES(Map<String,
	// List<com.xunlei.xlmc.comment.domain.CommentDTO>> map) {
	// for (Entry<String, List<com.xunlei.xlmc.comment.domain.CommentDTO>> entry
	// : map.entrySet()) {
	// for (com.xunlei.xlmc.comment.domain.CommentDTO commentDTO :
	// entry.getValue()) {
	// IdColumn idColumn = gson.fromJson(commentDTO.getIdColumn(),
	// IdColumn.class);
	// UserColumn userColumn = gson.fromJson(commentDTO.getUserColumn(),
	// UserColumn.class);
	// ContentColumn contentColumn =
	// gson.fromJson(commentDTO.getContentColumn(), ContentColumn.class);
	// // sendKafkaToAudit(CommentAuditStatusEnum.PASSED, idColumn,
	// // userColumn, contentColumn.getContent());
	// }
	// }
	// }
	//
	// private void addUserComment(int appId, int typeId, String tid, long uid,
	// long cid, long ctime)
	// throws IllegalArgumentException, IOException {
	//
	// UserCommentColumn userComment = new UserCommentColumn();
	// userComment.setAppId(appId);
	// userComment.setTypeId(typeId);
	// userComment.setTid(tid);
	// userComment.setLevel(1);
	// userComment.setCid(cid);
	// // 根据typeId分组的用户评论缓存
	// ssdbCommentNew.zset(String.format(COMMENT_USER_TYPE, uid, typeId), tid +
	// "-" + cid, ctime);
	// commentDao.addUserComment(uid, cid, gson.toJson(userComment));
	// }
	//
	// @Override
	// public SecurityCommentReslut listCheckComments(int typeId, long pageId,
	// int pageSize, int status, String orderBy,
	// String keyword, String author, long authorId, String checker, long
	// startCkeckTime, long endCkeckTime)
	// throws IllegalArgumentException, IOException {
	// List<CommentCheckColumn> commentColumns = null;
	// int count = 0;
	// switch (status) {
	// case 1:
	// long startList = System.currentTimeMillis();
	// logger.debug("listCheckPassComments pageSize {}", pageSize);
	// if (checker.equalsIgnoreCase("AI")) {
	// checker = "";
	// commentColumns = commentDao.listAIPassComments(typeId, pageId, pageSize,
	// orderBy, keyword, author,
	// authorId, checker, startCkeckTime, endCkeckTime);
	// } else {
	// commentColumns = commentDao.listCheckPassComments(typeId, pageId,
	// pageSize, orderBy, keyword, author,
	// authorId, checker, startCkeckTime, endCkeckTime);
	// }
	// logger.debug("listCheckPassComments result {}", commentColumns.size());
	// logger.debug("listCheckPassComments cost {}", System.currentTimeMillis()
	// - startList);
	// // count = commentDao.countCheckPassComments(typeId, keyword,
	// // author, authorId, checker, startCkeckTime, endCkeckTime);
	// break;
	// case 2:
	// commentColumns = commentDao.listReviewComments(typeId, pageId, pageSize,
	// orderBy, keyword, author, authorId,
	// checker, startCkeckTime, endCkeckTime);
	// // count = commentDao.countReviewComments(typeId, keyword, author,
	// // authorId, checker, startCkeckTime, endCkeckTime);
	// break;
	// case 3:
	// commentColumns = commentDao.listCheckNoPassComments(typeId, pageId,
	// pageSize, orderBy, keyword, author,
	// authorId, checker, startCkeckTime, endCkeckTime);
	// // count = commentDao.countCheckNoPassComments(typeId, keyword,
	// // author, authorId, checker, startCkeckTime, endCkeckTime);
	// break;
	// case 4:
	// commentColumns = commentDao.listCheckNoPassComments(typeId, pageId,
	// pageSize, orderBy, keyword, author,
	// authorId, "report", startCkeckTime, endCkeckTime);
	// break;
	// default:
	// break;
	// }
	// if (commentColumns == null || commentColumns.size() < 1) {
	// return null;
	// }
	// List<SecurityComment> checkComments = new ArrayList<>(pageSize);
	// String[] reportLogKeys = new String[commentColumns.size()];
	// int index = 0;
	// for (CommentCheckColumn commentColumn : commentColumns) {
	// SecurityComment checkComment = new SecurityComment();
	// ContentColumn contentColumn =
	// gson.fromJson(commentColumn.getCommentColumnValue(),
	// ContentColumn.class);
	// checkComment.setContent(contentColumn.getContent());
	// IdColumn idColumn = gson.fromJson(commentColumn.getIdColumnValue(),
	// IdColumn.class);
	// checkComment.setId(idColumn.getCid());
	// checkComment.setTime(idColumn.getTime());
	// checkComment.setGcid(idColumn.getTid());
	// checkComment.setVideoId(idColumn.getSourceId());
	// checkComment.setAppId(idColumn.getSourceAppId());
	// UserColumn userColumn = gson.fromJson(commentColumn.getUserColumnValue(),
	// UserColumn.class);
	// checkComment.setUid(userColumn.getUid());
	// checkComment.setUserName(userColumn.getName());
	// checkComment.setCheckTime(commentColumn.getCheckTime());
	// checkComment.setChecker(commentColumn.getChecker());
	// checkComments.add(checkComment);
	//
	// List<CommentReplyResp> replys = contentColumn.getReply();
	// if (CollectionUtils.isNotEmpty(replys)) {
	// checkComment.setReplyUid(replys.get(0).getUid());
	// }
	// reportLogKeys[index++] = String.format(COMMENT_REPORT_LOG,
	// idColumn.getCid());
	// }
	// Map<String, String> reportLogMap = ssdbCommentNew.mget(reportLogKeys,
	// String.class, String.class);
	// if (reportLogMap != null) {
	// for (Entry<String, String> e : reportLogMap.entrySet()) {
	// String[] keyUnit = e.getKey().split("-");
	// long cid = Long.parseLong(keyUnit[keyUnit.length - 1]);
	// for (SecurityComment checkComment : checkComments) {
	// if (cid == checkComment.getId()) {
	// checkComment.setReportLog(e.getValue());
	// }
	// }
	// }
	// }
	// SecurityCommentReslut reslut = new SecurityCommentReslut();
	// logger.debug("listCheckPassComments service result {}", pageSize);
	// reslut.setSecurityComment(checkComments);
	// reslut.setCount(count);
	// return reslut;
	// }
	//
	// @Override
	// public boolean checkComments(int typeId, int status, String checker,
	// long... ids)
	// throws IllegalArgumentException, IOException {
	// if (ids == null || ids.length < 1) {
	// return false;
	// }
	// long checkTime = System.currentTimeMillis();
	// CommentCheckType commentCheckType =
	// CommentCheckType.parseCommentCheckType(status);
	// if (commentCheckType == null) {
	// return false;
	// }
	// // 审核通过
	// if (status == 1) {
	// List<CommentCheckColumn> commentColumns =
	// commentDao.getReviewComments(typeId, ids);
	// if (commentColumns == null || commentColumns.size() < 1) {
	// throw new ApiException(4013);
	// }
	// for (CommentCheckColumn commentColumn : commentColumns) {
	// commentColumn.setChecker(checker);
	// commentColumn.setCheckTime(checkTime);
	// }
	// if (commentDao.addAIPassComments(typeId, commentColumns)
	// && commentDao.addCheckPassComments(typeId, commentColumns) &&
	// addComments(checker, commentColumns)
	// && commentDao.delReviewComments(typeId, ids)) {
	// if (commentMySqlDao.addCommentDetail(commentColumns,
	// commentCheckType.getAction()) > 0) {
	// this.checkUpdateCache(commentColumns);
	// return true;
	// }
	// }
	// } else if (status == 2) { // 审核不通过
	// List<CommentCheckColumn> commentColumns =
	// commentDao.getReviewComments(typeId, ids);
	// if (commentColumns == null || commentColumns.size() < 1) {
	// throw new ApiException(4013);
	// }
	// for (CommentCheckColumn commentColumn : commentColumns) {
	// commentColumn.setChecker(checker);
	// commentColumn.setCheckTime(checkTime);
	// IdColumn idColumn = gson.fromJson(commentColumn.getIdColumnValue(),
	// IdColumn.class);
	// UserColumn userColumn = gson.fromJson(commentColumn.getUserColumnValue(),
	// UserColumn.class);
	// delReviewCommentCache(userColumn.getUid(), idColumn.getCid());
	// String hKey = getCommentHKey(idColumn.getAppId(), idColumn.getTypeId(),
	// idColumn.getTid());
	// ssdbx.hdel(hKey, idColumn.getCid());
	// }
	// if (commentDao.addCheckNoPassComments(typeId, commentColumns) &&
	// commentDao.delReviewComments(typeId, ids)
	// && commentDao.delAIPassComments(typeId, ids)) {
	// if (commentMySqlDao.addCommentDetail(commentColumns,
	// commentCheckType.getAction()) > 0) {
	// return true;
	// }
	// }
	// } else if (status == 3) { // 下线
	// logger.debug("delAIPassComments enter {}", ids);
	// List<CommentCheckColumn> commentColumns =
	// commentDao.getCheckPassComments(typeId, ids);
	// if (commentColumns == null || commentColumns.size() < 1) {
	// commentDao.delCheckPassComments(typeId, ids);
	// commentDao.delAIPassComments(typeId, ids);
	// throw new ApiException(4015);
	// }
	// for (CommentCheckColumn commentColumn : commentColumns) {
	// commentColumn.setChecker(checker);
	// commentColumn.setCheckTime(checkTime);
	// }
	// logger.debug("delAIPassComments start {}", ids);
	// if (commentDao.addCheckNoPassComments(typeId, commentColumns)) {
	// commentDao.delCheckPassComments(typeId, ids);
	// logger.debug("delAIPassComments {}", ids);
	// commentDao.delAIPassComments(typeId, ids);
	// logger.debug("delAIPassComments success {}", ids);
	// for (CommentColumn commentColumn : commentColumns) {
	// IdColumn idColumn = gson.fromJson(commentColumn.getIdColumnValue(),
	// IdColumn.class);
	// ExtColumn extColumn = gson.fromJson(commentColumn.getExtColumnValue(),
	// ExtColumn.class);
	// UserColumn userColumn = gson.fromJson(commentColumn.getUserColumnValue(),
	// UserColumn.class);
	// commentDao.del(idColumn.getAppId(), idColumn.getTypeId(),
	// idColumn.getTid(), idColumn.getCid());
	// commentDao.delUserComment(userColumn.getUid(), idColumn.getCid());
	// delCache(idColumn.getAppId(), idColumn.getTypeId(), idColumn.getTid(),
	// idColumn.getCid(),
	// extColumn.getReplyerCids());
	// if (commentMySqlDao.addCommentDetail(commentColumns,
	// commentCheckType.getAction()) > 0) {
	// return true;
	// }
	// }
	// }
	// } else if (status == 4) { // 重新上线
	// List<CommentCheckColumn> commentColumns =
	// commentDao.getCheckNoPassComments(typeId, ids);
	// if (commentColumns == null || commentColumns.size() < 1) {
	// throw new ApiException(4014);
	// }
	// for (CommentCheckColumn commentColumn : commentColumns) {
	// commentColumn.setChecker(checker);
	// commentColumn.setCheckTime(checkTime);
	// }
	// if (commentDao.addCheckPassComments(typeId, commentColumns) &&
	// addComments(checker, commentColumns)
	// && commentDao.delCheckNoPassComments(typeId, ids)) {
	// if (commentMySqlDao.addCommentDetail(commentColumns,
	// commentCheckType.getAction()) > 0) {
	// this.checkUpdateCache(commentColumns);
	// return true;
	// }
	// }
	// } else if (status == 5) { // AI无法判断
	// List<CommentCheckColumn> commentColumns =
	// commentDao.getReviewComments(typeId, ids);
	// if (commentColumns == null || commentColumns.size() < 1) {
	// throw new ApiException(4013);
	// }
	// for (CommentCheckColumn commentColumn : commentColumns) {
	// commentColumn.setChecker(checker);
	// commentColumn.setCheckTime(checkTime);
	// }
	// if (commentDao.addReviewComments(typeId, commentColumns)) {
	// if (commentMySqlDao.addCommentDetail(commentColumns,
	// commentCheckType.getAction()) > 0) {
	// return true;
	// }
	// }
	// } else if (status == 6) { // 人工审核通过
	// boolean aiPass = false;
	// List<CommentCheckColumn> commentColumns =
	// commentDao.getReviewComments(typeId, ids);
	// if (commentColumns == null || commentColumns.size() < 1) {
	// commentColumns = commentDao.getAIPassComments(typeId, ids);
	// aiPass = true;
	// }
	// for (CommentCheckColumn commentColumn : commentColumns) {
	// commentColumn.setChecker(checker);
	// commentColumn.setCheckTime(checkTime);
	// }
	// if (commentDao.addCheckPassComments(typeId, commentColumns) &&
	// commentDao.delReviewComments(typeId, ids)
	// && commentDao.delAIPassComments(typeId, ids)) {
	// if (aiPass) {
	// List<Long> aiPassIds = new ArrayList<>(commentColumns.size());
	// for (CommentCheckColumn commentCheckColumn : commentColumns) {
	// aiPassIds.add(commentCheckColumn.getCommentId());
	// }
	// // 已经审核过的需要从AIPass表中删除
	// List<Long> delAiPassIds = new ArrayList<>();
	// // 没有AI审核过的需要加入到comments
	// List<CommentCheckColumn> needAddComment = new ArrayList<>();
	// for (CommentCheckColumn commentColumn : commentColumns) {
	// long cid = commentColumn.getCommentId();
	// if (aiPassIds.contains(cid)) {
	// delAiPassIds.add(cid);
	// } else {
	// needAddComment.add(commentColumn);
	// }
	// }
	// if (CollectionUtils.isNotEmpty(delAiPassIds)) {
	// int size = delAiPassIds.size();
	// long[] delIds = new long[size];
	// for (int i = 0; i < size; i++) {
	// delIds[i] = delAiPassIds.get(i);
	// }
	// commentDao.delAIPassComments(typeId, delIds);
	// }
	// addComments(checker, needAddComment);
	// } else {
	// addComments(checker, commentColumns);
	// }
	// if (commentMySqlDao.addCommentDetail(commentColumns,
	// commentCheckType.getAction()) > 0) {
	// this.checkUpdateCache(commentColumns);
	// return true;
	// }
	// }
	// }
	// return false;
	// }
	//
	// private void checkUpdateCache(List<CommentCheckColumn> commentColumns) {
	// for (CommentColumn commentColumn : commentColumns) {
	// IdColumn idColumn = gson.fromJson(commentColumn.getIdColumnValue(),
	// IdColumn.class);
	// long cid = idColumn.getCid();
	// String tid = idColumn.getTid();
	// UserColumn userColumn = gson.fromJson(commentColumn.getIdColumnValue(),
	// UserColumn.class);
	// Long userId = userColumn.getUid();
	// if (userId != null && userId > 0) {
	// this.delReviewCommentCache(userId, cid);
	// }
	// redisService.del(String.format(COMMENT_COUNT, tid));
	// ssdbCommentNew.del(String.format(EMPTY_TID, tid));
	// }
	// }
	//
	// private boolean addComments(final String auditUserNmae,
	// List<CommentCheckColumn> commentColumns)
	// throws IOException {
	// for (CommentColumn commentColumn : commentColumns) {
	// final IdColumn idColumn = gson.fromJson(commentColumn.getIdColumnValue(),
	// IdColumn.class);
	// final UserColumn userColumn =
	// gson.fromJson(commentColumn.getUserColumnValue(), UserColumn.class);
	// final ContentColumn contentColumn =
	// gson.fromJson(commentColumn.getCommentColumnValue(),
	// ContentColumn.class);
	// final String tid = idColumn.getTid();
	// final int sourceAppId = idColumn.getSourceAppId();
	// final String sourceId = idColumn.getSourceId();
	// final int typeId = idColumn.getTypeId();
	// final Long userId = userColumn.getUid();
	// final String deviceId = idColumn.getDevice();
	// // 添加评论到评论展示表
	// commentDao.addComment(idColumn.getAppId(), idColumn.getTypeId(), tid,
	// commentColumn.getCommentId(),
	// commentColumn.getIdColumnValue(), commentColumn.getCommentColumnValue(),
	// commentColumn.getUserColumnValue(), commentColumn.getCountColumnValue(),
	// commentColumn.getExtColumnValue());
	//
	// // 通过kafka写入ES做全文索引
	// kafkaExecutor.execute(new Runnable() {
	// @Override
	// public void run() {
	// try {
	// logger.debug("start send comment to kafka {}", idColumn.getCid());
	// sendKafkaToAudit(auditUserNmae, CommentAuditStatusEnum.PASSED, idColumn,
	// userColumn,
	// contentColumn.getContent());
	// logger.debug("success send comment to kafka {}", idColumn.getCid());
	// } catch (Exception e) {
	// logger.warn("error send comment to kafka {}", idColumn.getCid(), e);
	// }
	// }
	// });
	//
	// boolean isReply = false;
	// long receiver = 0L;
	//
	// // 回复类型的评论
	// if (contentColumn.getReply() != null && contentColumn.getReply().size() >
	// 0) {
	// isReply = true;
	// CommentReplyResp commentReplyResp = contentColumn.getReply().get(0);
	// long previousUserId = 0;
	// String previousContent = commentReplyResp.getContent();
	// receiver = commentReplyResp.getUid();
	// long replyCid = commentReplyResp.getCid();
	// CommentColumn replyComment = commentDao.getComment(3, 1, tid, replyCid);
	// if (replyComment != null) {
	// ContentColumn replyContentColumn =
	// gson.fromJson(replyComment.getCommentColumnValue(),
	// ContentColumn.class);
	// if (replyContentColumn != null && replyContentColumn.getReply() != null)
	// {
	// CommentReplyResp replyReplyResp = replyContentColumn.getReply().get(0);
	// previousUserId = replyReplyResp.getUid();
	// }
	// }
	// // 非手雷入库
	// if (idColumn.getSourceAppId() != 17 || idColumn.getSourceAppId() != 18) {
	// userMessageService.addReplyMessage(userId, receiver,
	// contentColumn.getContent(),
	// idColumn.getTypeId(), sourceId, String.valueOf(idColumn.getCid()), tid,
	// previousUserId,
	// null, previousContent);
	// // 添加回复评论的id到被回复评论的ext列
	// addCommentReplyerId(idColumn.getAppId(), idColumn.getTypeId(), tid,
	// contentColumn.getReply().get(0).getCid(), idColumn.getCid());
	// }
	// } else {
	// // 非回复的评论发消息给视频所属人
	// if (idColumn.getSourceAppId() != 17 || idColumn.getSourceAppId() != 18) {
	// // 非手雷的入库拿视频所属人
	// receiver = userMessageService.addCommentMessage(userId,
	// contentColumn.getContent(),
	// idColumn.getTypeId(), sourceId, String.valueOf(idColumn.getCid()), tid);
	// } else {
	// // 手雷的因为不入库所以自己拿视频所属人
	// long videoId = NumberUtils.toLong(sourceId);
	// HotVideoDTO hotVideo = fileService.queryAllState(0, videoId,
	// CdnType.QINIU, false);
	// if (hotVideo != null) {
	// receiver = hotVideo.getUserId();
	// }
	// }
	// }
	// sendFeed(sourceAppId, sourceId, userId, deviceId, receiver);
	// // 给手雷消息中心发评论回复类型的消息
	// final String type = isReply ? "reply_comment" : "comment_video";
	// final long consumer = receiver;
	// final String gcid = idColumn.getTid();
	// final String original = contentColumn.getContent();
	// final String reply = isReply ?
	// contentColumn.getReply().get(0).getContent() : null;
	// final long mcid = idColumn.getCid();
	// messageExecutor.execute(new Runnable() {
	// @Override
	// public void run() {
	// Map<String, Object> messageMap = new HashMap<>();
	// messageMap.put("type", type);
	// messageMap.put("initiator", userId);
	// messageMap.put("consumer", consumer);
	// messageMap.put("resource_id", sourceId);
	// messageMap.put("gcid", gcid);
	// messageMap.put("original", original);
	// if (StringUtils.isNotBlank(reply)) {
	// messageMap.put("reply", reply);
	// }
	// messageMap.put("cid", mcid);
	// String messageJson = gson.toJson(messageMap);
	// HttpUtil.post(client, messageUrl, messageJson);
	// }
	// });
	// // 添加评论到用户评论表
	// addUserComment(idColumn.getAppId(), idColumn.getTypeId(),
	// idColumn.getTid(), userColumn.getUid(),
	// commentColumn.getCommentId(), idColumn.getTime());
	// CommentResp resp = new CommentResp();
	// CommentReviewResp reviewResp = ssdbx.hget(
	// getCommentHKey(idColumn.getAppId(), idColumn.getTypeId(),
	// idColumn.getTid()),
	// commentColumn.getCommentId(), CommentReviewResp.class);
	// if (reviewResp != null) {
	// resp = reviewResp;
	// } else {
	// resp.setCid(commentColumn.getCommentId());
	// resp.setComment(contentColumn.getContent());
	// if (contentColumn.getReply() != null && contentColumn.getReply().size() >
	// 0) {
	// resp.setReplys(contentColumn.getReply());
	// }
	// resp.setDevice(idColumn.getDevice());
	// resp.setSourceId(idColumn.getSourceId());
	// resp.setPo(idColumn.getPo());
	// resp.setCi(idColumn.getCi());
	// resp.setGcount(0);
	// resp.setRcount(0);
	// resp.setScount(0);
	// resp.setTime(idColumn.getTime());
	// resp.setUid(userColumn.getUid());
	// resp.setUserName(userColumn.getName());
	// resp.setUserImg(userColumn.getImg());
	// // PC迅雷增加两个字段
	// ExtColumn extColumn = gson.fromJson(commentColumn.getExtColumnValue(),
	// ExtColumn.class);
	// resp.setDownLoadSpeed(extColumn.getDownLoadSpeed());
	// resp.setAnonymous(extColumn.isAnonymous());
	// resp.setBandwidth(extColumn.getBandwidth());
	// resp.setExtParamsJson(extColumn.getExeParamsJson());
	// }
	// isReplyDel(idColumn.getAppId(), idColumn.getTypeId(), idColumn.getTid(),
	// resp);
	// // 审核通过即添加到评论缓存中
	// addCommentCache(idColumn.getAppId(), idColumn.getTypeId(),
	// idColumn.getTid(), resp);
	// // 更新hbase中的评论计数
	// if (typeId == 1 && NumberUtils.isDigits(idColumn.getSourceId())) {
	// long videoId = Long.parseLong(idColumn.getSourceId());
	// fileService.updateCommentNum(videoId, this.commentCount(tid, videoId));
	// }
	// // 删除用户待审核的的缓存评论
	// delReviewCommentCache(userColumn.getUid(), idColumn.getCid());
	// if (idColumn.getTypeId() == 1 &&
	// NumberUtils.isDigits(idColumn.getSourceId())) {
	// redisService.del(String.format(COMMENT_COUNT, tid));
	// }
	//
	// try {
	// if (typeId == 1) {// 视频被评论，评论被回复
	// RaUserInfo raUserInfo = raUserInfoDao.getUserInfo(receiver);
	//
	// boolean newCommentMessageOn = raUserInfo != null &&
	// raUserInfo.newCommentMessageOn();
	// if (newCommentMessageOn) {
	// // "videoId": "29520455621411840",
	// // "commenter_headIconUrl":"评论者的头像url",
	// // "commenter_title":"评论内容",
	// // "commenter_sourceId":"评论id",
	// // "commenter_userTypes":"评论者类型，是否是原创达人",
	// // "commenter_userName":"评论者名字"
	// List<Integer> userTypes =
	// userAccountService.getUserKind(userColumn.getUid());
	// if (userTypes == null) {
	// userTypes = new ArrayList<>();
	// }
	// Map<String, Object> extra = new HashMap<>();
	// extra.put("videoId", Long.valueOf(idColumn.getSourceId()));
	// extra.put("commenter_headIconUrl", userColumn.getImg());
	// extra.put("commenter_title", contentColumn.getContent());
	// extra.put("commenter_sourceId", idColumn.getCid());
	// extra.put("commenter_userTypes", userTypes);
	// extra.put("commenter_userName", userColumn.getName());
	//
	// if (isReply) {
	// if (receiver != userColumn.getUid()) {
	// userMessageService.sendMessage(receiver,
	// UserMessageService.MIPUSH_MESSAGE_TYPE_NEW_MESSAGE_111, "新消息通知", "",
	// userColumn.getName() + "回复了你：" + contentColumn.getContent(), extra);
	// }
	// } else {
	// if (receiver != userColumn.getUid()) {
	// userMessageService.sendMessage(receiver,
	// UserMessageService.MIPUSH_MESSAGE_TYPE_NEW_MESSAGE_111, "新消息通知", "",
	// userColumn.getName() + "评论了你：" + contentColumn.getContent(), extra);
	// }
	// }
	// }
	// }
	// } catch (Exception e) {
	// logger.info(e.getMessage(), e);
	// }
	// }
	// return true;
	// }
	//
	// /**
	// * 发送feed
	// */
	// private void sendFeed(int sourceAppId, String sourceId, long userId,
	// String deviceId, long receiver) {
	// FeedAction feedAction = new FeedAction();
	// feedAction.setActAppId(sourceAppId);// appid
	// feedAction.setActAppVersion("");// FIXME 没存
	// feedAction.setActId(sourceId);
	// feedAction.setActType(FeedActionType.COMMENT.getType());// 操作类型 1 发布 2
	// // 评论 3 点赞
	// feedAction.setActUserId(userId);// 操作用户id
	// feedAction.setDeviceId(deviceId);// 设备id
	// feedAction.setOsType("");// 操作系统类型 //FIXME 没存
	//
	// FeedResOnlyContent feedResOnlyContent = new FeedResOnlyContent();
	// feedResOnlyContent.setResContent(userId + "评论了" + receiver + "发布的短视频");
	// feedResOnlyContent.setResId(receiver + "");
	// feedResOnlyContent.setResPublished(System.currentTimeMillis());
	// feedResOnlyContent.setResType(FeedResourceType.FeedResOnlyContent.getType());
	// feedResOnlyContent.setResUserId(receiver); // 资源所属人
	//
	// FeedProductReq req = new FeedProductReq();
	// req.setAction(gson.toJson(feedAction));
	// req.setActType(feedAction.getActType());
	// req.setFeedStat(FeedStat.STAT_OK.getStat());
	// req.setResource(gson.toJson(feedResOnlyContent));
	// req.setResType(feedResOnlyContent.getResType());
	// req.setUserId(feedAction.getActUserId());
	// feedService.feedProduct(actAppId, actAppSecretKey, req);
	// }
	//
	// /**
	// * 审核通过时判断回复的评论是否已删除
	// */
	// private void isReplyDel(int appId, int typeId, String tid, CommentResp
	// resp) {
	// if (resp.getReplys() != null) {
	// List<CommentReplyResp> replys = resp.getReplys();
	// for (CommentReplyResp reply : replys) {
	// String idKey = getCommentIdZKey(appId, typeId, tid, "new");
	// if (ssdbx.zget(idKey, reply.getCid()) <= 0) {
	// reply.setCid(-1L);
	// reply.setContent("该评论已删除!");
	// }
	// }
	// }
	// }
	//
	// @Override
	// public void report(int typeId, int appId, int type, String tid, long cid)
	// throws IllegalArgumentException, IOException {
	// String reportKey = String.format(COMMENT_REPORT_COUNT, cid);
	// String reportLogKey = String.format(COMMENT_REPORT_LOG, cid);
	// long num = ssdbFast.incr(reportKey, 1);
	// String reportLog = ssdbFast.get(reportLogKey, String.class);
	// if (StringUtils.isBlank(reportLog)) {
	// reportLog = "";
	// }
	// String currentLog = type + ":" + System.currentTimeMillis() + ",";
	// reportLog += currentLog;
	// ssdbFast.set(reportLogKey, reportLog);
	// if (num > 9) {
	// List<CommentCheckColumn> commentColumns =
	// commentDao.getCheckPassComments(typeId, cid);
	// if (commentColumns == null || commentColumns.size() < 1) {
	// return;
	// }
	// for (CommentCheckColumn commentColumn : commentColumns) {
	// commentColumn.setChecker("report");
	// commentColumn.setCheckTime(System.currentTimeMillis());
	// }
	// if (commentDao.addReviewComments(typeId, commentColumns)) {
	// commentDao.delCheckPassComments(typeId, cid);
	// for (CommentColumn commentColumn : commentColumns) {
	// IdColumn idColumn = gson.fromJson(commentColumn.getIdColumnValue(),
	// IdColumn.class);
	// ExtColumn extColumn = gson.fromJson(commentColumn.getExtColumnValue(),
	// ExtColumn.class);
	// UserColumn userColumn = gson.fromJson(commentColumn.getUserColumnValue(),
	// UserColumn.class);
	// commentDao.del(idColumn.getAppId(), idColumn.getTypeId(),
	// idColumn.getTid(), idColumn.getCid());
	// commentDao.delUserComment(userColumn.getUid(), idColumn.getCid());
	// delCache(idColumn.getAppId(), idColumn.getTypeId(), idColumn.getTid(),
	// idColumn.getCid(),
	// extColumn.getReplyerCids());
	// commentDao.addCheckNoPassComments(typeId, commentColumns);
	// }
	// }
	// ssdbFast.del(COMMENT_REPORT_COUNT);
	// }
	// }
	//
	// @Override
	// public CommentResp get(int typeId, long cid) throws
	// IllegalArgumentException, IOException {
	// List<CommentCheckColumn> comments =
	// commentDao.getCheckPassComments(typeId, cid);
	// CommentResp commentResp = new CommentResp();
	// if (comments != null && comments.size() > 0) {
	// IdColumn idColumn = gson.fromJson(comments.get(0).getIdColumnValue(),
	// IdColumn.class);
	// ContentColumn contentColumn =
	// gson.fromJson(comments.get(0).getCommentColumnValue(),
	// ContentColumn.class);
	// UserColumn userColumn =
	// gson.fromJson(comments.get(0).getUserColumnValue(), UserColumn.class);
	// CountColumn countColumn =
	// gson.fromJson(comments.get(0).getCountColumnValue(), CountColumn.class);
	// commentResp.setCid(idColumn.getCid());
	// commentResp.setComment(contentColumn.getContent());
	// commentResp.setDevice(idColumn.getDevice());
	// commentResp.setGcount(countColumn.getGcount());
	// commentResp.setSourceId(idColumn.getSourceId());
	// commentResp.setScount(countColumn.getScount());
	// commentResp.setRcount(countColumn.getRcount());
	// commentResp.setUid(userColumn.getUid());
	// commentResp.setUserImg(userColumn.getImg());
	// commentResp.setUserName(userColumn.getName());
	// commentResp.setTime(idColumn.getTime());
	// commentResp.setCommentType(idColumn.getCommentType());
	//
	// }
	// return commentResp;
	// }
	//
	// @Override
	// public CommentStatListResp commentCheckerStat(String checker, long
	// checkStartTime, long checkEndTime) {
	//
	// if (StringUtils.isBlank(checker)) {
	// checker = null;
	// }
	// if (checkEndTime < 1) {
	// checkEndTime = System.currentTimeMillis();
	// }
	// List<CommentStat> commentStats =
	// commentMySqlDao.listCommentDetail(checker, checkStartTime, checkEndTime);
	// CommentStatListResp resp = new CommentStatListResp();
	// if (CollectionUtils.isEmpty(commentStats)) {
	// resp.setCommentStats(new ArrayList<CommentStatResp>(0));
	// return resp;
	// }
	// List<CommentStatResp> commentStatResps = new
	// ArrayList<>(commentStats.size());
	// for (CommentStat commentStat : commentStats) {
	// CommentStatResp commentStatResp = new CommentStatResp();
	// commentStatResp.setChecker(commentStat.getCommentChecker());
	// commentStatResp.setCheckCount(commentStat.getCheckCount());
	// commentStatResp.setPassCount(commentStat.getPassCount());
	// commentStatResp.setNoPassCount(commentStat.getNopassCount());
	// commentStatResp.setOnlineCount(commentStat.getOnlineCount());
	// commentStatResp.setOfflineCount(commentStat.getOfflineCount());
	// commentStatResps.add(commentStatResp);
	// }
	// resp.setCommentStats(commentStatResps);
	// return resp;
	// }
	//
	// /**
	// * 登录的时候调用 把原来deviceId下面的点赞信息刷新到userId上
	// */
	// @Override
	// public int updateDevicePraiseToUser(Long userId, String deviceId) {
	// logger.info(String.format("transfer log from device %s to user %s",
	// deviceId, userId));
	// // 如果有历史device数据则转移到当前userId下面,清空原device表的记录
	// int count = commentPraiserDeviceDao.countByDeviceId(deviceId);
	// long[] cids;
	// if (count > 0) {
	// commentPraiserUserDao.saveFromDeviceDetail(userId, deviceId);
	// cids = commentPraiserDeviceDao.queryAll(deviceId);
	// commentPraiserDeviceDao.deleteByDeviceId(deviceId);
	// // 处理缓存
	// String userKey = getCommentPrasierKey(userId);
	// String deviceKey = getCommentPrasierKey(deviceId);
	// Map<Long, Long> temp = new HashMap<>();
	// for (long cid : cids) {
	// temp.put(cid, cid);
	// }
	// ssdbx.multi_zset(userKey, temp);
	// // 删掉ssdb中的缓存
	// ssdbx.zclear(deviceKey);
	// }
	// return count;
	// }
	//
	// private static final String HKEY_TID_COUNT = "0";
	//
	// /**
	// * 资源查询评论数
	// */
	// private int commentCountTid(String tid, int typeId) {
	// final CommandContext ctx = McpThreadLocal.thCommandContext.get();
	// try {
	// String idKey = getCommentIdZKey(3, typeId, tid, "new");
	// String commentCountKey = String.format(COMMENT_COUNT, tid);
	// McpUtils.addStat("clCctStart", System.currentTimeMillis());
	// Integer count = redisService.hget(commentCountKey, HKEY_TID_COUNT,
	// Integer.class);
	// McpUtils.addStat("clCctGetE", System.currentTimeMillis());
	// if (count == null || count <= 0) {
	// if (count != null) {
	// McpUtils.addStat("clCctDelS", System.currentTimeMillis());
	// redisService.del(commentCountKey);
	// McpUtils.addStat("clCctDelE", System.currentTimeMillis());
	// }
	// if (ssdbCommentNew.exists(String.format(EMPTY_TID, tid)) == 1) {
	// return 0;
	// } else {
	// McpUtils.addStat("clCctZsizeS", System.currentTimeMillis());
	// count = (int) ssdbx.zsize(idKey);
	// McpUtils.addStat("clCctZsizeE", System.currentTimeMillis());
	// if (count == 0) {
	// McpUtils.addStat("clCctSetS", System.currentTimeMillis());
	// ssdbCommentNew.set(String.format(EMPTY_TID, tid), -1, 3600 * 24 * 5);
	// McpUtils.addStat("clCctSetE", System.currentTimeMillis());
	// } else {
	// McpUtils.addStat("clCctSetexS", System.currentTimeMillis());
	// redisService.hset(commentCountKey, HKEY_TID_COUNT, count);
	// McpUtils.addStat("clCctSetexE", System.currentTimeMillis());
	// }
	// }
	// }
	// McpUtils.addStat("clCctEnd", System.currentTimeMillis());
	// return count;
	// } catch (Exception e) {
	// logger.warn("count comment of tid error，tid:" + tid, e);
	// McpUtils.addStat("clCctEndE", System.currentTimeMillis());
	// return -1;
	// }
	// }
	//
	// /**
	// * 短视频查询评论数
	// */
	// public int commentCount(String tid, long videoId) {
	// try {
	// String countKey = String.format(COMMENT_COUNT, tid);
	// String countHKey = String.valueOf(videoId);
	// McpUtils.addStat("clCcStart", System.currentTimeMillis());
	// Integer count = redisService.hget(countKey, countHKey, Integer.class);
	// McpUtils.addStat("clCcGetE", System.currentTimeMillis());
	// if (count == null || count == 0) {
	// String idKey = getCommentIdZKey(3, 1, tid, "new");
	// McpUtils.addStat("clCcZcountS", System.currentTimeMillis());
	// count = (int) ssdbx.zcount(idKey, "", String.valueOf(Long.MAX_VALUE -
	// videoId));
	// McpUtils.addStat("clCcZcountE", System.currentTimeMillis());
	// redisService.hset(countKey, countHKey, count);
	// McpUtils.addStat("clCcEnd", System.currentTimeMillis());
	// return count;
	// } else {
	// return count;
	// }
	// } catch (Exception e) {
	// logger.warn("count comment of video error，videoId:" + videoId, e);
	// McpUtils.addStat("clCcEndE", System.currentTimeMillis());
	// return -1;
	// }
	// }
	//
	// @Override
	// public CommentBatchVideoReq commentCountByVideoIds(CommentBatchVideoReq
	// req) {
	// for (CommentBatchVideoReq.CommentBatchVideo commentBatchVideo :
	// req.getCommentBatchVideo()) {
	// try {
	// commentBatchVideo
	// .setRcount(this.commentCount(commentBatchVideo.getGcid(),
	// commentBatchVideo.getVideoId()));
	// } catch (Exception e) {
	// logger.warn("commentCountByVideoIds error videoId:{}",
	// commentBatchVideo.getVideoId(), e);
	// }
	// }
	// return req;
	// }
}
