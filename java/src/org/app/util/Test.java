package org.app.util;

import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Test {
	public static void main(String[] args) {
		Logger log = Logger.getLogger(Test.class.getClass());
		log.setLevel(Level.INFO);
		/*int m = 2147483647;
		Random r = new Random();
		while(true) {
			int num = r.nextInt(200);
			System.out.println(num);
			if(num<20)
				break;
		}*/
		log.info("ddd");
		log.error("error");
	}
}
