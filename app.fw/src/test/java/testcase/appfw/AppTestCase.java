package testcase.appfw;

import net.popbean.pf.business.service.CommonBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.mvc.bind.support.SessionArgumentResolver;
import net.popbean.pf.security.vo.AccountVO;
import net.popbean.pf.security.vo.CompanyVO;
import net.popbean.pf.security.vo.SecuritySession;

import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
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

@ContextConfiguration({"classpath:spring/mvc.test.xml","classpath:spring/app.test.xml"})
@WebAppConfiguration("src/test/resources")
public class AppTestCase extends AbstractTestNGSpringContextTests{
	//
	@Autowired
    private WebApplicationContext wac;
	@Autowired
	@Qualifier("service/pf/common")
	CommonBusinessService commonService;
	//
	@Test
	public void simple(){
		try {
			//安装应用(以cms为例)：在线安装以提供一个url为基准，比如:service/app/install/cms 这个链接需要予以保护，比如加个参数mobile=13701310963
			//
			MockMvc mvc = MockMvcBuilders.webAppContextSetup(wac).build();
			MockHttpServletRequestBuilder req_builder = MockMvcRequestBuilders.get("/service/app/install/{appkey}","cms");
			req_builder.param("salt", "13701310963");
			req_builder.contentType(MediaType.APPLICATION_JSON)  
	        .accept(MediaType.APPLICATION_JSON)  
	        .characterEncoding(CharEncoding.UTF_8);
			//
			JSONObject jo = new JSONObject();
			jo.put("data", "cms");
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
			//
			//FIXME -----------------------------------------第一个企业：运维方(与第一个租户不同的地方是啥？)
			// call:service/company/create(保存数据；往哪安装？)
//			createMaster();
			//-------------------------------------------------------------
			//FIXME 第一个租户
//			createTenant();
			//FIXME 创建企业(以运营方为例):service/company/create(post data+with cookies) -> service/org/active
			//1-创建一个用户
			//2-模拟这个用户登录，创建一个企业
			//FIXME 为本例创建的企业激活一个应用:service/app/active/cms(身份由cookie来识别)
			//1-激活一个应用
		} catch (Exception e) {
			Assert.fail(e.getMessage(), e);
		}
	}
	@Test
	public void createMaster(){//注册运维方企业
		try {
			MockMvc mvc = MockMvcBuilders.webAppContextSetup(wac).build();
			MockHttpServletRequestBuilder req_builder = MockMvcRequestBuilders.get("/service/company/master/create","cms");
			req_builder.param("company_code", "root");
			req_builder.param("company_name", "运营平台");
			req_builder.param("account_code", "admin");
			req_builder.param("account_name", "admin");
			req_builder.param("pwd", "13707460987");
			req_builder.param("salt", "13707460987");
			req_builder.contentType(MediaType.APPLICATION_JSON)  
	        .accept(MediaType.APPLICATION_JSON)  
	        .characterEncoding(CharEncoding.UTF_8);
			//
			JSONObject jo = new JSONObject();
			jo.put("data", "success");
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
		} catch (Exception e) {
			Assert.fail(e.getMessage(), e);
		}
	}
	@Test
	public void createTenant(){//注册租户企业
		//前台传入数据，构成companyvo
		try {
			//为新创建的企业，增加一个模拟的用户
//			AccountVO account = new AccountVO();
//			account.code = "account";
//			account.name = "account";
//			String salt_str = "fuckinganything";
//			account.salt = salt_str;
//			account.pwd = Md5Crypt.apr1Crypt("13707460987", salt_str);
////			account.pwd = Md5Crypt.md5Crypt("13707460987".getBytes(), salt_str);
//			AccountVO tmp = commonService.find(new StringBuilder("select * from pb_bd_account where code=${code} "), JO.gen("code",account.code), AccountVO.class,null);
//			if(tmp != null){
//				throw new Exception("编码为"+account.code+"的用户已经存在");
//			}
//			commonService.save(account, null);
			SecuritySession client = new SecuritySession();
			client.account_code = "admin";
			client.account_id = "admin";
			client.company = new CompanyVO();
			client.company.id = "root";
			MockHttpSession session = new MockHttpSession();
			session.setAttribute(SessionArgumentResolver.TOKEN, client);
			//
			JSONObject company = new JSONObject();
			company.put("name", "元之园");
			company.put("code","yzy");
			//
			MockMvc mvc = MockMvcBuilders.webAppContextSetup(wac).build();
			MockHttpServletRequestBuilder req_builder = MockMvcRequestBuilders.get("/service/company/tenant/create","cms");
			req_builder.session(session);//提供模拟的登陆变量
			req_builder.param("salt", "13701310963");
			req_builder.contentType(MediaType.APPLICATION_JSON)  
	        .accept(MediaType.APPLICATION_JSON)
	        .content(JSON.toJSONString(company))
	        .characterEncoding(CharEncoding.UTF_8);
			//
			JSONObject jo = new JSONObject();
			jo.put("data", "success");
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
		} catch (Exception e) {
			Assert.fail(e.getMessage(), e);
		}
	}
	@Test
	public void enableCMS(){//注册企业激活应用
		//前台传入数据，构成companyvo
		try {
			//
			//
			MockMvc mvc = MockMvcBuilders.webAppContextSetup(wac).build();
			MockHttpServletRequestBuilder req_builder = MockMvcRequestBuilders.get("/service/company/app/active/{appkey}","cms");
			req_builder.param("salt", "13701310963");
			req_builder.param("company_code", "yzy");
			req_builder.contentType(MediaType.APPLICATION_JSON)  
	        .accept(MediaType.APPLICATION_JSON)
	        .characterEncoding(CharEncoding.UTF_8);
			//
			JSONObject jo = new JSONObject();
			jo.put("data", "success");
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
		} catch (Exception e) {
			Assert.fail(e.getMessage(), e);
		}
	}
	@Test
	public void enumTest(){
//		System.out.println(SourceType.Groovy.toString());
	}
}
