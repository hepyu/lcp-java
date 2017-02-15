package com.test;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestTryLock {

	private ArrayList<Integer> arrayList = new ArrayList<Integer>();

	Lock lock = new ReentrantLock();

	public static void main(String[] args) {
		final TestTryLock test = new TestTryLock();

		new Thread() {
			public void run() {
				test.insert(Thread.currentThread());
			}
		}.start();

		new Thread() {
			public void run() {
				test.insert(Thread.currentThread());
			}
		}.start();
	}

	public void insert(Thread thread) {
		if (lock.tryLock()) {
			try {
				System.out.println(thread.getName() + "得到了锁");
				for (int i = 0; i < 5; i++) {
					arrayList.add(i);
				}
			} catch (Exception e) {
				// TODO
			} finally {
				System.out.println(thread.getName() + "释放了锁");
				lock.unlock();
			}
		}else{
			System.out.println(thread.getName()+"获取锁失败");
		}
	}
}