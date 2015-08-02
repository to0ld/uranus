package testcase.security;

import java.util.regex.Matcher;

import net.popbean.pf.business.service.CommonBusinessService;
import net.popbean.pf.persistence.helper.DaoHelper;
import net.popbean.pf.security.vo.AccountVO;

import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 用户管理
 * 用户的类型分为
 * -[] 用户分类：普通用户
 * -[] 用户来源：自行注册；后台添加;老用户推荐
 * -[?] 特殊用户来源：oauth2
 * -[] 密码加盐存储:md5(pwd+salt)
 * @author to0ld
 */
@ContextConfiguration({"classpath:spring/mvc.test.xml","classpath:spring/app.test.xml"})
@WebAppConfiguration("src/test/resources")
public class AccountTestCase extends AbstractTestNGSpringContextTests{
	@Autowired
    private WebApplicationContext wac;
	@Autowired
	@Qualifier("service/pf/common")
	CommonBusinessService commonService;
	//
	@Test
	public void simple(){
		//FIXME 注册(成功)
		//FIXME 注册(失败)
	}
	@Test
	public void mockBrowser(){
		try {
			//注册(暂时不含kaptcha认证)
			signup();
			//sign in
			signin();
			//清除新注册用户：确保可以重复执行
		} catch (Exception e) {
			Assert.fail(e.getMessage(), e);
		}
	}
	/**
	 * 
	 */
	@Test
	public void signup(){
		try {
			//
			AccountVO param = new AccountVO();
			param.code = "tester";//要求使用邮箱或者手机号
			param.salt = "suckinganything";
			param.pwd = "123456";
			param.pwd = Md5Crypt.apr1Crypt(param.pwd, param.salt);
			//
			MockMvc mvc = MockMvcBuilders.webAppContextSetup(wac).build();
			MockHttpServletRequestBuilder req_builder = MockMvcRequestBuilders.get("/service/pf/security/account/signup","cms");
			req_builder.param("salt", "13701310963");
			req_builder.contentType(MediaType.APPLICATION_JSON)  
	        .accept(MediaType.APPLICATION_JSON)
	        .content(JSON.toJSONString(param))
	        .characterEncoding(CharEncoding.UTF_8);
			//
			JSONObject jo = new JSONObject();
			jo.put("data", param.code);//预期返回的是securityclient(带有token)
			jo.put("status", 1);
			jo.put("ip","127.0.0.1:80");
			String expect = JSON.toJSONString(jo);
			//
			ResultActions ra = mvc.perform(req_builder)
	        .andDo(MockMvcResultHandlers.print()) // 打印整个请求与响应细节:MockMvcResultMatchers
	        .andExpect(MockMvcResultMatchers.status().isOk())
//	        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))//FIXME 艹是谁加上的utf-8
	        .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
	        .andExpect(MockMvcResultMatchers.content().string(expect)) // 校验是否是期望的结果  
	        ; 
			System.out.println(ra);
		} catch (Exception e) {
			Assert.fail(e.getMessage(), e);
		}
	}
	/**
	 * 登陆认证
	 */
	@Test
	public void signin(){
		try {
			//
//			AccountVO param = new AccountVO();
//			param.code = "tester";//要求使用邮箱或者手机号
//			param.salt = "suckinganything";
//			param.pwd = "123456";
//			param.pwd = Md5Crypt.apr1Crypt(param.pwd, param.salt);
			JSONObject param = new JSONObject();
			param.put("account_code", "tester");
			param.put("pwd","123456");
			//
			MockMvc mvc = MockMvcBuilders.webAppContextSetup(wac).build();
			MockHttpServletRequestBuilder req_builder = MockMvcRequestBuilders.get("/service/pf/security/account/signin","cms");
			req_builder.param("account_code", "tester");
			req_builder.param("pwd", "123456");
			req_builder.contentType(MediaType.APPLICATION_JSON)  
	        .accept(MediaType.APPLICATION_JSON)
//	        .content(JSON.toJSONString(param))
	        .characterEncoding(CharEncoding.UTF_8);
			//
			JSONObject jo = new JSONObject();
			jo.put("data", "tester");//预期返回的是securityclient(带有token)
			jo.put("status", 1);
			jo.put("ip","127.0.0.1:80");
			String expect = JSON.toJSONString(jo);
			//
			ResultActions ra = mvc.perform(req_builder)
	        .andDo(MockMvcResultHandlers.print()) // 打印整个请求与响应细节:MockMvcResultMatchers
	        .andExpect(MockMvcResultMatchers.status().isOk())
//	        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))//FIXME 艹是谁加上的utf-8
	        .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//	        .andExpect(MockMvcResultMatchers.content().string(expect)) // 校验是否是期望的结果  
	        ; 
			System.out.println(ra);
		} catch (Exception e) {
			Assert.fail(e.getMessage(), e);
		}
	}
	/**
	 * 测试一下特殊字符的替换问题
	 * @throws Exception
	 */
	@Test
	public void matcher()throws Exception{
		String sql = " insert into pb_bd_account(code,pwd,salt,id,status) values(${code},${pwd},${salt},${id},${status})";
		JSONObject jo = new JSONObject();
		jo.put("pwd", Matcher.quoteReplacement("$apr1$suckinga$eRTwlXt.2r6/gMSUdaSbN1"));
		
		System.out.println(DaoHelper.parseMsg(new StringBuilder(sql),jo));
//		{"code":"tester","salt":"suckinganything","id":"tester","pwd":"$apr1$suckinga$eRTwlXt.2r6/gMSUdaSbN1","status":5}
		String x="abc$$abc";
		System.out.println("-->"+Matcher.quoteReplacement(x).replaceAll("$$", "Amaan"));
		String y=x.replaceAll("$$", "Amaan"); 
		System.out.println("-->"+y+"<--");
//		DaoHelper.parseMsg(sql, vo)
	}
}
