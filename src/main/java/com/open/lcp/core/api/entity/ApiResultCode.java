package com.open.lcp.core.api.entity;

/**
 * "E"表示“error”(错误)；"SYS"表示"system"（系统平台级的）;"biz"表示“business”（业务级的）
 */
public final class ApiResultCode {

	/** 成功 **/

	public final static int SUCCESS = 0;

	/** 小于1000的为系统错误 **/

	public final static int E_SYS_UNKNOWN = 1;// 系统服务错误

	public final static int E_SYS_PARAM = 2;// 无效的请求参数

	public final static int E_SYS_PERMISSION_DENY = 3;// 用户无权限

	public final static int E_SYS_REQUEST_TOO_FAST = 4;// 用户操作过于频繁

	public final static int E_SYS_RPC_ERROR = 5;// RPC error

	public final static int E_SYS_INVALID_APP_ID = 6;// 无效的app id

	public final static int E_SYS_INVALID_TICKET = 7;// 无效的ticket

	public final static int E_SYS_INVALID_SIG = 8;// 无效的签名

	public final static int E_SYS_INVALID_VERSION = 9;// 无效的版本

	public final static int E_SYS_UNKNOWN_METHOD = 10;// 未知接口请求

	public final static int E_SYS_UNKNOWN_RESULT_FORMAT = 11;// 未知输出格式

	public final static int E_SYS_RPC_NULL = 12;// 底层服务没有抛出异常，正常返回，返回的结果为null

	public final static int E_SYS_UNSUPPORTED_FILE_TYPE = 13;// 不支持的文件类型

	public final static int E_BIZ_BATCH_RUN_CYCLE_CALL = 14; // 批处理时，循环调用了

	public final static int E_SYS_FORCED_UPDATE = 15; // 强直升级

	public final static int E_SYS_FAULT_ISOLATION = 16; // 接口遭故障隔离

	public final static int E_SYS_TICKET_NOT_EXIST = 17; // 没传t票

	public final static int E_SYS_LACKOF_REQUIRED_PARAM = 18;// 缺必选参数
	public final static int E_SYS_RESP = 22;// 无效的返回数据
	public final static int E_USER_TOO_FAST = 24;// 触发用户级频控：用户操作过于频繁。

	/** 大于1000且小于2000为Account业务级错误 **/

	public final static int E_BIZ_ACCOUNT_LOGIN_ERROR = 1001;// 登陆失败

	public final static int E_BIZ_ACCOUNT_INVALID_USER = 1002;// 登陆失败，此帐户不存在

	public final static int E_BIZ_ACCOUNT_WRONG_PWD = 1003;// 登陆失败，密码不正确

	public final static int E_BIZ_ACCOUNT_INVALID_AT = 1004; // 无效的access token

	public final static int E_BIZ_ACCOUNT_PROFILE_ERROR = 1005; // 该手机号已使用，请更换手机号绑定

	public final static int E_BIZ_ACCOUNT_QUAD_CONTACT_LIST_ERROR = 1006; // 用户尚未进行绑定验证操作

	public final static int E_BIZ_ACCOUNT_FB_CONTACT_LIST_ERROR = 1007; // 验证码错误

	public final static int E_BIZ_ACCOUNT_INVALID_QUAD_USER = 1008; // 批处理时，循环调用了

	public final static int E_BIZ_ACCOUNT_INVALID_FB_USER = 1009; // 不存在的Facebook用户

	public final static int E_BIZ_ACCOUNT_EXCEED_CONTACT_NUMBER_LIMIT = 1010; // 好友数量达到上限

	public final static int E_BIZ_ACCOUNT_INVALID_CONTACT = 1011; // 该用户不是本人的Quad联系人

	public final static int E_BIZ_ACCOUNT_DELETE_CONTACT_ERROR = 1012; // 删除Quad好友失败

	public final static int E_BIZ_ACCOUNT_GET_RECOMMEND_ERROR = 1013; // 删除推荐好友失败

	public final static int E_BIZ_ACCOUNT_USER_BLOCKED = 1014; // 用户已经被禁止登录

	public final static int E_BIZ_ACCOUNT_MOBILE_REGISTERED = 1015; // 该手机号已经被注册

	public final static int E_BIZ_ACCOUNT_SEND_MOBILE_VERIFY_FAIL = 1016; // 手机验证码发送失败

