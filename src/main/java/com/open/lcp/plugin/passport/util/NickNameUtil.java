package com.open.lcp.plugin.passport.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class NickNameUtil {

	// private static final Log logger = LogFactory.getLog(NickNameUtil.class);

	public static String convertNickName(String mobile) {
		if (isMobile(mobile)) {
			mobile = mobile.substring(0, 3) + "****" + mobile.substring(7);
		}
		return mobile;
	}

	private static boolean isMobile(String mobile) {
		Pattern p = Pattern.compile("^(1)\\d{10}$");
		Matcher m = p.matcher(mobile);
		return m.matches();
	}

	public static void main(String[] args) {
		System.out.println(isMobile("17021995348"));
	}
}
