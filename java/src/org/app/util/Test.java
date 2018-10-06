package org.app.util;

import java.util.Random;

public class Test {
	public static void main(String[] args) {
		int m = 2147483647;
		Random r = new Random();
		while(true) {
			int num = r.nextInt(200);
			System.out.println(num);
			if(num<20)
				break;
		}
	}
}
