package com.open.lcp.biz.passport.sdk.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
//import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.open.lcp.biz.passport.PassportException;
import com.open.lcp.biz.passport.UserAccountType;
import com.open.lcp.biz.passport.sdk.ThirdAccountSDKPortrait;
import com.open.lcp.biz.passport.util.PlaceholderAvatarUtil;
import com.open.lcp.common.enums.Gender;
import com.open.lcp.common.enums.UserType;

@Component("xiaomiThirdAccountSDK")
public class XiaomiThirdAccountSDK extends AbstractThirdAccountSDK {

	@Autowired
	protected CloseableHttpClient httpClient;

	// private static final Log logger = LogFactory.getLog(XiaomiUserSDK.class);

	// @Override
	// public String obtainOpenId(String oauthAppId, String accessToken) {
	// List<Header> headers = new ArrayList<Header>();
	//
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("clientId", String
	// .valueOf(oauthAppId)));
	// params.add(new BasicNameValuePair("token", accessToken));
	//
	// String macKey = "accessToken.macKey";
	//
	// String nonce = XMUtil.generateNonce();
	// String qs = URLEncodedUtils.format(params, "UTF-8");
	// String apiHost = "open.account.xiaomi.com";
	// String apiPath = "/user/openidV2";
	//
	// try {
	// String mac = XMUtil.getMacAccessTokenSignatureString(nonce, "GET",
	// apiHost, apiPath, qs, macKey, "HmacSHA1");
	// Header macHeader = XMUtil.buildMacRequestHead(accessToken, nonce,
	// mac);
	//
	// headers.add(macHeader);
	//
	// XMApiHttpClient client = new XMApiHttpClient(
	// Long.valueOf(oauthAppId), accessToken, new XMHttpClient());
	// JSONObject json = client.apiCall(apiPath, params, headers, "GET");
	//
	// JSONObject profile = (json != null && json.has("result") && json
	// .getString("result").equalsIgnoreCase("ok")) ? json
	// .getJSONObject("data") : null;
	// // TODO
	// return null;
	// } catch (PassportApiException pae) {
	// throw pae;
	// } catch (Exception e) {
	// logger.warn(e.getMessage(), e);
	// throw new PassportApiException(
	// PassportApiException.EXCEPTION_OBTAIN_OPEN_ID_FAILED);
	// }
	// }

	@Override
	public ThirdAccountSDKPortrait validateAndObtainUserPortrait(String oauthAppId, String openId, String accessToken)
			throws PassportException {

		// obtainOpenId(oauthAppId, accessToken);

		// List<Header> headers = new ArrayList<Header>();
		//
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		// params.add(new BasicNameValuePair("clientId",
		// String.valueOf(oauthAppId)));
		// params.add(new BasicNameValuePair("token", accessToken));
		//
		// String macKey = "accessToken.macKey";
		//
		// String nonce = XMUtil.generateNonce();
		// String qs = URLEncodedUtils.format(params, "UTF-8");
		// String apiHost = "open.account.xiaomi.com";
		// String apiPath = "/user/profile";
		//
		// try {
		// String mac = XMUtil.getMacAccessTokenSignatureString(nonce, "GET",
		// apiHost, apiPath, qs, macKey,
		// "HmacSHA1");
		// Header macHeader = XMUtil.buildMacRequestHead(accessToken, nonce,
		// mac);
		//
		// headers.add(macHeader);
		//
		// XMApiHttpClient client = new
		// XMApiHttpClient(Long.valueOf(oauthAppId), accessToken, new
		// XMHttpClient());
		// JSONObject json = client.apiCall(apiPath, params, headers, "GET");
		//
		// JSONObject profile = (json != null && json.has("result") &&
		// json.getString("result").equalsIgnoreCase("ok"))
		// ? json.getJSONObject("data") : null;
		//
		// if (profile != null) {
		// ThirdAccountSDKPortrait dto = new ThirdAccountSDKPortrait();
		// String headIconUrl = profile.has("miliaoIcon_120") ?
		// profile.getString("miliaoIcon_120") : null;
		//
		// if (headIconUrl != null && headIconUrl.equals(
		// "https://account.xiaomi.com/static/res/7c3e9b0/passport/acc-2014/img/n-avator-bg.png"))
		// {
		// headIconUrl =
		// PlaceholderAvatarUtil.getPlaceholderAvatarByMod(profile.getLong("userId"));
		// }
		//
		// String nickName = profile.has("miliaoNick") ?
		// profile.getString("miliaoNick") : null;
		// String userId = profile.has("userId") ? profile.getString("userId") :
		// null;
		// if (openId == null || openId.equals(userId) == false) {
		// throw new
		// PassportException(PassportException.EXCEPTION_OPEN_ID_INVALID, null);
		// }
		// dto.setAvatar(headIconUrl);
		// dto.setNickname(nickName);
		// dto.setGender(Gender.unknown); // no this item data.
		//
		// dto.setUsername(userId);
		// dto.setUserType(UserType.valueOf(UserAccountType.xiaomi.category()));
		// return dto;
		// } else {
		// throw new
		// PassportException(PassportException.EXCEPTION_OBTAIN_PORTRAIT_FAILED,
		// "EXCEPTION_OBTAIN_PORTRAIT_FAILED", null);
		// }
		// } catch (PassportException pae) {
		// throw pae;
		// } catch (Exception e) {
		// throw new
		// PassportException(PassportException.EXCEPTION_OBTAIN_OPEN_ID_FAILED,
		// "EXCEPTION_OBTAIN_OPEN_ID_FAILED", e);
		// }
		return null;
	}

}
