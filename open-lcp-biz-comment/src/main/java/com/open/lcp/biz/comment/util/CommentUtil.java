package com.open.lcp.biz.comment.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CommentUtil {

	public static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues()
			.create();

	/**
	 * 转换额外参数为json串
	 * 
	 * @param extKeys
	 * @param extParams
	 * @return
	 */
	public static String transformExtParams(String extKeys, Map<String, String> extParams) {
		if (!StringUtils.isEmpty(extKeys) && !CollectionUtils.isEmpty(extParams)) {
			String[] keys = extKeys.split(",");
			Set<String> keySet = new HashSet<String>(Arrays.asList(keys));
			Map<String, String> paramMap = new HashMap<String, String>();
			for (Map.Entry<String, String> entry : extParams.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (keySet.contains(key)) {
					paramMap.put(key, value);
				}
			}
			return gson.toJson(paramMap);
		}
		return null;
	}
}
