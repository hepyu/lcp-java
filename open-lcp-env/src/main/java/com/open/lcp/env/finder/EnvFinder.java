package com.open.lcp.env.finder;

import java.net.InetAddress;

public class EnvFinder {

	public static EnvEnum getProfile() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String ipPrefix = addr.getHostAddress();
			if (ipPrefix.startsWith("192.168.")) {
				return EnvEnum.dev;
			}
			return HostHolder.findProfile(addr.getHostAddress());
		} catch (Exception e) {
			throw new Error("no env exist this host:");
		}
	}

	public static String findProfile(String host) throws Exception {
		return HostHolder.findProfile(host).name();
	}

}
