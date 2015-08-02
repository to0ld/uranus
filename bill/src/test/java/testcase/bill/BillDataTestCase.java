package testcase.bill;

import java.util.Arrays;
import java.util.List;

import net.popbean.pf.bill.service.BillDataBusinessService;
import net.popbean.pf.bill.service.BillModelBusinessService;
import net.popbean.pf.bill.vo.BillModel;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.helper.IOHelper;
import net.popbean.pf.security.vo.SecuritySession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@ContextConfiguration({"classpath:spring/mvc.test.xml","classpath:spring/app.test.xml"})
public class BillDataTestCase extends AbstractTestNGSpringContextTests{
	@Autowired
	@Qualifier("service/pf/bill/data")
	BillDataBusinessService bdService;
	//
	@Autowired
	@Qualifier("service/pf/bill/model/redis")
	BillModelBusinessService bmService;
	//
	@Test
	public void testDataSet(){
		try {
			//
			SecuritySession session = mockLogin();
			//
			BillModel model = bmService.find("pf-dataset", null, true, session);
			Assert.assertNotNull(model);
			//1.1 load config
			String path = "classpath:/data/dataset.data";//如果是安装的情况
			String content = IOHelper.readByChar(path, "utf-8");
			JSONObject jo = JSON.parseObject(content);
			JSONArray ja = jo.getJSONArray("data");
			JSONObject data = ja.getJSONObject(0);
			//
			String ds_id = bdService.save("pf-dataset", null, true, data, session);
			List<JSONObject> result = bdService.fetchMainData("pf-dataset", JO.gen("id",ds_id), session);
			Assert.assertNotNull(result, "看，傻逼了吧，没有找到数据吧");
			List<JSONObject> slaves = bdService.fetchSlaveData("pf-dataset", null, ds_id, "pb_pf_ds_field", session);
			Assert.assertNotNull(slaves, "看，傻逼了吧，没有找到数据(slave)吧");
			bdService.delete("pf-dataset", Arrays.asList(ds_id), session);
		} catch (Exception e) {
			Assert.fail(e.getMessage(), e);
		}
		
		//1-利用安装的dataset的bill model进行数据的吞吐测试
	}
	private SecuritySession mockLogin(){
		SecuritySession session = new SecuritySession();
		session.account_id = "faker";
		return session;
	}
}
