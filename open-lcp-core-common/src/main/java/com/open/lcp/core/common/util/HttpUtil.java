package com.open.lcp.core.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpUtil {

	private static final int CONN_TIMEOUT = 10 * 1000;

	private static ObjectMapper objectMapper = new ObjectMapper();

	private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	public static class HttpRequestException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		private String url;
		private int statusCode;

		public HttpRequestException(String url, int statusCode, String message) {
			super(message);
			this.statusCode = statusCode;
			this.url = url;
		}

		public String getUrl() {
			return url;
		}

		public int getStatusCode() {
			return statusCode;
		}

	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getResultAsMap(CloseableHttpClient client, String url, NameValuePair... nvps) {
		String result = get(client, url, nvps);
		if (StringUtils.isNotEmpty(result)) {
			try {
				return objectMapper.readValue(result, Map.class);
			} catch (IOException e) {
				logger.error(url);
				logger.error("", e);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> postResultAsMap(CloseableHttpClient client, String url, String body) {
		String result = post(client, url, body);
		if (StringUtils.isNotEmpty(result)) {
			try {
				return objectMapper.readValue(result, Map.class);
			} catch (IOException e) {
				logger.error(url);
				logger.error("", e);
			}
		}
		return null;
	}

	public static String get(CloseableHttpClient client, String url, NameValuePair... nvps) {
		return httpMethod(HttpMethod.GET, client, url, null, null, nvps);
	}

	public static String post(CloseableHttpClient client, String url, String body) {
		return httpMethod(HttpMethod.POST, client, url, body, null);
	}
	
	public static String post(CloseableHttpClient client, String url, String body, Map<String,String> cookie) {

		return httpMethod(HttpMethod.POST, client, url, body, cookie);
	}

	private static String httpMethod(HttpMethod method, CloseableHttpClient client, String url, String body, Map<String,String> cookie, NameValuePair... nvps) {
		RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(false).setConnectionRequestTimeout(CONN_TIMEOUT).setConnectTimeout(CONN_TIMEOUT)
				.setSocketTimeout(CONN_TIMEOUT).build();
		RequestBuilder builder = null;
		switch (method) {
		case GET:
			builder = RequestBuilder.get();
			break;
		case POST:
			builder = RequestBuilder.post();
			break;
		default:
			throw new RuntimeException("method is not supported");
		}
		builder = builder.setConfig(requestConfig).addHeader("User-Agent", "Mozilla/5.0").setUri(url);
		if (StringUtils.isNotEmpty(body))
			builder = builder.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
		if (nvps != null && nvps.length > 0)
			builder.addParameters(nvps);
		if(cookie != null && cookie.size() > 0){
			String cookieParams = "";
			for (Map.Entry<String, String> e: cookie.entrySet()) {
				cookieParams = e.getKey() + "=" + e.getValue() + ";";
			}
			builder.setHeader("cookie", cookieParams);
		}

		HttpUriRequest request = builder.build();
		int statusCode = 0;
		String reason = null;
		CloseableHttpResponse httpResponse = null;
		try {
			logger.debug(url);
			httpResponse = client.execute(request);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			reason = httpResponse.getStatusLine().getReasonPhrase();
			logger.debug(String.valueOf(statusCode));
			if (statusCode >= 200 && statusCode < 300) {
				HttpEntity entity = httpResponse.getEntity();
				return entity != null ? EntityUtils.toString(entity) : null;
			}
		} catch (Exception e) {
			logger.error(url);
			logger.error("error", e);
			throw new HttpRequestException(url, statusCode, reason);
		} finally {
			try {
				if (httpResponse != null)
					httpResponse.close();
			} catch (IOException e) {
			}
		}
		logger.error(url);
		logger.error(String.valueOf(statusCode));
		logger.error(reason);
		throw new HttpRequestException(url, statusCode, reason);
	}

	public static String getPostBodyFromMap(Map<String, String> map) {
		if (map == null || map.isEmpty())
			return null;
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> e : map.entrySet()) {
			if (sb.length() > 0) {
				sb.append("&");
			}
			sb.append(e.getKey());
			sb.append("=");
			try {
				sb.append(URLEncoder.encode(e.getValue(), "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static String postForm(CloseableHttpClient client, String url, String body, NameValuePair... nvps) {
		RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(false).setConnectionRequestTimeout(CONN_TIMEOUT).setConnectTimeout(CONN_TIMEOUT)
				.setSocketTimeout(CONN_TIMEOUT).build();
		RequestBuilder builder = RequestBuilder.post();
		builder = builder.setConfig(requestConfig).addHeader("User-Agent", "Mozilla/5.0").setUri(url);
		if (StringUtils.isNotEmpty(body))
			builder = builder.setEntity(new StringEntity(body, ContentType.APPLICATION_FORM_URLENCODED));
		if (nvps != null && nvps.length > 0)
			builder.addParameters(nvps);
		HttpUriRequest request = builder.build();
		int statusCode = 0;
		String reason = null;
		CloseableHttpResponse httpResponse = null;
		try {
			logger.debug(url);
			httpResponse = client.execute(request);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			reason = httpResponse.getStatusLine().getReasonPhrase();
			logger.debug(String.valueOf(statusCode));
			if (statusCode >= 200 && statusCode < 300) {
				HttpEntity entity = httpResponse.getEntity();
				return entity != null ? EntityUtils.toString(entity) : null;
			}
		} catch (Exception e) {
			logger.error(url);
			logger.error("error", e);
			throw new HttpRequestException(url, statusCode, reason);
		} finally {
			try {
				if (httpResponse != null)
					httpResponse.close();
			} catch (IOException e) {
			}
		}
		throw new HttpRequestException(url, statusCode, reason);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> postFormWithMapResult(
			CloseableHttpClient httpClient, String url, String body) {
		String result = postForm(httpClient, url, body);//String result = postForm(httpClient, url, body, null);
		if (StringUtils.isNotEmpty(result)) {
			try {
				return objectMapper.readValue(result, Map.class);
			} catch (IOException e) {
				logger.error(url);
				logger.error("Post Error", e);
			}
		}
		return null;
	}
	
	
	public static BufferedReader getReader(CloseableHttpClient client, String url, NameValuePair... nvps) {
		
		RequestConfig requestConfig = RequestConfig.custom()
			      .setConnectionRequestTimeout(CONN_TIMEOUT).setConnectTimeout(CONN_TIMEOUT).setSocketTimeout(CONN_TIMEOUT).build();
		
		RequestBuilder builder = RequestBuilder.get()
				.setConfig(requestConfig)
				.addHeader("User-Agent", "Mozilla/5.0")
				.addHeader("Accept-Encoding", "gzip")
				.setUri(url);
		
		if(nvps != null)
			builder.addParameters(nvps);
		
		HttpUriRequest request = builder.build();
	
		int statusCode = 0;
		String reason = null;
		
		CloseableHttpResponse httpResponse = null;
		
		try {
			
			logger.debug(url);
			
			httpResponse = client.execute(request);
			
			statusCode = httpResponse.getStatusLine().getStatusCode();
			
			reason = httpResponse.getStatusLine().getReasonPhrase();
			
			if(statusCode >= 200 && statusCode < 300) {
				HttpEntity entity = httpResponse.getEntity();
				return new BufferedReader(new InputStreamReader(entity.getContent()));
			
			}
			
		} 
		catch (Exception e) {
			throw new HttpRequestException(url, statusCode,reason);			
		} 
		finally {			
			
//			try {
//				if (httpResponse != null)
//					httpResponse.close();
//			} catch (IOException e) {
//			}
		}
		
		
		logger.debug("invalid status code {} {}",  statusCode, reason);
		logger.debug(url);

		throw new HttpRequestException(url, statusCode,reason);
	}
}
