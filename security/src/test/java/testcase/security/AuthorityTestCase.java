package testcase.security;

import net.popbean.pf.dataset.vo.DataSetModel;
import net.popbean.pf.entity.helper.VOHelper;
import net.popbean.pf.entity.service.EntityStructBusinessService;
import net.popbean.pf.helper.IOHelper;
import net.popbean.pf.rm.vo.ResourceMappingModel;
import net.popbean.pf.security.vo.AccountVO;
import net.popbean.pf.security.vo.PermVO;
import net.popbean.pf.security.vo.RltRoleAccount;
import net.popbean.pf.security.vo.RltRolePerm;
import net.popbean.pf.security.vo.RoleVO;
import net.popbean.pf.security.vo.SecuritySession;
import net.popbean.pf.testcase.TestHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 授权
 * 利用资源映射实现
 * @author to0ld
 *
 */
@ContextConfiguration(locations={"classpath:/spring/app.test.xml"})
public class AuthorityTestCase extends AbstractTestNGSpringContextTests{
	@Autowired
	@Qualifier("service/pf/entity/struct")
	EntityStructBusinessService esService;
	/**
	 * 
	 */
	private SecuritySession mockLogin(){
		SecuritySession session = new SecuritySession();
		return session;
	}
	@Test
	public void simplesuite(){
		try {
			SecuritySession session = mockLogin();
			//1-建表(account,role,perm,rltroleaccount,rltroleperm)
			esService.syncDbStruct(AccountVO.class, session);
			esService.syncDbStruct(RoleVO.class, session);
			esService.syncDbStruct(PermVO.class, session);
			esService.syncDbStruct(RltRoleAccount.class, session);
			esService.syncDbStruct(RltRolePerm.class, session);
			//2-清理数据(如果是正式数据还是不要动的好，如果要动也是针对测试数据)
			//3-初始化授权模型
			//FIXME 需要参考spring中resourceutils的实现
			String content = IOHelper.readByChar("data/rm_1.data", "utf-8");
			JSONObject json = JSON.parseObject(content);
			json = json.getJSONObject("data");
			ResourceMappingModel model = JSON.toJavaObject(json, ResourceMappingModel.class);//维护一个资源映射模型
			//制作testcase app的http://role/{app_code}数据集(为了简单起见，我就直接mock了)
			DataSetModel role_ds_model = null;//模拟一个指定app_code的角色列表
			DataSetModel perm_ds_model = null;//模拟一个指定app_code的功能节点清单
			String role_ref = VOHelper.buildRef(role_ds_model.id, role_ds_model.name);
			String perm_ref = VOHelper.buildRef(perm_ds_model.id, perm_ds_model.name);
			model.subject_ref = role_ref;
			model.resource_ref = perm_ref;
			//制作testcase app的perm的数据集(为了简单起见，我就直接mock了)
			//4-创建一个角色
			//5-创建一个虚拟的app
			//5-进行授权(为某角色授予权限)
			//6-验证有无该权限:mockmvc
			//7.1-看起来需要一个拦截器
			//6.0-模拟用户创建
			//6.1-模拟用户创建关联角色
			//6.2-模拟用户登录
			//6.3-模拟用户访问某个app的某个功能节点：{app}.xx.com/node/a/b/c ->
			//6.3.1-分为有权限，无权限两种情况给出预期的返回值/界面
			//FIXME app之间应该是一个restful耦合的关系，汇聚在accountcenter ->ac.xx.com or tealc.ac.xx.com
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
}
