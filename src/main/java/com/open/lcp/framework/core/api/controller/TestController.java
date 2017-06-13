package com.open.lcp.framework.core.api.controller;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.open.common.util.HttpUtil;
import com.open.common.util.JsonFormatUtil;
import com.open.lcp.framework.util.LcpUtils;

/**
 * 测试用
 * 
 * @author
 *
 */
@Controller
@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
public class TestController extends AbstractController {
	private static final Log logger = LogFactory.getLog(TestController.class);
	@Autowired
	private CloseableHttpClient client;
	// private static final String[] allowdIps = { "127.0.0.1",
	// "0:0:0:0:0:0:0:1", "192.168.", "106.39.75.",
	// "114.255.247.", "113.208.115.74", "202.104.136.196", "219.133.170.82" };
	private static final String[] allowdIps = { "127.0.0.1", "123.57.204.187", "0:0:0:0:0:0:0:1" };

	private static boolean isIpAllowed(String fromIp) {
		for (String ip : allowdIps) {
			if (fromIp.startsWith(ip)) {
				return true;
			}
		}
		return false;
	}

	@RequestMapping("/test")
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String result = null;
		try {
			final String ip = request.getRemoteAddr();
			if (!isIpAllowed(ip)) {
				result = "access denied! " + ip;
				return null;
			}
			final int appId = NumberUtils.toInt(request.getParameter("_i"));// appId
			if (appId < 1) {
				result = buildResp(0, "", "", "", "请用_i指定appId参数。");
				return null;
			}
			String secretKey = request.getParameter("_s");// appId对应的secretkey
			if (secretKey == null || secretKey.isEmpty()) {
				result = buildResp(0, "", "", "", "请用_s指定appId对应的secretKey参数。");
				return null;
			}
			if ("GET".equalsIgnoreCase(request.getMethod())) {// 返回基本页面
				result = buildResp(0, "", "", "", "");
				return null;
			}
			// 请求并得到结果
			final String userSecretKey = request.getParameter("_u");// 用户t票对应的secretKey
			if (userSecretKey != null && userSecretKey.length() > 0) {
				secretKey = secretKey + userSecretKey;
			}
			final String parames = request.getParameter("_p");// 参数列表，每行一个，=号分隔
			final int envId = NumberUtils.toInt(request.getParameter("_e"));// 环境编号
			if (envId < 0 || envId >= envs.length) {
				result = buildResp(0, "", "", "", "请不要伪造环境参数。");
				return null;
			}
			final String method = request.getParameter("_m");// 接口名称
			if (method == null || method.isEmpty()) {
				result = buildResp(envId, method, parames, "请指定要测试的接口名称。", "");
				return null;
			}
			// 取请求参数，增加默认参数
			final Map<String, String> reqMap = new HashMap<String, String>();
			{
				reqMap.put("appId", String.valueOf(appId));
				reqMap.put("v", "1.0");
				if (parames != null && parames.length() > 0) {
					String[] ps = parames.split("[\r\n]");
					for (String p : ps) {
						if (p.isEmpty())
							continue;
						String[] kv = p.split("[=]", 2);
						if (kv.length != 2)
							continue;
						if (kv[0].equals("appId")) {
							result = buildResp(envId, method, parames, "AppId已经在测试平台URL中指定，请不要重复提供。", "");
							return null;
						}
						if (kv[0].equals("sig")) {
							result = buildResp(envId, method, parames, "本测试平台会自动计算sig值，无需提供此参数。", "");
							return null;
						}
						if (kv[0].equals("gz")) {
							continue;
						}
						reqMap.put(kv[0], kv[1]);
					}
				}
			}
			// 算sig值
			String normalizedString = LcpUtils.generateNormalizedString(null, reqMap);
			String sig = LcpUtils.generateSignature(normalizedString, secretKey);
			reqMap.put("sig", sig);
			final String body = HttpUtil.getPostBodyFromMap(reqMap);
			final String url = ((envs[envId].startsWith(ENV_TEST) && IP_LOCAL.startsWith("192.168.")) ? ENV_TEST_REAL
					: envs[envId]) + method.replaceAll("[.]", "/");
			try {
				String json = HttpUtil.postForm(client, url, body);
				if (json.startsWith("<")) {
					json = json.replaceAll("<", "&lt;");
					json = json.replaceAll(">", "&gt;");
				}
				logger.info(String.format("test %s,%s,%s,%s,%s,%s", request.getRemoteAddr(), appId, envId, method,
						parames, json));
				result = buildResp(envId, method, parames, json, "");
			} catch (Exception e) {
				result = buildResp(envId, method, parames, e.getMessage(), "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (result == null) {
				return null;
			}
			response.setContentType("text/html");
			// 禁用页面缓存
			response.setHeader("Pragma", "no-util");
			response.addHeader("Cache-Control", "must-revalidate");
			response.addHeader("Cache-Control", "no-util");
			response.addHeader("Cache-Control", "no-store");
			response.addHeader("Content-Type", "text/html; charset=UTF-8");
			response.setDateHeader("Expires", 0);

			try (OutputStream os = response.getOutputStream()) {
				byte[] ob = result == null ? null : result.getBytes(CharEncoding.UTF_8);
				os.write(ob);
				os.flush();
			}
		}
		return null;
	}

