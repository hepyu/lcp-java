package com.open.lcp.schedule;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @EnableScheduling : 开启计划任务
 * @Scheduled(fixedRate = 1000 * 30) : ����ĳ����ʱ����
 */
@EnableScheduling
public class LCPScheduler {

	// 秒 分 时 日 月 年
	@Scheduled(cron = "0 */1 *  * * * ")
	public void ping() {
		// TODO
	}
}
