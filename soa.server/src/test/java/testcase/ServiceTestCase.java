package testcase;

import net.popbean.a.service.EchoService;
import net.popbean.soa.service.HelloService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceTestCase {

	public static void main(String[] args) {
		try {
			ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring/app.test.multiplexed.server.xml");
//			HelloService.Iface inst = ac.getBean("serivce/hello",HelloService.Iface.class);
//			String ret = inst.say("client");
//			System.out.println(ret+"==>");
			System.out.println(".....started");
//			EchoService.Iface inst = ac.getBean("serivce/echo/mock",EchoService.Iface.class);
//			String ret = inst.execute("client");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
