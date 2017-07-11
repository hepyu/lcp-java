package com.open.lcp.plugin.passport.util;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderAvatarUtil {

	private static final List<String> urlList;

	static {
		urlList = new ArrayList<>();
		urlList.add("http://7xt0pj.com1.z0.glb.clouddn.com/headIcon_placeholder_6.png");
		urlList.add("http://7xt0pj.com1.z0.glb.clouddn.com/headIcon_placeholder_7.png");
		urlList.add("http://7xt0pj.com1.z0.glb.clouddn.com/headIcon_placeholder_8.png");
		urlList.add("http://7xt0pj.com1.z0.glb.clouddn.com/headIcon_placeholder_9.png");
		urlList.add("http://7xt0pj.com1.z0.glb.clouddn.com/headIcon_placeholder_10.png");
		urlList.add("http://7xt0pj.com1.z0.glb.clouddn.com/headIcon_placeholder_11.png");
	}

	public static String getPlaceholderAvatar() {
		int index = (int) (Math.random() * 5);
		return urlList.get(index);
	}

	public static String getPlaceholderAvatarByMod(long number) {
		int size = urlList.size();
		int modResult = (int) (number % size);
		return urlList.get(modResult);
	}

}
