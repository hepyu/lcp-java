package com.open.lcp.core.framework;

import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;


public class IdWorker {

	private final long workerId;
	private final static long twepoch = 1361753741828L;
	private long sequence = 0L;
	private final static long workerIdBits = 8L;
	private final static long maxWorkerId = -1L ^ -1L << workerIdBits;
	private final static long sequenceBits = 10L;

	private final static long workerIdShift = sequenceBits;
	private final static long timestampLeftShift = sequenceBits + workerIdBits;
	private final static long sequenceMask = -1L ^ -1L << sequenceBits;

	private long lastTimestamp = -1L;

	public IdWorker(final long workerId) {
		super();
		if (workerId > maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException(String.format(
					"worker Id can't be greater than %d or less than 0",
					maxWorkerId));
		}
		this.workerId = workerId;
	}
	
	public long[] nextBatchId(int count) {
		
		if(count <= 0 || count > 100000)
			throw new IllegalArgumentException("count must be larger than 0 and less than 1000.");
		
		synchronized(this) {		
			
			long[] idList = new long[count];
			long timestamp = this.timeGen();
	
			for(int i=0;i<count;i++) {
			
				if (this.lastTimestamp == timestamp) {
					this.sequence = (this.sequence + 1) & sequenceMask;
					if (this.sequence == 0) {
						timestamp = this.tilNextMillis(this.lastTimestamp);
					}
				} else {
					this.sequence = 0;
				}
				if (timestamp < this.lastTimestamp) {
						throw new RuntimeException(
								String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
										this.lastTimestamp - timestamp));
					
				}
		
				this.lastTimestamp = timestamp;
				long nextId = ((timestamp - twepoch << timestampLeftShift))
						| (this.workerId << workerIdShift) | (this.sequence);
			
				idList[i] =nextId;
			}
			
			return idList;
		
		}
	}

	public long nextId() {
		
		synchronized(this) {						
			long timestamp = this.timeGen();
			if (this.lastTimestamp == timestamp) {
				this.sequence = (this.sequence + 1) & sequenceMask;
				if (this.sequence == 0) {
					timestamp = this.tilNextMillis(this.lastTimestamp);
				}
			} else {
				this.sequence = 0;
			}
			if (timestamp < this.lastTimestamp) {
					throw new RuntimeException(
							String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
									this.lastTimestamp - timestamp));
			}
	
			this.lastTimestamp = timestamp;
			long nextId = ((timestamp - twepoch << timestampLeftShift))
					| (this.workerId << workerIdShift) | (this.sequence);
			
			return nextId;
		}	
	}

	private long tilNextMillis(final long lastTimestamp) {
		long timestamp = this.timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = this.timeGen();
		}
		return timestamp;
	}
	
	public long getLastTimestamp() {
		return lastTimestamp;
	}

	private long timeGen() {
		return System.currentTimeMillis();
	}
	
	public long toTimeStamp(long id) {
		return ((id >> IdWorker.timestampLeftShift) + twepoch) / 1000;
	}
	
	public long getTimestamp(long id) {
		return (id >> timestampLeftShift) + twepoch;
	}
	
	public long getCurrentAdminStartId() {
		return ((System.currentTimeMillis() - twepoch << timestampLeftShift)) | 0 | 0;
	}
	
	public long nextAdminId(long timestamp) {
		// TODO:set another workerId
		long tempWorkId = this.workerId + 100;

		return ((timestamp - twepoch << timestampLeftShift))
				| (tempWorkId << workerIdShift) | 0;

	}
	
	public long getStartId(long timestamp) {
		
		return ((timestamp - twepoch << timestampLeftShift)); //| 0 | 0

	}
	
	public long test(long timestamp) {
		
		return ((timestamp - twepoch << timestampLeftShift))
				| (this.workerId << workerIdShift) | 0;

	}
	
	public static void main(String[] args) {
		IdWorker worker = new IdWorker(1);
				
		System.out.println(worker.nextId());
		
		long timestamp = DateUtils.addDays(DateUtils.ceiling(new Date(), Calendar.DATE), -8).getTime() ;
		
		System.out.println(new Date(timestamp));
		System.out.println(worker.getStartId(timestamp));
		
		
	}
}
