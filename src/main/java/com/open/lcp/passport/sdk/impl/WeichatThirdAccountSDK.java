package com.open.lcp.passport.sdk.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.open.common.enums.Gender;
import com.open.common.enums.UserType;
import com.open.lcp.passport.PassportException;
import com.open.lcp.passport.UserAccountType;
import com.open.lcp.passport.sdk.ThirdAccountSDKPortrait;

@Component("weichatThirdAccountSDK")
public class WeichatThirdAccountSDK extends AbstractThirdAccountSDK {

	private static final Log logger = LogFactory.getLog(WeichatThirdAccountSDK.class);

	@Autowired
	protected CloseableHttpClient httpClient;

	@Override
	public ThirdAccountSDKPortrait validateAndObtainUserPortrait(String appId, String openId, String accessToken,
			String type) throws PassportException {

		StringBuilder sb = new StringBuilder();
		BufferedReader in = null;
		try {
			String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId;
			URL urlObject = new URL(url);
			URLConnection uc = urlObject.openConnection();
			in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}

			Gson gson = new Gson();
			WeichatUserPortraitDTO temp = gson.fromJson(sb.toString(), WeichatUserPortraitDTO.class);

			if (temp == null || temp.getOpenid() == null || temp.getOpenid().equals(openId) == false) {
				throw new PassportException(PassportException.EXCEPTION_OPEN_ID_INVALID, null);
			}

			if (StringUtils.isEmpty(temp.getNickname())) {
				temp.setNickname("nickName");
			}

			ThirdAccountSDKPortrait dto = new ThirdAccountSDKPortrait();
			dto.setAvatar(temp.getHeadimgurl());
			dto.setNickname(temp.getNickname());
			dto.setGender(Gender.get(temp.getSex()));
			dto.setUsername(temp.getNickname());
			dto.setUserType(UserType.valueOf(UserAccountType.weichat.category()));

			return dto;
		} catch (PassportException pae) {
			throw pae;
		} catch (Exception e) {
			throw new PassportException(PassportException.EXCEPTION_OBTAIN_OPEN_ID_FAILED, e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}

	}

	// {"openid":"oixX-swihHML04UaHN37uvwfJF14",
	// "nickname":"work001",
	// "sex":0,
	// "language":"zh_CN",
	// "city":"",
	// "province":"",
	// "country":"CN",
	// "headimgurl":"",
	// "privilege":[],
	// "unionid":"oF-WjxN7jrwSAi3skFAS3hBTs-Bc"}
	class WeichatUserPortraitDTO {

		private String openid;
		private String nickname;
		private int sex;
		private String language;
		private String city;
		private String province;
		private String country;
		private String headimgurl;
		private String[] privilege;
		private String unionid;

		public String getOpenid() {
			return openid;
		}

		public void setOpenid(String openid) {
			this.openid = openid;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public int getSex() {
			return sex;
		}

		public void setSex(int sex) {
			this.sex = sex;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getProvince() {
			return province;
		}

		public void setProvince(String province) {
			this.province = province;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getHeadimgurl() {
			return headimgurl;
		}

		public void setHeadimgurl(String headimgurl) {
			this.headimgurl = headimgurl;
		}

		public String[] getPrivilege() {
			return privilege;
		}

		public void setPrivilege(String[] privilege) {
			this.privilege = privilege;
		}

		public String getUnionid() {
			return unionid;
		}

		public void setUnionid(String unionid) {
			this.unionid = unionid;
		}

	}
}
