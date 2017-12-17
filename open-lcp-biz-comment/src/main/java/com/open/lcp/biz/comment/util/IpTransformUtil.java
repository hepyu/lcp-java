package com.open.lcp.biz.comment.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.open.lcp.core.common.util.GsonUtil;
import com.open.lcp.core.common.util.HttpUtil;
import com.open.lcp.core.framework.api.ApiException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class IpTransformUtil {

	private static final String IP_SERVER_SINA = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js&ip=%s";

	private static final String LEFT_BRACE = "{";

	private static final String RIGHT_BRACE = "}";

	private static final String EQUAL = "=";

	@Autowired
	private CloseableHttpClient httpClient;

	public String postIpServerSina(String ip) {
		String url = String.format(IP_SERVER_SINA, ip);
		String response = HttpUtil.get(httpClient, url);
		AddrOfSina addrOfSina = responseToAddr(response);

		return null;
	}

	// @Test
	public void addrTest() {
		this.responseToAddr(
				"var remote_ip_info = {\"ret\":1,\"start\":-1,\"end\":-1,\"country\":\"\\u4e2d\\u56fd\",\"province\":\"\\u5317\\u4eac\",\"city\":\"\\u5317\\u4eac\",\"district\":\"\",\"isp\":\"\",\"type\":\"\",\"desc\":\"\"};");
	}

	private AddrOfSina responseToAddr(String response) {
		if (response.contains(LEFT_BRACE) && response.contains(RIGHT_BRACE)) {
			ArrayList<String> splitted = Lists
					.newArrayList(Splitter.on(EQUAL).trimResults().omitEmptyStrings().split(response));
			if (splitted.size() == 2) {
				String jsonAddr = splitted.get(1);
				jsonAddr = jsonAddr.substring(0, jsonAddr.length() - 1);
				AddrOfSina addrOfSina = GsonUtil.gson.fromJson(jsonAddr, AddrOfSina.class);
				System.out.println(addrOfSina);
			}
		} else {
			throw new ApiException(1, "cant find sina ip addr");
		}
		return null;
	}

	class AddrOfSina {

		private int ret;

		private int start;

		private int end;

		private String country;

		private String province;

		private String city;

		private String district;

		private String isp;

		private String type;

		private String desc;

		public int getRet() {
			return ret;
		}

		public void setRet(int ret) {
			this.ret = ret;
		}

		public int getStart() {
			return start;
		}

		public void setStart(int start) {
			this.start = start;
		}

		public int getEnd() {
			return end;
		}

		public void setEnd(int end) {
			this.end = end;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getProvince() {
			return province;
		}

		public void setProvince(String province) {
			this.province = province;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getDistrict() {
			return district;
		}

		public void setDistrict(String district) {
			this.district = district;
		}

		public String getIsp() {
			return isp;
		}

		public void setIsp(String isp) {
			this.isp = isp;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}
}
