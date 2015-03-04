package testcase.pf.mvc;


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

/**
 * - [] 简单测试(controller,interceptor)
 * - [] get/post
 * - [] header
 * - [] response(view/json)
 * - [] dance with bean/service
 * @author to0ld
 *
 */
@ContextConfiguration({"classpath:spring/mvc.test.xml","classpath:spring/app.test.xml"})
@WebAppConfiguration("src/test/resources")
public class ControllerTestCase extends AbstractTestNGSpringContextTests{
	@Autowired
    private WebApplicationContext wac;
	/**
	 * 
	 */
	@Test
	public void simpleget(){
		try {
			MockMvc mvc = MockMvcBuilders.webAppContextSetup(wac).build();
			MockHttpServletRequestBuilder req_builder = MockMvcRequestBuilders.get("/testcase/simple/{id}","233");
			req_builder.contentType(MediaType.APPLICATION_JSON)  
	        .accept(MediaType.APPLICATION_JSON)  
	        .characterEncoding(CharEncoding.UTF_8)  
	        ;
			JSONObject jo = new JSONObject();
			jo.put("data", "233");
			jo.put("status", 1);
			String expect = JSON.toJSONString(jo);
			mvc.perform(req_builder)
	        .andDo(MockMvcResultHandlers.print()) // 打印整个请求与响应细节:MockMvcResultMatchers
	        .andExpect(MockMvcResultMatchers.status().isOk())
//	        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))//FIXME 艹是谁加上的utf-8
	        .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
	        .andExpect(MockMvcResultMatchers.content().string(expect)) // 校验是否是期望的结果  
	        ;
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
	@Test
	public void simplegetWithService(){//测试带有service call的controller(本质上这是在测试配置文件有效性)
		try {
			MockMvc mvc = MockMvcBuilders.webAppContextSetup(wac).build();
			MockHttpServletRequestBuilder req_builder = MockMvcRequestBuilders.get("/testcase/simple/hello/{name}","233");
			req_builder.contentType(MediaType.APPLICATION_JSON)  
	        .accept(MediaType.APPLICATION_JSON)  
	        .characterEncoding(CharEncoding.UTF_8)  
	        ;
			JSONObject jo = new JSONObject();
			jo.put("data", "233");
			jo.put("status", 1);
			String expect = JSON.toJSONString(jo);
			mvc.perform(req_builder)
	        .andDo(MockMvcResultHandlers.print()) // 打印整个请求与响应细节:MockMvcResultMatchers
	        .andExpect(MockMvcResultMatchers.status().isOk())
//	        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))//FIXME 艹是谁加上的utf-8
	        .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
	        .andExpect(MockMvcResultMatchers.content().string(expect)) // 校验是否是期望的结果  
	        ;
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
	@Test
	public void simplepost(){//测试post method
		try {
			//
			JSONObject param = new JSONObject();
			param.put("pk_param", "pk_value");
			//
			MockMvc mvc = MockMvcBuilders.webAppContextSetup(wac).build();
			MockHttpServletRequestBuilder req_builder = MockMvcRequestBuilders.post("/testcase/simple/custom");
			req_builder.contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(param))  
	        .accept(MediaType.APPLICATION_JSON)
	        .characterEncoding(CharEncoding.UTF_8)
	        ;
			JSONObject jo = new JSONObject();
			jo.put("data", "pk_value");
			jo.put("status", 1);
			String expect = JSON.toJSONString(jo);
			mvc.perform(req_builder)
	        .andDo(MockMvcResultHandlers.print()) // 打印整个请求与响应细节:MockMvcResultMatchers
	        .andExpect(MockMvcResultMatchers.status().isOk())
//	        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))//FIXME 艹是谁加上的utf-8
	        .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
	        .andExpect(MockMvcResultMatchers.content().string(expect)) // 校验是否是期望的结果  
	        ;
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
	@Test
	public void simpleshow(){//对页面进行测试
		try {
			MockMvc mvc = MockMvcBuilders.webAppContextSetup(wac).build();
			MockHttpServletRequestBuilder req_builder = MockMvcRequestBuilders.get("/testcase/simple/show/{id}","233");
//			req_builder.contentType(MediaType.APPLICATION_JSON)  
//	        .accept(MediaType.APPLICATION_JSON)  
//	        .characterEncoding(CharEncoding.UTF_8)  
//	        ;
			req_builder.characterEncoding(CharEncoding.UTF_8);
			//
			JSONObject jo = new JSONObject();
			jo.put("data", "233");
			jo.put("status", 1);
			MvcResult result = mvc.perform(req_builder)
	        .andDo(MockMvcResultHandlers.print()) // 打印整个请求与响应细节:MockMvcResultMatchers
	        .andExpect(MockMvcResultMatchers.view().name("hello"))
	        .andExpect(MockMvcResultMatchers.model().attributeExists("key"))
	        .andReturn();
	        ;
	        //
	        Assert.assertNotNull(result.getModelAndView().getModel().get("key"));
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}

		
	}
}