	private static final String ENV_TEST = "https://test.api.bchbc.com/api/";
	private static final String ENV_TEST_REAL = "http://192.168.226.123/api/";
	// private static final String IP_LOCAL =
	// EnvFinderUtil.getIpcfg().getLocalIp();
	private static final String IP_LOCAL = "192.168.";
	private static final String[] envs = { ""// 0忽略
			// , "http://test.api.xlmc.sandai.net/api/"// 新测试环境
			// , "http://pre.api.tw06.xlmc.sandai.net/api/"// 定版
			// , "http://api.tw06.xlmc.sandai.net/api/"// 线上
			// , "http://localhost:9001/api/"// 本机
			// , "http://localhost:9004/api/"// 本机
			// , "http://pre.api.xlmc.sandai.net/api/"// 定版
			// , "http://api.xlmc.sandai.net/api/"// 线上
			// , "http://api.ra2.xlmc.sec.miui.com/api/"// 红二老域名
			// , "http://api.xlmc.sec.miui.com/api/"//
			// miui新域名http://test.mcp.bchbc.com/api
			// , ENV_TEST// 测试
			// , "http://t16b61.sandai.net/api/"// 临时测试环境
			// , "http://localhost:9002/api/"// 本机测试
			, "http://localhost:8080/api/"// 本机
	};

	private static String buildResp(int envId, String methodName, String ps, String resp, String error) {
		final StringBuilder sb = new StringBuilder();
		sb.append(
				"<html><head><meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\"/><title>XLMCP测试平台</title></head><body>");
		sb.append("<form method=\"POST\"><table style=\"border-spacing:0px;\">");
		boolean isError = false;
		if (error != null && error.length() > 0) {
			isError = true;
			sb.append(String.format("<caption><font color='red'><b><br/>%s</b></font></caption>", error));
		}
		sb.append("<tr><td colspan=\"2\">");
		sb.append("<b>目标环境：</b><select name=\"_e\">");
		for (int i = 1; i < envs.length; i++) {
			if (i == envId) {
				sb.append(String.format("<option value=\"%s\" selected=\"selected\">%s</option>", i, envs[i]));
			} else {
				sb.append(String.format("<option value=\"%s\">%s</option>", i, envs[i]));
			}
		}
		sb.append("</select><br/><br/>");
		sb.append(String.format("<b>接口名称：</b><input name=\"_m\" value=\"%s\" /><br/>", methodName));
		sb.append(
				"</td></tr><tr><td width=\"700px\" style=\"border-right-width:1px;border-right-style:solid;\"><b>请求参数：</b>每行一个参数，格式为：key=value</td><td><b>返回结果</b><hr/></td></tr><tr>");
		sb.append("<td valign=\"top\" style=\"border-right-width:1px;border-right-style:solid;\">");
		sb.append(String.format(
				"<textarea rows=\"20\" cols=\"80\" name=\"_p\" style=\"word-wrap:normal;\">%s</textarea><br/>", ps));
		if (isError) {
			sb.append("<font color='red'><b>请处理顶部提示的错误。</b></font>");//
		} else {
			sb.append("<input type=\"submit\" name=\"_skip\" value=\"提交\">");//
		}
		sb.append("</td><td valign=\"top\"><pre>");//
		if (resp != null && resp.length() > 0) {
			sb.append(JsonFormatUtil.formatJson(resp, "  "));
		}
		sb.append("</pre></td></tr></table></form></body></html>");
		return sb.toString();
	}

}
