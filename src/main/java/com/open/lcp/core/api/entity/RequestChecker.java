package com.open.lcp.core.api.entity;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestChecker {

	public static final long PERIOD_REQ = 60 * 1000;// Ò»·ÖÖÓ
	private long beginTime = System.currentTimeMillis();
	private long notifyTime = 0;

	private AtomicInteger requestNum = new AtomicInteger(0);

	public int incAndGet() {
		final long now = System.currentTimeMillis();
		if (now - beginTime > PERIOD_REQ) {// ³¬ÏÞ
			requestNum.set(1);
			this.beginTime = now;
			return 1;
		}
		return requestNum.incrementAndGet();
	}

	/**
	 * @return the time
	 */
	public long getLastNotifyDist() {
		return System.currentTimeMillis() - this.notifyTime;
	}

	public void notified() {
		this.notifyTime = System.currentTimeMillis();
	}

}
