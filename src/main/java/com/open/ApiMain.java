package com.open;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

import com.open.env.finder.EnvFinder;

/**
 * @ComponentScan : 开启扫描，会扫描当前类的包及其子包
 * @EnableAutoConfiguration : 这个注解告诉Spring
 *                          Boot根据添加的jar依赖猜测你想如何配置Spring。由于spring-boot-starter-web添加了Tomcat和Spring
 *                          MVC，所以auto-configuration将假定你正在开发一个web应用，并对Spring进行相应地设置。
 * @EnableCaching : 开启spring-cache,
 *                ConcurrentHashMap是其存储结构，由ConcurrentHashMapFactoryBean创建和管理.
 * 
 * @author hepengyuan
 *
 */
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, WebMvcAutoConfiguration.class })
@ComponentScan
@EnableCaching
public class ApiMain {

	public static void main(String[] args) throws Exception {
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
		new SpringApplicationBuilder(ApiMain.class).run(args);
	}
}
