package com.open.dbs.cache;

import java.util.concurrent.atomic.AtomicInteger;

public class SSDBCounterByThread {
	private static boolean enabled = false;

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

	public static int clear() {
		return me().counter.getAndSet(0);
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
	}
}
