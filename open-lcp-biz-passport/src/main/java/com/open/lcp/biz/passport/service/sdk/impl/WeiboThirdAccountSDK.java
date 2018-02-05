package com.open.lcp.biz.passport.service.sdk.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.open.lcp.biz.passport.PassportException;
import com.open.lcp.biz.passport.service.sdk.ThirdAccountSDKPortrait;
import com.open.lcp.biz.passport.util.PlaceholderAvatarUtil;
import com.open.lcp.core.common.enums.Gender;
import com.open.lcp.core.common.util.HttpUtil;
import com.open.lcp.core.feature.user.api.UserType;

@Component("weiboThirdAccountSDK")
public class WeiboThirdAccountSDK extends AbstractThirdAccountSDK {

	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(WeiboThirdAccountSDK.class);

	@Autowired
	protected CloseableHttpClient httpClient;

	private static final List<String> DEFAULT_AVATAR_URL_LIST;

	// http://tva3.sinaimg.cn/default/images/default_avatar_female_50.gif
	// http://tva3.sinaimg.cn/default/images/default_avatar_male_50.gif
	static {
		DEFAULT_AVATAR_URL_LIST = new ArrayList<String>();
		DEFAULT_AVATAR_URL_LIST.add("default_avatar_female");
		DEFAULT_AVATAR_URL_LIST.add("default_avatar_male");
	}

