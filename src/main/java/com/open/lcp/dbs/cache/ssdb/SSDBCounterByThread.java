package com.open.lcp.dbs.cache.ssdb;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SSDBCounterByThread {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(SSDBCounterByThread.class);

	private static boolean enabled = false;
	private static long warnTime = 100;

	public static long getWarnTime() {
		return warnTime;
	}

	public static void setWarnTime(long warnTime) {
		SSDBCounterByThread.warnTime = warnTime;
	}

	public static void enable() {
		enabled = true;
	}

	public static void disable() {
		enabled = false;
	}

	private static final ThreadLocal<SSDBCounter> th = new ThreadLocal<SSDBCounter>();

	private SSDBCounterByThread() {
	}

	public static int inc() {
		if (!enabled) {
			return 0;
		}
		return me().counter.incrementAndGet();
	}

	public static int get() {
		return me().counter.get();
	}

	public static long getCost() {
		return me().cost.get();
	}

	public static int clear() {
		final SSDBCounter me = me();
		me.key = null;
		me.keys = null;
		me.beginTimer = 0;
		me.cost.set(0);
		return me.counter.getAndSet(0);
	}

	public static void beginTimer(byte[] key) {
		final SSDBCounter me = me();
		me.beginTimer = System.currentTimeMillis();
		me.key = key;
	}

	public static void beginTimer(byte[][] keys) {
		final SSDBCounter me = me();
		me.beginTimer = System.currentTimeMillis();
		me.keys = keys;
	}

	public static long endTimer() {
		final SSDBCounter me = me();
		final long costTime = System.currentTimeMillis() - me.beginTimer;
		if (costTime >= warnTime) {
			String key = null;
			if (me.key != null) {
				key = new String(me.key);
			} else if (me.keys != null) {
				String[] ss = new String[me.keys.length];
				for (int i = 0; i < ss.length; i++) {
					byte[] btsKey = me.keys[i];
					if (btsKey == null) {
						ss[i] = "null";
					} else {
						ss[i] = new String(btsKey);
					}
				}
				key = ss.toString();
			}
			logger.warn(String.format("SSDB Cost %s:%s", key, costTime));
		}
		me.cost.addAndGet(costTime);
		return costTime;
	}

	private static SSDBCounter me() {
		SSDBCounter me = th.get();
		if (me == null) {
			me = new SSDBCounter();
			th.set(me);
		}
		return me;
	}

	private static final class SSDBCounter {
		private final AtomicInteger counter = new AtomicInteger(0);
		private final AtomicLong cost = new AtomicLong(0);
		private long beginTimer;
		private byte[] key;
		private byte[][] keys;
	}
}
