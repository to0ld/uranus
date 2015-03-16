package testcase.pf.rm;

import net.popbean.pf.testcase.TestHelper;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * -[] 构建资源映射模型
 * -[] 对resource进行排序
 * -[] 对resource进行封存/启封操作(需要提供参数，以决定是否过滤数据)
 * @author to0ld
 *
 */
@ContextConfiguration(locations={"classpath:/spring/app.test.xml"})
public class ResourceMappingTestCase extends AbstractTestNGSpringContextTests{
	@Test
	public void test(){
		try {
			//1-构建两个模拟的数据集
			//2-构建一个资源映射模型
			//3-给出subject进行授权操作
			//4-给出subject进行取消授权操作
			//5-根据3，4操作进行数据查证，判断是否有违例出现
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
}
