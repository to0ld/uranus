package testcase.vo;

import java.lang.reflect.Field;
import java.sql.Timestamp;

import net.popbean.pf.entity.IValueObjectWrapper;
import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.helper.EntityWrapperHelper;
import net.popbean.pf.entity.helper.JOHelper;
import net.popbean.pf.entity.helper.VOHelper;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.reflectasm.FieldAccess;

/**
 * 针对vo进行测试
 * 
 * @author to0ld
 *
 */
public class VOTestCase {
	static int LOOP = 100000000;// 10^9
	@Test(invocationCount=1)
	public void performanceForRead(){
		try {
			System.out.println("-----------------------------");
			AccountVO vo = new AccountVO();
			vo.code_account="cdoe";
			vo.domain = Domain.pk;
			long start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				String tmp = vo.code_account;// getter
				Domain domain = vo.domain;
			}
			long end = System.currentTimeMillis();
			System.out.println("getter/setter:" + (end - start));// 性能基线

			//asm style
			IValueObjectWrapper<AccountVO> wrapper = EntityWrapperHelper.wrapper(AccountVO.class);
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				String code_account = (String)wrapper.get(vo,"code_account");//getter
				Object domain = wrapper.get(vo,"domain");//getter
			}
			end = System.currentTimeMillis();
			System.out.println("asm style:" + (end - start));
			
