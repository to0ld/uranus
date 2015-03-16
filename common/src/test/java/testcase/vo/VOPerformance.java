package testcase.vo;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import net.popbean.pf.entity.field.Domain;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMember;
import net.sf.cglib.reflect.FastMethod;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.esotericsoftware.reflectasm.FieldAccess;

//import com.esotericsoftware.reflectasm.FieldAccess;

/**
 * 结构性测试
 * 这可以堪称一个悲伤的故事
 * if-else if的嵌套解决不了问题
 * @author to0ld
 *
 */
public class VOPerformance {
	
	public void judge_reflectasm(int loop) {//评估reflectasm的性能
		try {
			TestVO inst = new TestVO();
			FieldAccess access = FieldAccess.get(TestVO.class);
//			access.set(inst, "name", "Awesome McLovin");
			
			long start = System.currentTimeMillis();
			for (int i = 0; i < loop; i++) {
				String name = (String) access.get(inst, "domainx");
				access.set(inst,"domainx","new_value");
			}
			long end = System.currentTimeMillis();
			System.out.println("reflectasm(" + loop + "):" + (end - start));
		} catch (Exception e) {
			Assert.fail("soso", e);
		}
	}
	public void judge_reflect(int loop){
		try {
			TestVO inst = new TestVO();
			Field f = TestVO.class.getDeclaredField("code_account");
			Field f1 = TestVO.class.getDeclaredField("domain");
			f.setAccessible(true);// 加上确实快一些
			f1.setAccessible(true);
			long start = System.currentTimeMillis();
			for (int i = 0; i < loop; i++) {
				Object value = f.get(inst);// getter
				Object value1 = f1.get(inst);//getter
			}
			long end = System.currentTimeMillis();
			System.out.println("jdk reflect(" + loop + "):" + (end - start));
		} catch (Exception e) {
			Assert.fail("soso", e);
		}
	}
	/**
	 * 如果函数没有返回，性能要比有返回值的好很多，这是为啥呢 
	 */
	@Test
	public void performance() {
		int loop = 100000000;// 10^9
		fieldAccess(loop);
		//equals只是比较无返回；get是先equals，然后返回；return是
		judge_equals(loop);
		judge_get(loop);//模拟beanwrapper的get
		judge_return(loop);//连续判断，最后返回一个
		judge_reflectasm(loop);//评估reflectasm的性能
		judge_reflect(loop);//利用jdk反射
	}

	/**
	 * 直接通过公开属性进行get/set处理(已知的性能极限)
	 * 
	 * @param loop
	 */
	public void fieldAccess(int loop) {
		TestVO inst = new TestVO();
		Timestamp current = new Timestamp(System.currentTimeMillis());
		long start = System.currentTimeMillis();
		for (int i = 0; i < loop; i++) {
			inst.code_account = ";";
			inst.ts_crt = current;
		}
		long end = System.currentTimeMillis();
		System.out.println("field access(" + loop + "):" + (end - start));
	}

	public void judge_equals(int loop) {
		TestVO inst = new TestVO();
		Timestamp current = new Timestamp(System.currentTimeMillis());
		long start = System.currentTimeMillis();
		for (int i = 0; i < loop; i++) {
			equals(inst, "domainx");
			
		}
		long end = System.currentTimeMillis();
		System.out.println("inner class->equals(" + loop + "):" + (end - start));
	}

	public void judge_get(int loop) {
		TestVO inst = new TestVO();
		Timestamp current = new Timestamp(System.currentTimeMillis());
		long start = System.currentTimeMillis();
		for (int i = 0; i < loop; i++) {
			get(inst, "domainx");
		}
		long end = System.currentTimeMillis();
		System.out.println("inner class->get(" + loop + "):" + (end - start));
	}

	public void judge_return(int loop) {
		TestVO inst = new TestVO();
		Timestamp current = new Timestamp(System.currentTimeMillis());
		long start = System.currentTimeMillis();
		for (int i = 0; i < loop; i++) {
			justreturn(inst, "domainx");
		}
		long end = System.currentTimeMillis();
		System.out.println("inner class->just return(" + loop + "):" + (end - start));
	}

	/**
	 * 模拟wrapper中get的主体内容进行测评 outer class
	 * 
	 * @param target
	 * @param key
	 */
	public void equals(TestVO target, String key) {// 测试一下无返回的
		if ("pk_account".equals(key)) {
			return;
		}
		if ("code_account".equals(key)) {
			return;
		}
		if ("date_birth".equals(key)) {
			return;
		}
		if ("domain".equals(key)) {
			return;
		}
		if ("money_account".equals(key)) {
			return;
		}
		if ("ts_crt".equals(key)) {
			return;
		}
		if ("i_stat".equals(key)) {
			return;
		}
		if ("memo_account".equals(key)) {
			return;
		}
		if ("domainx".equals(key)) {
			return;
		}
	}

	/**
	 * 模拟wrapper中的get逻辑 与equals的主要差别就是返回(因为读取field通过fieldaccess验证了，可以拿到数据)
	 * 
	 * @param target
	 * @param key
	 * @return
	 */
	public Object get(TestVO target, String key) {
		if ("pk_account".equals(key)) {
			return target.pk_account;
		}
		if ("code_account".equals(key)) {
			return target.code_account;
		}
		if ("date_birth".equals(key)) {
			return target.date_birth;
		}
		if("domain".equals(key)){
			return target.domain;
		}
		if ("money_account".equals(key)) {
			return target.money_account;
		}
		if ("ts_crt".equals(key)) {
			return target.ts_crt;
		}
		if ("i_stat".equals(key)) {
			return target.i_stat;
		}
		if ("memo_account".equals(key)) {
			return target.memo_account;
		}
		if ("domainx".equals(key)) {
			return target.domainx;
		}
		return null;
	}

	/**
	 * 模拟getter方法(简化返回模式)
	 * 
	 * @param target
	 * @return
	 */
	public Object justreturn(TestVO target, String key) {
		//FIXME 如果每一个if的block中进行，反而会更加慢
		if ("pk_account".equals(key)) {
		}
		if ("code_account".equals(key)) {
		}
		if ("date_birth".equals(key)) {
		}
		if ("domain".equals(key)) {
		}
		if ("money_account".equals(key)) {
		}
		if ("ts_crt".equals(key)) {
		}
		if ("i_stat".equals(key)) {
		}
		if ("memo_account".equals(key)) {
		}
		if ("domainx".equals(key)) {
		}
		return target.code_account;
	}
	public static class TestVO implements Serializable,Cloneable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 6182093880508822571L;
		public String pk_account;
		
		public String code_account;
		
		public BigDecimal money_account;
		
		public Date date_birth;
		
		public Timestamp ts_crt;
		
		public Integer i_stat;
		
		public String memo_account;

		public String domainx;
		public Domain domain;
		//
		public void setName(String value){
			this.code_account = value;
			return ;
		}
		public String getName(){
			return this.code_account;
		}
		public TestVO(){
			super();
		}
	}
}
