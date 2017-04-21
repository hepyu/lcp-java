package com.test;

import java.util.HashSet;
import java.util.Set;

public class Test {

	public static void main(String[] args) {
		Set<Long> set = new HashSet<Long>();
		set.add(1l);

		int i=1;
		long l=i;
		
		System.out.println(set.contains(1));
		System.out.println(set.contains(1l));
		System.out.println(set.contains(i));
		System.out.println(set.contains(l));
		System.out.println(set.contains(Long.valueOf(1)));
	}
}
