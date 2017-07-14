package com.open.lcp.dbs.cache;

public class Renewal<V> {
	
	public Renewal(long tick, V v) {
		this.tick = tick;
		this.v = v;
	}

	private long tick;
	private V v;

	public long getTick() {
		return tick;
	}

	public void setTick(long tick) {
		this.tick = tick;
	}

	public V getV() {
		return v;
	}

	public void setV(V v) {
		this.v = v;
	}

}
