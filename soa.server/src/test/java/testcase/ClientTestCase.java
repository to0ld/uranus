package testcase;

import net.popbean.a.service.EchoService;
import net.popbean.b.service.FooService;
import net.popbean.soa.service.HelloService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ClientTestCase {

	public static void main(String[] args) {
		try {
			ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring/app.test.multiplexed.client.xml");
//			HelloService.Iface inst = ac.getBean("serivce/hello",HelloService.Iface.class);
//			String ret = inst.say("client");
//			System.out.println(ret+"==>");
			System.out.println(".....started(client)");
			//
			EchoService.Iface echoService =  ac.getBean("serivce/echo",EchoService.Iface.class);
			String ret = echoService.execute("to0ld");
			System.out.println(ret);
//			//
			FooService.Iface fooService =  ac.getBean("serivce/foo",FooService.Iface.class);
			ret = fooService.bar("test");
			System.out.println(ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
