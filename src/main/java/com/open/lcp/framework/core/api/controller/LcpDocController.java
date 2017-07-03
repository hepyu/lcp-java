package com.open.lcp.framework.core.api.controller;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.open.common.util.DateUtil;
import com.open.lcp.framework.core.annotation.LcpMethod;
import com.open.lcp.framework.core.annotation.LcpReq;
import com.open.lcp.framework.core.annotation.LcpRequired;
import com.open.lcp.framework.core.api.LcpFieldLoadHolder;
import com.open.lcp.framework.core.api.LcpModelDefLanHolder;
import com.open.lcp.framework.core.api.NZCode;
import com.open.lcp.framework.core.api.NZCodeHolder;
import com.open.lcp.framework.core.api.command.ApiCommand;
import com.open.lcp.framework.core.api.command.ApiFacadeCommand;
import com.open.lcp.framework.core.api.command.ApiFacadeMethod;
import com.open.lcp.framework.core.api.command.CommandModelHolder;
import com.open.lcp.framework.core.api.service.LcpApiCommandLookupService;

@Controller
@RequestMapping(method = RequestMethod.GET)
public class LcpDocController extends AbstractController implements InitializingBean {

	private static final Log logger = LogFactory.getLog(LcpDocController.class);

	private static final List<String> oldMethods = new ArrayList<String>();

	private static final Map<Integer, Set<String>> errorCodeMap = new HashMap<Integer, Set<String>>();

	@Autowired
	private LcpApiCommandLookupService commandLookupService;

	private String[] methods;

	private static final String env_node = "env_node";
	private static final String env_host = "env_host";
	// private static final String env_node = EnvFinderUtil.getProfile();
	// private static final String env_host =
	// EnvFinderUtil.getIpcfg().getLocalIp();

	private static final String env_time = DateUtil.dateToStr(Calendar.getInstance().getTime(), DateUtil.DATE_TIME_SS);

	private static final String env_builder;
	private static final String env_buildtime;
	static {
		String builder = "";
		String buildtime = "";
		try {
			final ResourceBundle env = ResourceBundle.getBundle("env");
			builder = env.getString("depuser");
			if (builder == null) {
				builder = "";
			}
			buildtime = env.getString("deptime");
			if (buildtime == null) {
				buildtime = "";
			}
		} catch (Exception e) {
		}
		env_builder = builder;
		env_buildtime = buildtime;
	}

	@Override
	protected void initServletContext(ServletContext servletContext) {
		Set<SessionTrackingMode> estms = servletContext.getEffectiveSessionTrackingModes();
		estms.clear();
		super.initServletContext(servletContext);

		Map<String, List<String>> cmdVers = commandLookupService.getCommands();
		methods = cmdVers.keySet().toArray(new String[0]);
		Arrays.sort(methods);
		for (String command : methods) {
			for (String v : cmdVers.get(command)) {
				ApiCommand apiCommand = commandLookupService.lookupApiCommand(command, v);
				if (apiCommand instanceof ApiFacadeCommand) {
					final ApiFacadeMethod mm = CommandModelHolder.getApiFacadeMethod(command, v);
					if (mm != null) {
						final LcpReq lcpReq = mm.getLcpReq();
						if (lcpReq != null && lcpReq.errorCode() > 0) {
							this.addErrorCode(command, lcpReq, null, null);
						}
						if (mm.getReqClass() != null && mm.getReqClass() != Void.class) {
							final List<Field> fieldList = LcpFieldLoadHolder.getFields(mm.getReqClass());
							for (Field f : fieldList) {
								final LcpRequired required = f.getAnnotation(LcpRequired.class);
								if (required != null && required.errorCode() > 0) {
									this.addErrorCode(command, null, f.getName(), required);
								}
							}
						}
					}
				} else {
					oldMethods.add(command);
				}
			}
		}
		Collections.sort(oldMethods);
	}

	static {
		final ResourceBundle rsMessage = ResourceBundle.getBundle("api_result_code_messages");
		final ResourceBundle rsCause = ResourceBundle.getBundle("api_result_code_cause");
		Enumeration<String> keys = rsMessage.getKeys();
		final String KEY_PRE = "api.result.msg.";
		while (keys.hasMoreElements()) {
			final String key = keys.nextElement();
			if (!key.startsWith(KEY_PRE)) {
				continue;
			}
			final String znkey = key.substring(KEY_PRE.length());
			if (!znkey.matches("\\d{1,9}")) {
				continue;
			}
			final int nzcode = Integer.valueOf(znkey);
			final String message = rsMessage.getString(key);
			final String cause = rsCause.getString(key);
			NZCode nz = NZCodeHolder.get(nzcode);
			if (nz == null) {
				NZCodeHolder.set(nzcode, cause, message);
				continue;
			}
			nz.setMessage(message);
			if (nz.getCause() != null && !nz.getCause().isEmpty()) {
				continue;
			}
			if (cause != null && cause.length() > 0) {
				nz.setCause(cause);
			}
		}
	}

