package com.open.lcp.core.framework.loader;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 统一调度器，节省一些线程，节省每个需要自动调度数据的开发量。
 * 
 * @author
 *
 */
@Service
public class TimerLoaderManager implements InitializingBean {
	private static final Log logger = LogFactory.getLog(TimerLoaderManager.class);

	@Autowired
	private List<TimerLoader> reloads;

	class ReloadWorker implements Runnable {
		@Override
		public void run() {
			final int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			final int minute = Calendar.getInstance().get(Calendar.MINUTE);
			final long minuteOfAll = System.currentTimeMillis() / 1000 / 60;
			for (TimerLoader r : reloads)
				try {
					if (r.reloadable(hour, minute, minuteOfAll)) {
						String log = r.reload();
						if (log != null) {
							logger.info(String.format("reload %s: %s", r.getClass().getName(), log));
						}
					}
				} catch (Exception e) {
					logger.error("reload failed @" + r.getClass().getName(), e);
				}
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (reloads == null)
			return;
		for (TimerLoader r : reloads)
			try {
				if (r.initLoad()) {
					String log = r.reload();
					if (log != null) {
						logger.info(String.format("reload %s: %s", r.getClass().getName(), log));
					}
				}
			} catch (Exception e) {
				logger.error("reload failed @" + r.getClass().getName(), e);
			}
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new ReloadWorker(), 1, 1, TimeUnit.MINUTES);
	}
}