	// http://open.weibo.com/wiki/2/users/show
	@Override
	public ThirdAccountSDKPortrait validateAndObtainUserPortrait(String oauthAppId, String openId, String accessToken)
			throws PassportException {

		try {
			// String url =
			// "https://api.weixin.qq.com/sns/userinfo?access_token="
			// + accessToken + "&openid=" + openId;
			Gson gson = new Gson();
			// String url = "https://api.weibo.com/2/users/show.json";
			// String url = "https://api.weibo.com/2/eps/user/info.json";
			String url = "https://api.weibo.com/2/users/show.json?access_token=" + accessToken + "&uid=" + openId;
			// WeiboValidateAndGetUserReqDTO reqDto = new
			// WeiboValidateAndGetUserReqDTO();
			// reqDto.setAccess_token(accessToken);
			// reqDto.setUid(Long.valueOf(openId));
			// String json = gson.toJson(reqDto);

			// HttpResult httpResult = getCommonHttpClient().httpPostJson(url,
			// json);

			String result = HttpUtil.get(httpClient, url);

			// {"id":5909177590,"idstr":"5909177590","class":1,"screen_name":"不若不遇倾城�?","name":"不若不遇倾城�?","province":"11","city":"1","location":"北京
			// 东城�?","description":"","url":"","profile_image_url":"http://tva3.sinaimg.cn/default/images/default_avatar_female_50.gif","profile_url":"u/5909177590","domain":"","weihao":"","gender":"f","followers_count":1,"friends_count":42,"pagefriends_count":0,"statuses_count":7,"favourites_count":0,"created_at":"Tue
			// Apr 26 16:46:45 +0800
			// 2016","following":false,"allow_all_act_msg":false,"geo_enabled":true,"verified":false,"verified_type":-1,"remark":"","status":{"created_at":"Tue
			// Sep 06 11:43:58 +0800
			// 2016","id":4016675122699158,"mid":"4016675122699158","idstr":"4016675122699158","text":"ߚ«快来，这个视频再不看就没有了ߚ«ߚ«快来，这个视频再不看就没有了ߚ«
			// http://t.cn/RcUjmoy","textLength":92,"source_allowclick":0,"source_type":1,"source":"<a
			// href=\"http://app.weibo.com/t/feed/2kDjtx\"
			// rel=\"nofollow\">有料视频</a>","favorited":false,"truncated":false,"in_reply_to_status_id":"","in_reply_to_user_id":"","in_reply_to_screen_name":"","pic_urls":[{"thumbnail_pic":"http://ww3.sinaimg.cn/thumbnail/006rUj1Yjw1f7jpvky1glj309s07cmx4.jpg"}],"thumbnail_pic":"http://ww3.sinaimg.cn/thumbnail/006rUj1Yjw1f7jpvky1glj309s07cmx4.jpg","bmiddle_pic":"http://ww3.sinaimg.cn/bmiddle/006rUj1Yjw1f7jpvky1glj309s07cmx4.jpg","original_pic":"http://ww3.sinaimg.cn/large/006rUj1Yjw1f7jpvky1glj309s07cmx4.jpg","geo":null,"annotations":[{"client_mblogid":"91f2288d-a3a7-4c27-b528-7a4126874c5a"},{"mapi_request":true}],"filterID":"0:1","reposts_count":0,"comments_count":0,"attitudes_count":0,"isLongText":false,"mlevel":0,"visible":{"type":0,"list_id":0},"biz_feature":4294967300,"hasActionTypeCard":0,"darwin_tags":[],"hot_weibo_tags":[],"text_tag_tips":[],"userType":0,"positive_recom_flag":0,"gif_ids":"","is_show_bulletin":2},"ptype":0,"allow_all_comment":true,"avatar_large":"http://tva3.sinaimg.cn/default/images/default_avatar_female_180.gif","avatar_hd":"http://tva3.sinaimg.cn/default/images/default_avatar_female_180.gif","verified_reason":"","verified_trade":"","verified_reason_url":"","verified_source":"","verified_source_url":"","follow_me":false,"online_status":0,"bi_followers_count":0,"lang":"zh-cn","star":0,"mbtype":0,"mbrank":0,"block_word":0,"block_app":0,"credit_score":80,"user_ability":0,"urank":4}
			WeiboValidateAndGetUserRespDTO temp = gson.fromJson(result, WeiboValidateAndGetUserRespDTO.class);

			if (temp == null || temp.getIdstr() == null || temp.getIdstr().equals(openId) == false) {
				throw new PassportException(PassportException.EXCEPTION_OPEN_ID_INVALID, null);
			}

			if (StringUtils.isEmpty(temp.getScreen_name())) {
				temp.setScreen_name("nickName");
			}

			for (String defaultUrl : DEFAULT_AVATAR_URL_LIST) {
				if (temp.getProfile_image_url() != null && temp.getProfile_image_url().contains(defaultUrl)) {
					String newHeadIconUrl = PlaceholderAvatarUtil
							.getPlaceholderAvatarByMod(Long.valueOf(temp.getIdstr()));
					temp.setProfile_image_url(newHeadIconUrl);
					break;
				}
			}

			ThirdAccountSDKPortrait dto = new ThirdAccountSDKPortrait();
			dto.setAvatar(temp.getProfile_image_url());
			dto.setNickname(temp.getScreen_name());
			dto.setGender(Gender.get(WeiboSexEnum.valueOf(temp.getGender()).value()));
			dto.setUsername(temp.getScreen_name());
			dto.setUserType(UserType.valueOf(UserType.weibo.category()));

			return dto;
		} catch (PassportException pae) {
			throw pae;
		} catch (Exception e) {
			throw new PassportException(PassportException.EXCEPTION_OBTAIN_OPEN_ID_FAILED, e);
		}
	}

	class WeiboValidateAndGetUserReqDTO {

		private String access_token;

		private long uid;

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public long getUid() {
			return uid;
		}

		public void setUid(long uid) {
			this.uid = uid;
		}

	}

	class WeiboValidateAndGetUserRespDTO {

		private String idstr;

		private String profile_image_url;

		private String screen_name;

		private String gender;

		public String getProfile_image_url() {
			return profile_image_url;
		}

		public void setProfile_image_url(String profile_image_url) {
			this.profile_image_url = profile_image_url;
		}

		public String getScreen_name() {
			return screen_name;
		}

		public void setScreen_name(String screen_name) {
			this.screen_name = screen_name;
		}

		public String getGender() {
			return gender;
		}

		public void setGender(String gender) {
			this.gender = gender;
		}

		public String getIdstr() {
			return idstr;
		}

		public void setIdstr(String idstr) {
			this.idstr = idstr;
		}

	}

	static enum WeiboSexEnum {
		u(Gender.unknown.gender()), m(Gender.male.gender()), f(Gender.female.gender());

		private int value = 0;

		private WeiboSexEnum(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}

	}
}
