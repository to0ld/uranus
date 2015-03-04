package testcase.pf.mvc;

import net.popbean.pf.mvc.interceptor.access.AccessTokenInterceptor;
import net.popbean.pf.mvc.interceptor.access.TokenType;
import net.popbean.pf.testcase.TestHelper;

import org.apache.commons.lang3.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
public class TokenControllerTestCase extends AbstractTestNGSpringContextTests{
	@Autowired
    private WebApplicationContext wac;
	//
	private Object mockLogin(MockMvc mvc,TokenType type)throws Exception{
		MockHttpServletRequestBuilder req_builder = null;
		if(TokenType.Session.equals(type)){
			req_builder = MockMvcRequestBuilders.get("/testcase/simple/access/session/login");	
		}else{
			req_builder = MockMvcRequestBuilders.get("/testcase/simple/access/request/login");
		}
		req_builder.characterEncoding(CharEncoding.UTF_8);
		//
		MvcResult result = mvc.perform(req_builder)
		        .andDo(MockMvcResultHandlers.print()) // 打印整个请求与响应细节:MockMvcResultMatchers
		        .andExpect(MockMvcResultMatchers.view().name("hello"))
		        .andExpect(MockMvcResultMatchers.model().attributeExists(AccessTokenInterceptor.X_ACCESS_TOKEN))
		        .andReturn();
		        ;
		        //
		Assert.assertNotNull(result.getModelAndView().getModel().get(AccessTokenInterceptor.X_ACCESS_TOKEN));
		
		Object token = result.getModelAndView().getModel().get(AccessTokenInterceptor.X_ACCESS_TOKEN);
		return token;
	}
	private void mockAccessDataForSession(MockMvc mvc,String token)throws Exception{//session粒度的数据访问
		JSONObject param = new JSONObject();
		param.put("pk_param", "pk_value");
		
		MockHttpServletRequestBuilder req_builder = MockMvcRequestBuilders.post("/testcase/simple/access/session");
		req_builder.contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(param))  
        .accept(MediaType.APPLICATION_JSON)
        .header(AccessTokenInterceptor.X_ACCESS_TOKEN, token)
        .characterEncoding(CharEncoding.UTF_8)
        ;
		
		JSONObject jo = new JSONObject();
		jo.put("data", "pk_value");
		jo.put("status", 1);
		String expect = JSON.toJSONString(jo);
		mvc.perform(req_builder)
        .andDo(MockMvcResultHandlers.print()) // 打印整个请求与响应细节:MockMvcResultMatchers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.content().string(expect)) // 校验是否是期望的结果  
        ;
	}
	private void mockAccessDataForRequest(MockMvc mvc,String token)throws Exception{
		JSONObject param = new JSONObject();
		param.put("pk_param", "pk_value");
		//
		MockHttpServletRequestBuilder req_builder = MockMvcRequestBuilders.post("/testcase/simple/access/request");
		req_builder.contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(param))  
        .accept(MediaType.APPLICATION_JSON)
        .header(AccessTokenInterceptor.X_ACCESS_TOKEN, token)
        .characterEncoding(CharEncoding.UTF_8)
        ;
		
		JSONObject jo = new JSONObject();
		jo.put("data", "pk_value");
		jo.put("status", 1);
		String expect = JSON.toJSONString(jo);
		
		mvc.perform(req_builder)
        .andDo(MockMvcResultHandlers.print()) // 打印整个请求与响应细节:MockMvcResultMatchers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.content().string(expect)) // 校验是否是期望的结果  
        ;		
	}
	@Test
	public void testSessionType(){
		try {
			MockMvc mvc = MockMvcBuilders.webAppContextSetup(wac).build();
			//1-模拟一个登录行为，种下token:mockViewByGet()
			Object token = mockLogin(mvc,TokenType.Session);
			//2-模拟远程访问数据在步骤1的界面上)，带回token(session级验证)
			mockAccessDataForSession(mvc, token.toString());
			//

			//3-重复步骤2调用，验证是否抛出异常
			mockAccessDataForSession(mvc, token.toString());
			//4.0-模拟登录
			token = mockLogin(mvc,TokenType.Request);
			//4-模拟远程访问数据，带回token(request)
			mockAccessDataForRequest(mvc, token.toString());
			//5-
			JSONObject param = new JSONObject();
			param.put("pk_param", "pk_value");
			//
			MockHttpServletRequestBuilder req_builder = MockMvcRequestBuilders.post("/testcase/simple/access/request");
			req_builder.contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(param))  
	        .accept(MediaType.APPLICATION_JSON)
	        .header(AccessTokenInterceptor.X_ACCESS_TOKEN, token)
	        .characterEncoding(CharEncoding.UTF_8)
	        ;
			
			JSONObject jo = new JSONObject();
			jo.put("error", "无效访问，或者无标识头吧");
			jo.put("status", 0);
			String expect = JSON.toJSONString(jo);
			
			mvc.perform(req_builder)
	        .andDo(MockMvcResultHandlers.print()) // 打印整个请求与响应细节:MockMvcResultMatchers
	        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
	        .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
	        .andExpect(MockMvcResultMatchers.content().string(expect)) // 校验是否是期望的结果  
	        ;		
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
}
