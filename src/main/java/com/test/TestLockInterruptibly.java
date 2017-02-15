package com.test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestLockInterruptibly {

	Lock lock = new ReentrantLock();

	public static void main(String[] args) {
		TestLockInterruptibly test = new TestLockInterruptibly();

		MyThread thread0 = new MyThread(test);
		MyThread thread1 = new MyThread(test);

		thread0.start();
		thread1.start();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// thread1.interrupt();
	}

	public void insert(Thread thread) throws InterruptedException {
		lock.lockInterruptibly();
		try {
			System.out.println(thread.getName() + "得到了锁");
			long startTime = System.currentTimeMillis();
			for (;;) {
				if (System.currentTimeMillis() - startTime >= 5000) {
					break;
				}
			}
		} catch (Exception e) {
			// TODO
		} finally {
			System.out.println(Thread.currentThread().getName() + "执行finally");
			lock.unlock();
			System.out.println(thread.getName() + "释放了锁");
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
			System.out.println(Thread.currentThread().getName() + "被中断");
		}
	}
}