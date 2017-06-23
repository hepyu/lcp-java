package com.open.lcp.passport.sdk.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.open.common.enums.Gender;
import com.open.common.enums.UserType;
import com.open.common.util.HttpUtil;
import com.open.lcp.passport.PassportException;
import com.open.lcp.passport.UserAccountType;
import com.open.lcp.passport.sdk.ThirdAccountSDKPortrait;

@Component("qqThirdAccountSDK")
public class QQThirdAccountSDK extends AbstractThirdAccountSDK {

	private static final Log logger = LogFactory.getLog(QQThirdAccountSDK.class);

	@Autowired
	protected CloseableHttpClient httpClient;

	@Override
	public ThirdAccountSDKPortrait validateAndObtainUserPortrait(String oauthAppId, String openId, String accessToken,
			String bisType) throws PassportException {

		try {

			// https://graph.qq.com/user/get_user_info?access_token=50E5196F34B81CD059A5A8DD2929E067&oauth_consumer_key=1101020666&openid=3AAD71F3FAC2F983B2322BB12B0DF96F&format=json
			String url = "https://graph.qq.com/user/get_user_info?access_token=" + accessToken + "&oauth_consumer_key="
					+ oauthAppId + "&openid=" + openId + "&format=json";
			// OpenApiV3 qqApi = new OpenApiV3(oauthAppId, "8d8LNAQ4cvGfVFmR");
			// qqApi.setServerName("openapi.tencentyun.com");
			//
			// // 指定OpenApi Cgi名字
			// String scriptName = "/v3/user/get_info";
			//
			// // 指定HTTP请求协议类型
			// String protocol = "http";
			//
			// // 填充URL请求参数
			// HashMap<String, String> params = new HashMap<String, String>();
			// params.put("openid", openId);
			// params.put("openkey", accessToken);
			// params.put("appid", oauthAppId);
			// params.put("pf", "qq");

			try {
				// String resp = qqApi.api(scriptName, params, protocol);

				String resp = HttpUtil.get(httpClient, url);

				Gson gson = new Gson();
				QQValidateAndGetUserRespDTO temp = gson.fromJson(resp, QQValidateAndGetUserRespDTO.class);

				if (!temp.getRet().equals("0")) {
					return null;
				}

				// if (temp == null || temp.getOpenid() == null
				// || temp.getOpenid().equals(openId) == false) {
				// throw new PassportApiException(
				// PassportApiException.EXCEPTION_OPEN_ID_INVALID,
				// null);
				// }

				if (StringUtils.isEmpty(temp.getNickname())) {
					temp.setNickname("nickName");
				}

				ThirdAccountSDKPortrait dto = new ThirdAccountSDKPortrait();
				dto.setAvatar(temp.getFigureurl_qq_1());
				dto.setNickname(temp.getNickname());
				dto.setUsername(temp.getNickname());
				dto.setUserType(UserType.valueOf(UserAccountType.qq.category()));

				if ("男".equals(temp.getGender())) {
					dto.setGender(Gender.male);
				} else if ("女".equals(temp.getGender())) {
					dto.setGender(Gender.female);
				} else {
					dto.setGender(Gender.unknown);
				}

				return dto;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return null;
			}
		} catch (Exception e) {
			throw new PassportException(PassportException.EXCEPTION_OBTAIN_OPEN_ID_FAILED, e);
		}

	}

	// { "ret": 0, "msg": "", "is_lost":0, "nickname": "周可-亚历克斯山大阿姆�?",
	// "gender":
	// "�?", "province": "北京", "city": "东城", "year": "1997", "figureurl":
	// "http:\/\/qzapp.qlogo.cn\/qzapp\/1101020666\/3AAD71F3FAC2F983B2322BB12B0DF96F\/30",
	// "figureurl_1":
	// "http:\/\/qzapp.qlogo.cn\/qzapp\/1101020666\/3AAD71F3FAC2F983B2322BB12B0DF96F\/50",
	// "figureurl_2":
	// "http:\/\/qzapp.qlogo.cn\/qzapp\/1101020666\/3AAD71F3FAC2F983B2322BB12B0DF96F\/100",
	// "figureurl_qq_1":
	// "http:\/\/q.qlogo.cn\/qqapp\/1101020666\/3AAD71F3FAC2F983B2322BB12B0DF96F\/40",
	// "figureurl_qq_2":
	// "http:\/\/q.qlogo.cn\/qqapp\/1101020666\/3AAD71F3FAC2F983B2322BB12B0DF96F\/100",
	// "is_yellow_vip": "0", "vip": "0", "yellow_vip_level": "0", "level": "0",
	// "is_yellow_year_vip": "0" }
	class QQValidateAndGetUserRespDTO {

		private String ret = "-1";

		private String nickname;

		private String gender;

		private String figureurl_qq_1;

		public String getRet() {
			return ret;
		}

		public void setRet(String ret) {
			this.ret = ret;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public String getGender() {
			return gender;
		}

		public void setGender(String gender) {
			this.gender = gender;
		}

		public String getFigureurl_qq_1() {
			return figureurl_qq_1;
		}

		public void setFigureurl_qq_1(String figureurl_qq_1) {
			this.figureurl_qq_1 = figureurl_qq_1;
		}

	}
}
