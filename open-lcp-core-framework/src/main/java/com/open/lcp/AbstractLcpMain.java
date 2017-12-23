package com.open.lcp;

import org.springframework.boot.builder.SpringApplicationBuilder;

import com.open.lcp.core.env.finder.EnvFinder;

public abstract class AbstractLcpMain {

	protected static void start(String[] args) {
		// for (int i = 0; i < args.length; i++) {
		// if ("adpre".equals(args[i])) {
		// System.setProperty("adpre", "1");
		// }
		// }
		final String proFileName = EnvFinder.getProfile().name();
		System.setProperty("spring.profiles.active", proFileName);
		// System.setProperty("com.open.lcp.dao.*", "1");
		// logger.info(
		// String.format("ApiMain: start @ %s, profile: %s",
		// XunleiEnvFinder.getIpcfg().toString(), proFileName));
		new SpringApplicationBuilder(LcpCoreFrameworkMain.class).run(args);
	}
}