	@RequestMapping(value = { "/", "/doc" })
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			// if (env_node == null) {
			// env_node = (String) request.getAttribute("node");
			// env_revision = (String) request.getAttribute("revxxx");
			// env_host = (String) request.getAttribute("hostName");
			// env_time = (String) request.getAttribute("deploytime");
			// env_deployer = (String) request.getAttribute("deployer");
			// }
			final String methodName = request.getParameter("methodName");
			final String version = request.getParameter("v");
			logAccess(methodName);

			StringBuilder returnStr = new StringBuilder();
			if (null == methodName) {
				returnStr.append(getMethodLinkList());
			} else {
				ApiFacadeMethod mm = CommandModelHolder.getApiFacadeMethod(methodName, version);
				if (mm == null) {
					returnStr.append("no such method or method unsupport");
				} else {
					returnStr.append(getDetailResp(mm.getLcpMethod(), mm.getLcpReq(), mm.getMcpReqType(),
							mm.getReqClass(), mm.getRespClass()));
				}
			}

			String s = head(true) + returnStr.toString() + foot();
			response.setContentType("text/html");
			// 禁用页面缓存
			response.setHeader("Pragma", "no-util");
			response.addHeader("Cache-Control", "must-revalidate");
			response.addHeader("Cache-Control", "no-util");
			response.addHeader("Cache-Control", "no-store");
			response.setDateHeader("Expires", 0);
			try (OutputStream os = response.getOutputStream()) {
				byte[] ob = s == null ? null : s.getBytes(CharEncoding.UTF_8);
				os.write(ob);
				os.flush();
			}
		} catch (Exception ex) {
			logger.error("doc error", ex);
			ex.printStackTrace();
		}
		return null;
	}

	private void logAccess(String methodName) {
		// logger.info(String.format("methodName:" + methodName));
	}

	private String head(boolean isTable) {
		return "<html><head><meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\"/>" + style()
				+ "</head><body>";
	}

	private String foot() {
		return "</body></html>";
	}

	private String style() {
		String s = "<style>table{font-size: 10pt;line-height: 13pt;}table { margin-bottom: 0; border-collapse: collapse; }caption {display: table-caption;text-align: -webkit-center; }tr{font-size: 10pt;line-height: 13pt;color: #000;font-weight: normal;}table th{ color: #000000; background-color: #f0f0f0; font-weight: bold;border-width: 1px;border-style: solid;border-color: #ddd;padding: 5px 7px;vertical-align: top;min-width: .6em;text-align: left;}. th, td {white-space: pre-wrap;}td{display: table-cell;color: #000;font-weight: normal;margin: 0;}table td{border-width:1px;border-style: solid;border-color: #ddd;padding: 5px 7px;vertical-align: top;min-width: .6em;text-align: left;} a:link{font-size:13px;color: #0000FF;text-decoration: none;}a:visited {font-size: 13px;color: #0000FF;text-decoration: none;}a:active {font-size: 13px;color: #0000FF;text-decoration: none;}a:hover { font-size: 13px;color: #ffffff;background-color:#8888ff;text-decoration: none;benc:expression(this.onmousemove=function(){window.status=\"Quad's API\";event.returnValue=true});}"
				+ "</style>";
		return s;
	}

	private void addErrorCode(final String command, LcpReq lcpReq, String fieldName, LcpRequired required) {
		int errorCode = 0;
		String errorCodeMethod = "";
		if (required != null && required.errorCode() > 0) {
			errorCode = required.errorCode();
			errorCodeMethod = String.format("%s(%s)", command, fieldName);
		} else if (lcpReq != null && lcpReq.errorCode() > 0) {
			errorCode = lcpReq.errorCode();
			errorCodeMethod = String.format("%s(%s)", command, lcpReq.name());
		}
		if (errorCode > 0) {
			Set<String> ls = errorCodeMap.get(errorCode);
			if (ls == null) {
				ls = new HashSet<String>();
				errorCodeMap.put(errorCode, ls);
			}
			ls.add(errorCodeMethod);
		}
	}

	/**
	 * 接口详情
	 * 
	 * @param model
	 * @return
	 */
	private String getDetailResp(LcpMethod lcpMethod, LcpReq lcpReq, Type type, Class<?> req, Class<?> resp) {
		StringBuilder s = new StringBuilder();
		s.append("<p></br></p>");
		s.append("<h5>" + "接口名:" + "</h5>");
		s.append("&nbsp;&nbsp;&nbsp;" + lcpMethod.name());
		s.append("<h5>" + "登录验证:" + "</h5>");
		s.append("&nbsp;&nbsp;&nbsp;" + lcpMethod.logon());
		s.append("<h5>" + "起始版本:" + "</h5>");
		s.append("&nbsp;&nbsp;&nbsp;" + lcpMethod.ver());
		s.append("<h5>" + "接口描述:" + "</h5>");
		s.append("&nbsp;&nbsp;&nbsp;" + lcpMethod.desc());
		if (lcpMethod.nzcode() != null && lcpMethod.nzcode().length > 0) {
			s.append("<table id='errorCode'><caption><h5>" + "错误码:"
					+ "</h5></caption><tr><th>错误码</th><th>原因</th><th>用户提示</th></tr>");
			for (final int nzcode : lcpMethod.nzcode()) {
				String cause = "";
				String message = "";
				{
					final NZCode nz = NZCodeHolder.get(nzcode);
					if (nz != null) {
						if (nz.getCause() != null)
							cause = nz.getCause();
						if (nz.getMessage() != null)
							message = nz.getMessage();
					}
				}
				s.append("<tr><td>");
				s.append(nzcode);
				s.append("</td><td>");
				s.append(cause);
				s.append("</td><td>");
				s.append(message);
				s.append("</td></tr>");
			}
			s.append("</table><br/>");
		}
		s.append("<h5 id='reqMemo'>" + "请求参数说明:" + "</h5>");
		if (lcpReq != null) {
			s.append(LcpModelDefLanHolder.getMtmlMDL(lcpReq, type, errorCodeMap));
		} else if (req != null) {
			s.append(LcpModelDefLanHolder.getMtmlMDL(req, errorCodeMap));
		} else {
			s.append("<h5>此接口无专用参数，仅提供平台必要的参数即可。</h5>");
		}
		if (resp == void.class) {
			s.append("<h5 id='respMemo'>" + "返回值为void，无错误码即为成功。" + "</h5>");
		} else {
			s.append("<h5 id='respMemo'>" + "返回数据说明:" + "</h5>");
			if (Integer.class == resp) {
				s.append(LcpModelDefLanHolder.getMtmlMDL(ResultInt.class, errorCodeMap));
			} else if (Long.class == resp) {
				s.append(LcpModelDefLanHolder.getMtmlMDL(ResultLong.class, errorCodeMap));
			} else if (String.class == resp) {
				s.append(LcpModelDefLanHolder.getMtmlMDL(ResultString.class, errorCodeMap));
			} else {
				s.append(LcpModelDefLanHolder.getMtmlMDL(resp, errorCodeMap));
			}
		}
		return s.toString();
	}

	/**
	 * 获得接口列表
	 * 
	 * @return
	 */
	private String getMethodLinkList() {
		StringBuilder returnStr = new StringBuilder();
		returnStr.append("<table> ");
		returnStr.append("<caption title=\"");
		returnStr.append("node: ");
		returnStr.append(env_node);
		returnStr.append("\r\nhost: ");
		returnStr.append(env_host);
		returnStr.append("\r\nuptime: ");
		returnStr.append(env_time);
		returnStr.append("\r\nbuilder: ");
		returnStr.append(env_builder);
		returnStr.append("\r\nbuildtime: ");
		returnStr.append(env_buildtime);
		returnStr.append(
				"\">接口描述　注：接入规范在<a href='http://wiki-op.kuaipan.cn/pages/viewpage.action?pageId=27918971'>这里</a></caption>");
		returnStr.append("<tr>");
		returnStr.append("<th>");
		returnStr.append("分组");
		returnStr.append("</th>");
		returnStr.append("<th>");
		returnStr.append("分组名称");
		returnStr.append("</th>");
		returnStr.append("<th>");
		returnStr.append("序号");
		returnStr.append("</th>");
		returnStr.append("<th>");
		returnStr.append("接口名称");
		returnStr.append("</th>");
		returnStr.append("<th>");
		returnStr.append("版本");
		returnStr.append("</th>");
		returnStr.append("<th>");
		returnStr.append("调用次数");
		returnStr.append("</th>");
		returnStr.append("<th>");
		returnStr.append("登录");
		returnStr.append("</th>");
		returnStr.append("<th>");
		returnStr.append("接口描述");
		returnStr.append("</th>");
		returnStr.append("</tr>");

		Map<String, List<String>> methodMap = new HashMap<String, List<String>>();
		for (String methodName : methods) {
			String method[] = methodName.split("\\.");
			List<String> methodLists = methodMap.get(method[0]);
			if (null == methodLists) {
				methodLists = new ArrayList<String>();
				methodMap.put(method[0], methodLists);
			}
			methodLists.add(methodName);

		}

		List<String> methodKeys = new ArrayList<String>(methodMap.keySet());
		Collections.sort(methodKeys);
		int j = 1;
		Map<String, List<String>> cmds = this.commandLookupService.getCommands();
		for (String methodkey : methodKeys) {
			List<String> methodValues = methodMap.get(methodkey);
			Collections.sort(methodValues);
			int size = 0;
			for (String methodValue : methodValues) {
				size += cmds.get(methodValue).size();
			}
			int i = 1;
			for (String methodValue : methodValues) {
				List<String> vers = cmds.get(methodValue);
				Collections.sort(vers);
				for (String ver : vers) {
					ApiFacadeMethod mm = CommandModelHolder.getApiFacadeMethod(methodValue, ver);
					String desc = mm.getLcpMethod().desc();
					returnStr.append("<tr id='" + methodkey + "'>");
					if (i == 1) {
						returnStr.append("<td rowspan=" + size + " style=\"text-align:right\">");
						returnStr.append(j++);
						returnStr.append("</td>");
						returnStr.append("<td rowspan=" + size + ">");
						returnStr.append(methodkey);
						returnStr.append("</td>");
					}

					returnStr.append("<td style=\"text-align:right\" id='" + methodValue + ":" + ver + "'>");
					returnStr.append(i++);
					returnStr.append("</td>");
					returnStr.append("<td>");
					returnStr.append("<a href=\"");
					returnStr.append("/doc?methodName=" + methodValue);
					returnStr.append("&v=");
					returnStr.append(ver);
					returnStr.append("\" >" + methodValue + "</a><br/>");
					returnStr.append("</td>");
					returnStr.append("<td>");
					returnStr.append(ver);
					returnStr.append("</td>");
					returnStr.append("<td style=\"text-align:right\">");
					returnStr.append(commandLookupService.getApiAndVerPv(String.format("%s:%s", methodValue, ver)));
					returnStr.append("</td>");
					returnStr.append("<td>");
					if (mm.getLcpMethod().logon()) {
						returnStr.append("是");
					} else {
						returnStr.append("-");
					}
					returnStr.append("</td>");
					returnStr.append("<td>");
					returnStr.append(desc);
					returnStr.append("</td>");
					returnStr.append("</tr>");
				}
			}
		}
		returnStr.append("</table> ");

		returnStr.append("<br/><br/>");

		returnStr.append(
				"<table id='errorCode'><caption>错误码定义</caption><tr><th>错误码</th><th>原因：代码优先级高于配置文件</th><th>用户提示：配置文件优先级高于代码</th></tr>");
		for (final int nzcode : NZCodeHolder.getSortedNZCodes()) {
			if (nzcode == 0)
				continue;
			String cause = "";
			String message = "";
			{
				final NZCode nz = NZCodeHolder.get(nzcode);
				if (nz != null) {
					if (nz.getCause() != null)
						cause = nz.getCause();
					if (nz.getMessage() != null)
						message = nz.getMessage();
				}
			}
			returnStr.append("<tr><td>");
			returnStr.append(nzcode);
			returnStr.append("</td><td>");
			returnStr.append(cause);
			returnStr.append("</td><td>");
			returnStr.append(message);
			returnStr.append("</td></tr>");
		}
		returnStr.append("</table><br/>");
		return returnStr.toString();
	}

	public static String getMcsJson(Class<?> c) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(commandLookupService, "commandLookupService must not null!");
	}

	public static class ResultString {
		public String result;
	}

	public static class ResultInt {
		public int result;

	}

	public static class ResultLong {
		public long result;

	}
}
