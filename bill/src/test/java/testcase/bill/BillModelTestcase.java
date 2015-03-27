package testcase.bill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.popbean.pf.bill.service.BillDataBusinessService;
import net.popbean.pf.bill.service.BillModelBusinessService;
import net.popbean.pf.bill.vo.BillModel;
import net.popbean.pf.business.service.CommonBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.model.EntityModel;
import net.popbean.pf.entity.model.FieldModel;
import net.popbean.pf.entity.model.RelationModel;
import net.popbean.pf.entity.service.EntityStructBusinessService;
import net.popbean.pf.helper.IOHelper;
import net.popbean.pf.security.vo.SecuritySession;
import net.popbean.pf.testcase.TestHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.internal.remote.SlavePool;

import testcase.bill.vo.DirectSlaveVO;
import testcase.bill.vo.MasterVO;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


@ContextConfiguration(locations={"classpath:/spring/app.test.xml"})
public class BillModelTestcase extends AbstractTestNGSpringContextTests{
	//
	@Autowired
	@Qualifier("service/pf/entity/struct")
	EntityStructBusinessService esService;
	@Autowired
	@Qualifier("service/pf/common")
	CommonBusinessService commonService;
	@Autowired
	@Qualifier("service/pf/bill/model/redis")
	BillModelBusinessService bmService;
	@Autowired
	@Qualifier("service/pf/bill/data")
	BillDataBusinessService bdService;
	//
	private SecuritySession mockLogin(){
		SecuritySession session = new SecuritySession();
		return session;
	}
	@Test
	public void temp_create(){
		try {
			SecuritySession session = mockLogin();
			esService.syncDbStruct(EntityModel.class, session);
			esService.syncDbStruct(FieldModel.class, session);
			//
			esService.syncDbStruct(MasterVO.class, session);
			esService.impByEntityClass(Arrays.asList(MasterVO.class.getName()), false,session);//如果true那就只要执行一个
//			esService.syncEntityModel(Arrays.asList(a), session);
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
	private void impByEntity(List<String> clazz_list)throws Exception{
		SecuritySession session = mockLogin();
		esService.impByEntityClass(clazz_list, true,session);//如果true那就只要执行一个
	}
	private <T> T load(String path,String pref,Class<T> clazz)throws Exception{
		String content = IOHelper.readByChar(path, "utf-8");
		JSONObject jo = JSON.parseObject(content);
		T model = jo.getObject(pref, clazz);
		return model; 
	}
	/**
	 * 单表，无事件，无默认值
	 */
	@Test
	public void simple(){
		try {
			SecuritySession session = mockLogin();
			//imp(List<clazz>)
			esService.syncDbStruct(EntityModel.class, session);
			esService.syncDbStruct(FieldModel.class, session);
			//
			esService.syncDbStruct(MasterVO.class, session);
			esService.impByEntityClass(Arrays.asList(MasterVO.class.getName()), false,session);//如果true那就只要执行一个
			//
			//1-制作一个简单的bill model(单表)->load(path,pref,billmodel)
			BillModel model = load("data/bill/simple.data","data",BillModel.class);
			Assert.assertNotNull(model);
			//1.1-定义一个实体:test_bill_master:id,code,name,account_crt_ref,status
			//1.2-根据entitymodel转化出一个billmodel
			//1.3-保存
			bmService.save(model, session);
			//2-读取模型
			//读取模型，带数据
			BillModel inst = bmService.find(model.code, model.stage, false, session);
			Assert.assertNotNull(inst);
			Assert.assertEquals(inst.code, model.code,"不得行呀");//FIXME 如果对比两个单据是否相同呢。。。
			//performance：循环个1000次，看看时间
			//删除模型:注意清除cache
			int LOOP = 10000;
			long start = System.currentTimeMillis();
			for(int i=0;i<LOOP;i++){
				inst = bmService.find(model.code, model.stage, false, session);
			}
			long end = System.currentTimeMillis();
			System.out.println("loop="+LOOP+";total="+(end-start)+";peer="+((float)(end-start)/LOOP));
			//bill data处理
			//模拟requestbody得到一个jsonobject
			//
			esService.syncDbStruct(MasterVO.class, session);
			StringBuilder sql = new StringBuilder("delete from test_bill_master");
			commonService.executeChange(sql, session);
			//
			JSONObject data = JO.gen("code","code_value_1","memo","memo...");
			String id = bdService.save(model.code, model.stage, true, data, session);
			JSONObject ret = bdService.findBillData(model.code, id, true, session);
			Assert.assertNotNull(ret);
			Assert.assertEquals(ret.get("code"), data.get("code"),"红刀子进白刀子出，可不好");
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
	@Test
	public void simpleWithEvent(){//带有事件变化
		try {
			//制作一个简单的bill model(单表)
			//读取模型
			//读取模型，带数据
			//performance
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
	@Test
	public void simpleWithDefValue(){//带有默认值
		try {
			//制作一个简单的bill model(单表)
			//读取模型
			//读取模型，带数据
			//performance
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
	@Test
	public void complexWithFileUpload(){//带附件上传的复合模型
		
	}
	@Test
	public void complex(){
		try {
			SecuritySession session = mockLogin();
			//0-制表,生成entity meta，relation meta
			impByEntity(Arrays.asList(
					EntityModel.class.getName(),
					FieldModel.class.getName(),
					RelationModel.class.getName(),
					MasterVO.class.getName(),
					DirectSlaveVO.class.getName()
			));
			//1-读取bill model
			BillModel model = load("data/bill/complex.data","data",BillModel.class);
			Assert.assertNotNull(model);
			//2-保存bill model
			bmService.save(model, session);
			//3-读取bill model(不带数据)
			BillModel inst = bmService.find(model.code, model.stage, false, session);
			Assert.assertNotNull(inst);
			Assert.assertEquals(inst.code, model.code,"不得行呀");//FIXME 如果对比两个单据是否相同呢。。。
			//4-bill model performance
			int LOOP = 10000;
			long start = System.currentTimeMillis();
			for(int i=0;i<LOOP;i++){
				inst = bmService.find(model.code, model.stage, false, session);
			}
			long end = System.currentTimeMillis();
			System.out.println("loop="+LOOP+";total="+(end-start)+";peer="+((float)(end-start)/LOOP));
			//5.1-清除bill data数据
			StringBuilder sql = new StringBuilder("delete from test_bill_master");
			commonService.executeChange(sql, session);
			sql = new StringBuilder("delete from test_bill_ds");
			commonService.executeChange(sql, session);
			//5.2-save bill data数据
			JSONObject data = JO.gen("code","code_value_1","memo","memo...");
			List<JSONObject> slaves = new ArrayList<>();
			JSONObject child = JO.gen("code","s_code_value_1","memo","s_memo...");
			slaves.add(child);
			data.put("test_bill_ds", slaves);
			String id = bdService.save(model.code, model.stage, true, data, session);
			//5.3-find bill data数据
			JSONObject ret = bdService.findBillData(model.code, id, true, session);
			Assert.assertNotNull(ret);
			Assert.assertEquals(ret.get("code"), data.get("code"),"红刀子进白刀子出，可不好");
			Assert.assertNotNull(ret.get("test_bill_ds"),"小样，子集数据还是没出来吧");
			//也得看看子集是不是出来了吧
			start = System.currentTimeMillis();
			for(int i=0;i<LOOP;i++){
				JSONObject tmp = bdService.findBillData(model.code, id, true, session);
			}
			end = System.currentTimeMillis();
			System.out.println("bill data find:loop="+LOOP+";total="+(end-start)+";peer="+((float)(end-start)/LOOP));
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
}
