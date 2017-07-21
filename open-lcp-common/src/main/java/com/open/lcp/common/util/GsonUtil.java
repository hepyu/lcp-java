package com.open.lcp.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {

	public final static Gson gson = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues()
			.create();

	public final static Gson gsonOnlyExpose = new GsonBuilder().disableHtmlEscaping()
			.excludeFieldsWithoutExposeAnnotation().create();
}
