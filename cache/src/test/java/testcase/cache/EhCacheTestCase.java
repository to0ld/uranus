package testcase.cache;

import net.popbean.pf.cache.service.CacheManagementBusinessService;
import net.popbean.pf.cache.test.service.HelloService;
import net.popbean.pf.testcase.TestHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * -[] mongo vs redis
 * -[] available
 * @author to0ld
 *
 */
@ContextConfiguration(locations={"classpath:/spring/app.test.ehcache.xml"})
public class EhCacheTestCase extends AbstractTestNGSpringContextTests{
	//
	@Autowired
	@Qualifier("service/test/hello/ehcache")
	HelloService hello_ehcache_Service;
	//
	@Test
	public void performanceEhCache(){
		try {
			//留意一下hello service我只是做了一个示范
			//获取初始值，比较(初始应该是0)
			hello_ehcache_Service.clean();//清理历史数据
			//10000次大概是1269毫秒
			int loop = 10000;
			String ret = null;
			long start = System.currentTimeMillis();
			for(int i=0;i<loop;i++){
				ret = hello_ehcache_Service.say();	
			}
			long end = System.currentTimeMillis();
			System.out.println("get:"+(end-start));
			int count = hello_ehcache_Service.count();
			Assert.assertEquals(ret, "say hello("+count+")");
			
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
}