			Field f = vo.getClass().getDeclaredField("code_account");
			Field f1 = vo.getClass().getDeclaredField("domain");
			f.setAccessible(true);// 加上确实快一些
			f1.setAccessible(true);
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				Object value = f.get(vo);// getter
				Object value1 = f1.get(vo);//getter
			}
			end = System.currentTimeMillis();
			System.out.println("jdk reflect:" + (end - start));

			//
			Class[] paramTypes = new Class[] {};
			Object[] args = new Object[] {};
			FastClass fc = FastClass.create(AccountVO.class);
			FastMethod read = fc.getMethod("getName", paramTypes);
			FastMethod read1 = fc.getMethod("getDomain", paramTypes);
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				Object value = read.invoke(vo, args);// getter
				Object value1 = read1.invoke(vo, args);// getter
			}
			end = System.currentTimeMillis();
			System.out.println("cglib:" + (end - start));

			// mybatis reflection
			MetaObject object = SystemMetaObject.forObject(vo);
			//
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				Object tmp = object.getValue("code_account");//getter
				Object tmp1 = object.getValue("domain");
			}
			end = System.currentTimeMillis();
			System.out.println("mybatis ref:" + (end - start));
			
			//json object
			JSONObject jo = new JSONObject();
			jo.put("code_account", "new_value");
			jo.put("domain", Domain.pk);
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				Object obj = jo.get("code_account");
				Object obj1 = jo.get("domain");
			}
			end = System.currentTimeMillis();
			System.out.println("jsonobject style(get):" + (end - start));
			//
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				String obj = jo.getString("code_account");
				Object obj1 = jo.get("domain");
			}
			end = System.currentTimeMillis();
			System.out.println("jsonobject style(getString):" + (end - start));
			//
			judge_reflectasm(LOOP);
		} catch (Exception e) {
			Assert.fail("soso", e);
		}
	}
	@Test
	public void judge_reflectasm(int loop) {
		try {
			AccountVO inst = new AccountVO();
			FieldAccess access = FieldAccess.get(AccountVO.class);
//			access.set(inst, "name", "Awesome McLovin");
			
			long start = System.currentTimeMillis();
			for (int i = 0; i < loop; i++) {
				Object name =  access.get(inst, "code_account");	
				name = access.get(inst, "domain");
			}
			long end = System.currentTimeMillis();
			System.out.println("reflectasm(" + loop + "):" + (end - start));
		} catch (Exception e) {
			Assert.fail("soso", e);
		}
	}
	@Test
	public void performanceForWrite() {
		try {
			AccountVO vo = new AccountVO();
			long start = System.currentTimeMillis();
			for (int i = 0, len = 0; i < LOOP; i++) {
				vo.code_account = "test";// setter
				VOHelper.cast(String.class, vo.code_account);//因为asm中有赋值的保护，所以，把其他的也补偿一个，以作为对比
			}
			long end = System.currentTimeMillis();
			System.out.println("getter/setter:" + (end - start));// 性能基线

			Field f = vo.getClass().getDeclaredField("code_account");
			f.setAccessible(true);// 加上确实快一些
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				f.set(vo, "test");// setter
				VOHelper.cast(String.class, vo.code_account);//因为asm中有赋值的保护，所以，把其他的也补偿一个，以作为对比
			}
			end = System.currentTimeMillis();
			System.out.println("ref:" + (end - start));

			//
			Class[] paramTypes_write = new Class[] { String.class };
			Object[] args_write = new Object[] { "test" };
			FastClass fc = FastClass.create(AccountVO.class);
			FastMethod write = fc.getMethod("setName", paramTypes_write);
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				write.invoke(vo, args_write);
				VOHelper.cast(String.class, vo.code_account);//因为asm中有赋值的保护，所以，把其他的也补偿一个，以作为对比
			}
			end = System.currentTimeMillis();
			System.out.println("cglib:" + (end - start));

			// mybatis reflection
			MetaObject object = SystemMetaObject.forObject(vo);
			//
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				String v = VOHelper.cast(String.class, "test_new");//因为asm中有赋值的保护，所以，把其他的也补偿一个，以作为对比
				object.setValue("code_account", v);//setter
			}
			end = System.currentTimeMillis();
			System.out.println("mybatis ref:" + (end - start));

			//asm style
			IValueObjectWrapper<AccountVO> wrapper = EntityWrapperHelper.wrapper(AccountVO.class);
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				wrapper.set(vo, "code_account", "test_new");//setter
			}
			end = System.currentTimeMillis();
			System.out.println("asm style:" + (end - start));
			
			//json object
			JSONObject jo = new JSONObject();
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				jo.put("code_account","test_new");
			}
			end = System.currentTimeMillis();
			System.out.println("jsonobject style:" + (end - start));
		} catch (Exception e) {
			Assert.fail("soso", e);
		}
	}
	/**
	 * 经过测试可以发现，asm的读很快，写的话，因为有类型转化的问题
	 */
	@Test
	public void performance() {
		try {
			AccountVO vo = new AccountVO();
			long start = System.currentTimeMillis();
			for (int i = 0, len = 0; i < LOOP; i++) {
//				vo.code_account = "test";// setter
				String tmp = vo.code_account;// getter
				Domain domain = vo.domain;
			}
			long end = System.currentTimeMillis();
			System.out.println("getter/setter:" + (end - start));// 性能基线

			Field f = vo.getClass().getDeclaredField("code_account");
			Field f1 = vo.getClass().getDeclaredField("domain");
			f.setAccessible(true);// 加上确实快一些
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				Object value = f.get(vo);// getter
				Object value1 = f1.get(vo);// getter
//				f.set(vo, "test");// setter
			}
			end = System.currentTimeMillis();
			System.out.println("ref:" + (end - start));

			//
			Class[] paramTypes = new Class[] {};
			Class[] paramTypes_write = new Class[] { String.class };
			Object[] args = new Object[] {};
			Object[] args_write = new Object[] { "test" };
			FastClass fc = FastClass.create(AccountVO.class);
			FastMethod read = fc.getMethod("getName", paramTypes);
			FastMethod write = fc.getMethod("setName", paramTypes_write);
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				read.invoke(vo, args);// getter
//				write.invoke(vo, args_write);
			}
			end = System.currentTimeMillis();
			System.out.println("cglib:" + (end - start));

			// mybatis reflection
			MetaObject object = SystemMetaObject.forObject(vo);
			//
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				Object tmp = object.getValue("code_account");//getter
//				object.setValue("code_account", "test_new");//setter
			}
			end = System.currentTimeMillis();
			System.out.println("mybatis ref:" + (end - start));

			//asm style
			IValueObjectWrapper<AccountVO> wrapper = EntityWrapperHelper.wrapper(AccountVO.class);
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				String code_account = (String)wrapper.get(vo,"code_account");//getter
//				wrapper.set(vo, "code_account", "test_new");//setter
			}
			end = System.currentTimeMillis();
			System.out.println("asm style:" + (end - start));
			//FIXME 需要增加pojo与json object之间读取数据的差异对比
			
		} catch (Exception e) {
			Assert.fail("soso", e);
		}
	}
	@Test
	public void test(){
		try {
			//
			Class clazz = Domain.class;
			System.out.println("enum.class:"+clazz.equals(Enum.class));
			
			//
			AccountVO target = new AccountVO();
			target.domain = Domain.pk;
			Object value = VOHelper.cast(Domain.class,"PK");
			//
			IValueObjectWrapper<AccountVO> wrapper = EntityWrapperHelper.wrapper(AccountVO.class);
			value = wrapper.get(target, "domain");
//			Domain domain = (Domain)wrapper.get(target,"domain" );
			System.out.println(value);
		} catch (Exception e) {
			Assert.fail("soso", e);
		}
	}
	@Test
	public void judge(){
		try {
			AccountVO vo = new AccountVO();
			vo.ts_crt = new Timestamp(System.currentTimeMillis());
			long start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				vo.code_account = "test";// setter
				String tmp = vo.code_account;// getter
				tmp = vo.memo_account;
//				Timestamp ts = vo.ts_crt;
			}
			long end = System.currentTimeMillis();
			System.out.println("getter/setter:" + (end - start));// 性能基线
			//
			//asm style
//			IValueObjectWrapper<AccountVO> wrapper = new AccountVOWrapperTpl();
			IValueObjectWrapper<AccountVO> wrapper = EntityWrapperHelper.wrapper(AccountVO.class);
//			IValueObjectWrapper<AccountVO> wrapper = new AccountVOWrapper();
//			wrapper.get(vo, "pk_account");

			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
				Object code_account = wrapper.get(vo,"pk_account");//getter
//				Object ts_crt = wrapper.get(vo, "ts_crt");
				code_account = wrapper.get(vo,"pk_account");
//				wrapper.set(vo, "code_account", "test_new");//setter
				Object domain = wrapper.get(vo,"domain");//getter
			}
			end = System.currentTimeMillis();
			System.out.println("asm style:" + (end - start));
			
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {//为啥两次执行，同样的代码差这么多呢？非要我归结于gc?
//				Object value = call(vo,"ts_crt");
				Object value = null;
				call1(vo,"domainx");
//				value = call(vo,"pk_account");
			}
			end = System.currentTimeMillis();
			System.out.println("call1(just equals):" + (end - start));

			
			//测试一下equals的性能
			start = System.currentTimeMillis();
			for (int i = 0; i < LOOP; i++) {
//				Object value = call(vo,"ts_crt");
//				Object value = call(vo,"pk_account");
				Object value = AccountVOWrapperTpl.call(vo,"pk_account");
//				value = call(vo,"pk_account");
				value = AccountVOWrapperTpl.call(vo,"pk_account");
			}
			end = System.currentTimeMillis();
			System.out.println("inner class mock get:" + (end - start));
		} catch (Exception e) {
			Assert.fail("soso", e);
		}
	}
	@Test
	public void judgeBatch(){
		for(int i=0;i<10;i++){
			judge();
		}
	}
	private Object call(AccountVO target,String key){
		if("pk_account".equals(key)){
			return target.pk_account;
		}
		if("code_account".equals(key)){
			return target.code_account;
		}
		if("date_birth".equals(key)){
			return target.date_birth ;
		}
		if("domain".equals(key)){
			return target.domain ;
		}
		if("money_account".equals(key)){
			return target.money_account ;
		}
		if("ts_crt".equals(key)){
			return target.ts_crt ;
		}
		if("i_stat".equals(key)){
			return target.i_stat ;
		}
		if("memo_account".equals(key)){
			return target.memo_account ;
		}
		if("domainx".equals(key)){
			return target.domainx ;
		}
		return null;
	}
	private void call1(AccountVO target,String key){//测试一下无返回的
		if("pk_account".equals(key)){
//			return target.pk_account;
			return ;
		}
		if("code_account".equals(key)){
//			return target.code_account;
			return ;
		}
		if("date_birth".equals(key)){
//			return target.date_birth ;
			return ;
		}
		if("domain".equals(key)){
//			return target.domain ;
			return ;
		}
		if("money_account".equals(key)){
//			return target.money_account ;
			return ;
		}
		if("ts_crt".equals(key)){
//			return target.ts_crt ;
			return ;
		}
		if("i_stat".equals(key)){
//			return target.i_stat ;
			return ;
		}
		if("memo_account".equals(key)){
//			return target.memo_account ;
			return ;
		}
		if("domainx".equals(key)){
//			return target.domainx ;
			return ;
		}
//		return null;
	}
//	private Object callStyle1(AccountVO target,String key){
//		
//	}
}