	public final static int E_BIZ_ACCOUNT_SEND_MOBILE_VERIFY_EXCEED_LIMT = 1017; // 手机验证码发送次数超限

	public final static int E_BIZ_ACCOUNT_BAD_MOBILE_VERIFY_CODE = 1018; // 手机验证码错误或者已过期

	public final static int E_BIZ_ACCOUNT_UNBIND_MOBILE_FAIL_NO_OTHERACCOUNT = 1019; // 没有其他有效帐号，解除手机绑定失败

	public final static int E_BIZ_ACCOUNT_UNBIND_FACEBOOK_FAIL_NO_OTHERACCOUNT = 1020; // 没有其他有效帐号，解除Facebook绑定失败

	public final static int E_BIZ_ACCOUNT_FB_USER_REGISTERED = 1021; // 用户已经注册

	public final static int E_BIZ_ACCOUNT_SEND_MOBILE_NUMBER_EXCEED_LIMIT = 1022; // 发送目标的手机号数量限制

	public final static int E_BIZ_ACCOUNT_FB_BIND_DUPLICATED = 1023; // 该用户已经绑定了facebook账号

	public final static int E_BIZ_ACCOUNT_INVITE_TIMES_EXCEED_LIMIT = 1024; // 发送邀请短信数量超出限制

	public final static int E_BIZ_ACCOUNT_SEND_SMS_FAIL = 1025; // 短信发送失败

	public final static int E_BIZ_ACCOUNT_SEND_INVITE_FB_FAIL = 1026; // 发送facebook邀请失败

	/** 大于2000且小于3000为Feed业务级错误 **/

	public final static int E_BIZ_FILE_TOO_LARGE_FILE = 2001;// 上传的文件太大

	public final static int E_BIZ_FEED_UPLOAD_ERROR = 2002;// 上传文件失败

	public final static int E_BIZ_FEED_SELECT_USER_LIMIT = 2003;// 选择的用户数量超过限制

	public final static int E_BIZ_FEED_WRONG_TYPE = 2004; // 错误的feed类型

	public final static int E_BIZ_FEED_PUBLISH_FREQUENCY = 2005; // 发布feed过快

	public final static int E_BIZ_FEED_PUBLISH_ERROR = 2006; // 发布feed失败

	public final static int E_BIZ_FEED_INVALID_ID = 2007; // 不存在的Feed ID

	public final static int E_BIZ_FEED_IGNORE_ERROR = 2008; // 屏蔽feed失败

	public final static int E_BIZ_FEED_QUIT_ERROR = 2009; // 退出feed失败

	public final static int E_BIZ_FEED_DELETE_DUPLICATE = 2010; // 该图片已删除

	public final static int E_BIZ_FEED_DELETE_PICTURE_ERROR = 2011;// 删除feed图片失败

	public final static int E_BIZ_FEED_GET_LIST_ERROR = 2012;// 获取feed列表失败

	public final static int E_BIZ_FEED_WRONG_COMMENT_TYPE = 2013;// 错误的评论类型

	public final static int E_BIZ_FEED_PUBLISH_COMMENT_FREQUENCY = 2014; // 发布评论过快

	public final static int E_BIZ_FEED_PUBLISH_COMMENT_ERROR = 2015; // 发布评论失败

	public final static int E_BIZ_FEED_GET_CONMMENT_LIST_ERROR = 2016; // 获取评论列表失败

	public final static int E_BIZ_FEED_GUESS_NUMBER_LIMIT = 2017; // 猜的人数超过限制

	public final static int E_BIZ_FEED_CONFESS_DUPLICATE = 2018; // 该feed已自首

	public final static int E_BIZ_FEED_GUESS_DUPLICATE = 2019; // 该用户已猜过该feed

	public final static int E_BIZ_FEED_GUESS_ERROR = 2020; // 猜失败

	public final static int E_BIZ_FEED_CONFESS_AUTHORITY = 2021; // 不能在别人的feed中自首

	public final static int E_BIZ_FEED_CONFESS_ERROR = 2022; // 自首失败

	public final static int E_BIZ_FEED_GET_GUESS_STATUS_ERROR = 2023; // 获取猜人动态失败

	public final static int E_BIZ_FEED_HAS_BEEN_DELETED = 2024; // 请求的feed已经被删除

	public final static int E_BIZ_FEED_HAS_BEEN_DUMPED = 2025; // Feed已经被屏蔽

