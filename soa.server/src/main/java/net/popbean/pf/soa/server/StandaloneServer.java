package net.popbean.pf.soa.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StandaloneServer {

	public static void main(String[] args) {
		try {
			ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring/server-context.xml");
//			Thread.sleep(3000000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
