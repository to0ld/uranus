package testcase.bpmn.deifne;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.popbean.pf.bpmn.define.service.ProcessDefineBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.security.service.AuthenticationBusinessService;
import net.popbean.pf.security.vo.SecuritySession;
import net.popbean.pf.testcase.TestHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;

@ContextConfiguration(locations = { "classpath:/spring/app.test.xml" })
public class DefineTestCase extends AbstractTestNGSpringContextTests{
	@Autowired
	@Qualifier("service/pf/security/auth")
	AuthenticationBusinessService authService;
	@Autowired
	@Qualifier("service/pf/bpmn/process/define")
	ProcessDefineBusinessService pdService;
	private SecuritySession mockLogin(){
		SecuritySession session = new SecuritySession();
		return session;
	}
	//
	@Test
	public void simple(){
		try {
			//定义一个简单的流程：start->end(code=first-flow)
//			SecuritySession client = authService.buildSession("test");//这个用户根本不存在啊，所以需要mock
			SecuritySession client = mockLogin();
			List<String> id_list = pdService.deploy(Arrays.asList("bpmn/pb.test.bpmn"), client);
			Assert.assertNotNull(id_list);
			List<JSONObject> pd_list = pdService.fetch(JO.gen("code","pb.test"), client);
			Assert.assertNotNull(pd_list,"null就不正常了");
			//部署一个版本
			//更新为一个新版本(另外一个文件，只不过编码都叫同一个名字)
			//查询部署内容，输出
			//取消部署
			List<String> deploy_id_list = new ArrayList<>();
			for(JSONObject pd:pd_list){
				JSONObject inst = pdService.findDeployModel(pd.getString("DEPLOYMENT_ID_"), client);
				Assert.assertNotNull(inst);
				deploy_id_list.add(pd.getString("DEPLOYMENT_ID_"));
			}
			pdService.unDeploy(deploy_id_list, client);
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
}
