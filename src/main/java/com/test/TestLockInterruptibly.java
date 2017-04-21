package com.test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestLockInterruptibly {

	Lock lock = new ReentrantLock();

	public static void main(String[] args) {
		
		long allCommentNum = 100;
		long playNum = 500;
		long praiseNum = 20;
		
		
		double rate1 = (allCommentNum*1.0/playNum);
		double rate2 = (praiseNum*1.0/playNum);
		
		System.out.println("rate1:"+rate1);
		System.out.println("rate2:"+rate2);
		
//		TestLockInterruptibly test = new TestLockInterruptibly();
//
//		MyThread thread0 = new MyThread(test);
//		MyThread thread1 = new MyThread(test);
//
//		thread0.start();
//		thread1.start();
//
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		// thread1.interrupt();
	}

	public void insert(Thread thread) throws InterruptedException {
		lock.lockInterruptibly();
		try {
			System.out.println(thread.getName() + "寰楀埌浜嗛攣");
			long startTime = System.currentTimeMillis();
			for (;;) {
				if (System.currentTimeMillis() - startTime >= 5000) {
					break;
				}
			}
		} catch (Exception e) {
			// TODO
		} finally {
			System.out.println(Thread.currentThread().getName() + "鎵цfinally");
			lock.unlock();
			System.out.println(thread.getName() + "閲婃斁浜嗛攣");
		}
	}

}

class MyThread extends Thread {
	private TestLockInterruptibly test;

	public MyThread(TestLockInterruptibly test) {
		this.test = test;
	}

	@Override
	public void run() {
		try {
			test.insert(Thread.currentThread());
		} catch (InterruptedException e) {
			System.out.println(Thread.currentThread().getName() + "琚腑鏂�");
		}
	}
}