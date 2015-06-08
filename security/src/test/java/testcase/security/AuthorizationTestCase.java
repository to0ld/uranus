package testcase.security;

import java.util.Arrays;

import net.popbean.pf.business.service.CommonBusinessService;
import net.popbean.pf.dataset.vo.DataSetFieldModel;
import net.popbean.pf.dataset.vo.DataSetModel;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.model.EntityModel;
import net.popbean.pf.entity.service.EntityStructBusinessService;
import net.popbean.pf.helper.IOHelper;
import net.popbean.pf.id.helper.IdGenHelper;
import net.popbean.pf.persistence.vo.BatchVO;
import net.popbean.pf.rm.helper.ResourceMappingHelper;
import net.popbean.pf.rm.service.ResourceMappingBusinessService;
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
public class AuthorizationTestCase extends AbstractTestNGSpringContextTests{
	@Autowired
	@Qualifier("service/pf/entity/struct")
	EntityStructBusinessService esService;
	@Autowired
	@Qualifier("service/pf/common")
	CommonBusinessService commonService;
	@Autowired
	@Qualifier("service/pf/resourcemapping")
	ResourceMappingBusinessService rmService;
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
			//
			esService.syncDbStruct(BatchVO.class, session);
			esService.syncDbStruct(EntityModel.class, session);
			esService.syncDbStruct(DataSetModel.class, session);
			esService.syncDbStruct(DataSetFieldModel.class, session);
			esService.syncDbStruct(ResourceMappingModel.class, session);
			//1-建表(account,role,perm,rltroleaccount,rltroleperm)
			esService.syncDbStruct(AccountVO.class, session);
			esService.syncDbStruct(RoleVO.class, session);
			esService.syncDbStruct(PermVO.class, session);
			esService.syncDbStruct(RltRoleAccount.class, session);
			esService.syncDbStruct(RltRolePerm.class, session);
			//
			
			
			//
			//2-清理数据(如果是正式数据还是不要动的好，如果要动也是针对测试数据)
			StringBuilder sql = new StringBuilder("delete from pb_pf_rm where code=${code} ");
			commonService.executeChange(sql,JO.gen("code","rm_showcase"), session);
			sql = new StringBuilder("delete from pb_pf_ds where code=${code}");
			commonService.executeChange(sql,JO.gen("code","spring_role_test"), session);
			commonService.executeChange(sql,JO.gen("code","spring_perm_test"), session);
			//
			sql = new StringBuilder("delete from pb_pf_batch");
			commonService.executeChange(sql, session);
			commonService.save(new BatchVO(), null);//确保有一条数据
			//
			//3-初始化授权模型
			//FIXME 需要参考spring中resourceutils的实现
			String content = IOHelper.readByChar("data/rm/rm_1.data", "utf-8");
			JSONObject json = JSON.parseObject(content);
			json = json.getJSONObject("data");
			ResourceMappingModel model = JSON.toJavaObject(json, ResourceMappingModel.class);//维护一个资源映射模型
			//
			EntityModel em = ResourceMappingHelper.buildRelationEntityModel(model.relation_code);
			esService.syncDbStructByEntityModel(Arrays.asList(em), session);//
			//
			//制作testcase app的http://role/{app_code}数据集(为了简单起见，我就直接mock了)
			
			DataSetModel role_ds_model = read("data/dataset/spring_role_test.data","data",DataSetModel.class);
//			role_ds_model.id= IdGenHelper.genID("pb", role_ds_model.code);
			String ds_id_role = IdGenHelper.genID("pb", role_ds_model.code);
			commonService.save(role_ds_model, ds_id_role,null);
			//模拟一个指定app_code的角色列表
			DataSetModel perm_ds_model = read("data/dataset/spring_perm_test.data","data",DataSetModel.class);//模拟一个指定app_code的功能节点清单
//			perm_ds_model.id= IdGenHelper.genID("pb", perm_ds_model.code);
			String ds_id_perm = IdGenHelper.genID("pb", perm_ds_model.code);
			commonService.save(perm_ds_model, ds_id_perm,null);
			
			model.subject_id = ds_id_role;
			model.resource_id = ds_id_perm;
			commonService.save(model, null);
			//制作testcase app的perm的数据集(为了简单起见，我就直接mock了)
			//4-创建一个角色
			//5-创建一个虚拟的app
			//5-进行授权(为某角色授予权限)
			rmService.grant("rm_showcase", "testcase:admin@#@角色列表(测试)", Arrays.asList("perm:testcase:node_a@#@权限列表(测试)"), session);
			boolean flag = rmService.valid("rm_showcase", "testcase:admin@#@角色列表(测试)", "perm:testcase:node_a@#@权限列表(测试)", session);
			Assert.assertTrue(flag);
			//验证是否有权限rmService.valid("rm_showcase","testcase:admin","perm:testcase:node_a")
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
	/**
	 * 读取数据
	 * @param path
	 * @param pref
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	private <T> T read(String path,String pref,Class<T> clazz)throws Exception{
		String content = IOHelper.readByChar(path, "utf-8");
		JSONObject json = JSON.parseObject(content);
		json = json.getJSONObject(pref);
		T model = JSON.toJavaObject(json, clazz);//维护一个资源映射模型
		return model;
	}
}
