package testcase.server;

import net.popbean.a.service.EchoService;
import net.popbean.b.service.FooService;
import net.popbean.soa.service.HelloService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
/**
 * 验证一下启动服务后，消费服务的情况，为了简单起见放到了一个容器中
 * 就性能而言，同一容器同一端口应该使用local，而不是rpc
 * @author to0ld
 *
 */
@ContextConfiguration(locations={"classpath:/spring/app.test.xml"})
public class ThriftServerTestCase extends AbstractTestNGSpringContextTests {
	@Autowired
	@Qualifier("serivce/hello")
	HelloService.Iface helloService;
	@Autowired
	@Qualifier("serivce/echo")
	EchoService.Iface echoService;
	@Autowired
	@Qualifier("serivce/foo")
	FooService.Iface fooService;
	//
	@Test
	public void simple(){
		try {
			String ret = helloService.say("to0ld");
			System.out.println(ret);
			//
			ret = echoService.execute("to0ld");
			System.out.println(ret);
			//
			ret = fooService.bar("test");
			System.out.println(ret);
			//
		} catch (Exception e) {
			Assert.fail(e.getMessage(), e);
		}
		
	}
}
