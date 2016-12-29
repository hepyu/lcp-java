package com.open.lcp.schedule;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @EnableScheduling : 标注启动定时任务。声明当前类是定时任务配置类.
 * @Scheduled(fixedRate = 1000 * 30) : 定义某个定时任务。
 */
@EnableScheduling
public class LCPScheduler {

	// 每1分钟执行一次
	@Scheduled(cron = "0 */1 *  * * * ")
	public void ping() {
		// TODO
	}
}
