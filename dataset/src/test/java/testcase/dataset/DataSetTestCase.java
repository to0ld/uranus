package testcase.dataset;

import net.popbean.pf.dataset.service.DataSetBusinessService;

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
 * 数据集测试 
 * @author to0ld
 *
 */
@ContextConfiguration({"classpath:spring/mvc.test.xml","classpath:spring/app.test.xml"})
@WebAppConfiguration("src/test/resources")
public class DataSetTestCase extends AbstractTestNGSpringContextTests{
	//
	@Autowired
	@Qualifier("service/pf/dataset")
	DataSetBusinessService dsService;
	//
	@Autowired
    private WebApplicationContext wac;
	//
	/**
	 * 
	 */
	public void install(){//安装一个数据集模型试试看
		
	}
	/**
	 * 
	 */
	@Test
	public void fetchModel(){//获取模型，读取数据
		try {
			//
			JSONObject body = new JSONObject();
			body.put("pageSize", 20);
			body.put("pageNo",1);
			//
			MockMvc mvc = MockMvcBuilders.webAppContextSetup(wac).build();
			MockHttpServletRequestBuilder req_builder = MockMvcRequestBuilders.get("/service/{appkey}/dataset/{ds_code}","cms","db_account");
			req_builder.param("with_data", "true");
			req_builder.contentType(MediaType.APPLICATION_JSON)  
	        .accept(MediaType.APPLICATION_JSON)
	        .content(JSON.toJSONString(body))
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
//	        .andExpect(MockMvcResultMatchers.content().string(expect)) // 校验是否是期望的结果  
	        ; 
		} catch (Exception e) {
			Assert.fail(e.getMessage(), e);
		}
	}
	public void fetchData(){
		try {
//			dsService.f
		} catch (Exception e) {
			Assert.fail(e.getMessage(), e);
		}
	}
}
