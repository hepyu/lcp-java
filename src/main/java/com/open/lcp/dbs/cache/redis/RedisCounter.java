package com.open.lcp.dbs.cache.redis;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RedisCounter {

	private static final ThreadLocal<RedisAtomic> th = new ThreadLocal<RedisAtomic>();

	public static int reset() {
		me().cost.set(0);
		return me().counter.getAndSet(0);
	}

	public static int get() {
		return me().counter.get();
	}

	public static int inc() {
		return me().counter.incrementAndGet();
	}

	public static long getCost() {
		return me().cost.get();
	}

	public static void addCost(long costTime) {
		me().cost.addAndGet(costTime);
	}

	private static RedisAtomic me() {
		RedisAtomic ra = th.get();
		if (ra == null) {
			ra = new RedisAtomic();
			th.set(ra);
		}
		return ra;
	}

	private static final class RedisAtomic {
		private final AtomicInteger counter = new AtomicInteger(0);
		private final AtomicLong cost = new AtomicLong(0);
	}
}