	public final static int E_BIZ_FEED_HAS_BEEN_UNDUMPED = 2026; // Feed已经被取消屏蔽

	public final static int E_BIZ_FEED_ONE_MEMBER = 2027; // 不能发布只猜自己的Feed

	public final static int E_BIZ_FEED_TOO_LONG_TEXT = 2028; // 该feed的评论的文本过长

	public final static int E_BIZ_FEED_HAS_NO_VOICE = 2029; // 该feed没有语音内容

	/** 大于3000且小于4000为Message业务级错误 **/

	public final static int E_BIZ_MSG_INVALID_TYPE = 3001;// 错误的消息类型

	/** 大于4000且小于5000为System业务级错误 **/

	public final static int E_BIZ_SYS_FEEDBACK_FREQUENCY = 4001;// 提交反馈过快

	public final static int E_BIZ_SYS_FEEDBACK_ERROR = 4002;// 提交反馈失败

	public final static int E_BIZ_SYS_SUE_DUPLICATE = 4003;// 已举报过该feed

	public final static int E_BIZ_SYS_SUE_ERROR = 4004;// 举报失败

	/** 大于5000且小于6000为Chat业务级错误 **/

	public final static int E_BIZ_CHAT_GROUP_MEMBER_EXCEED = 5001;// 群成员数量达到上限

	public final static int E_BIZ_CHAT_GROUP_MEMBER_INSUFFICIENT = 5002;// 群成员数量不足

	public final static int E_BIZ_CHAT_GROUP_CREATE_ERROR = 5003;// 建群失败

	public final static int E_BIZ_CHAT_GROUP_ADD_MEMBER_ERROR = 5004; // 添加群成员失败

	public final static int E_BIZ_CHAT_GROUP_NOT_MEMBER = 5005; // 非本群成员

	public final static int E_BIZ_CHAT_GROUP_NOT_EXIST = 5006; // 不存在的群

	public final static int E_BIZ_CHAT_GROUP_EDIT_NAME_ERROR = 5007; // 编辑群名失败

	public final static int E_BIZ_CHAT_MSG_SEND_ERROR = 5008; // 发送消息失败

	public final static int E_BIZ_CHAT_GROUP_MUTE_ERROR = 5009; // 屏蔽群失败

	public final static int E_BIZ_CHAT_GROUP_UNMUTE_ERROR = 5010; // 取消群屏蔽失败

	public final static int E_BIZ_CHAT_GROUP_QUIT_ERROR = 5011; // 退群失败

	public final static int E_BIZ_CHAT_GROUP_FETCH_INFO_ERROR = 5012; // 获取群信息失败

	public final static int E_BIZ_CHAT_GROUP_MEMBER_INFO_FETCH_ERROR = 5013; // 获取群成员信息失败

	public final static int E_BIZ_CHAT_GROUP_MUTE_STATUS_ERROR = 5014; // 获取群屏蔽状态失败

	public final static int E_BIZ_CHAT_MSG_RECV_ERROR = 5015; // 接收消息失败

	public final static int E_BIZ_CHAT_INIT_ERROR = 5016; // 聊天初始化失败

	public final static int E_BIZ_CHAT_USER_MUTE_ERROR = 5017; // 屏蔽用户失败

	public final static int E_BIZ_CHAT_USER_UNMUTE_ERROR = 5018; // 取消对用户的屏蔽失败

	public final static int E_BIZ_CHAT_MUTE_STATUS_ERROR = 5019; // 获取屏蔽状态失败

	public final static int E_BIZ_CHAT_LBS_GROUP_NOT_EXIST = 5034; // 群不存在

	public final static int E_BIZ_EVENT_NOT_EXIST = 5036;// 不存在的event

	public final static int E_BIZ_EVENT_DELETE_NOT_OWNER = 5037;// 不是本人或群主删除event

	public final static int E_BIZ_EVENT_DELETE_ERROR = 5038;// 删除event失败

	public final static int E_BIZ_EVENT_ADD_MEMBER_ERROR = 5039;// 添加成员失败

	public final static int E_BIZ_EVENT_QUIT_ERROR = 5040;// 退出event失败

	//
	public final static int VIDEO_OPTION_FAILED = 8000;
	public final static int VIDEO_CHANNEL_HAS_CONFIGED = 8001;
	public final static int VIDEO_NOT_EXIST = 8002;

	public final static int USER_IN_BLACKLIST = 70008;
}
