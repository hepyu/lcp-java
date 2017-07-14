package com.open.lcp.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static final String DATE_TIME = "yyyy-MM-dd";

	public static final String DATE_TIME_SS = "yyyy-MM-dd HH:mm:ss";

	public static String dateToStr(Date date) {

		return dateToStr(date, DATE_TIME);
	}

	public static String dateToStr(Date date, String time) {

		String dateStr = "";
		try {

			SimpleDateFormat dateFormate = new SimpleDateFormat(time);
			dateStr = dateFormate.format(date);

		} catch (Exception e) {
			System.err.println(e);
		}
		return dateStr;
	}
}
