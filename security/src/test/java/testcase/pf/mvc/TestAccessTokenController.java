package testcase.pf.mvc;

import net.popbean.pf.mvc.interceptor.access.AccessToken;
import net.popbean.pf.mvc.interceptor.access.TokenType;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;


@Controller
@RequestMapping("testcase/simple")
public class TestAccessTokenController {
	/**
	 * 
	 * @return
	 */
	@RequestMapping("access/session/login")
	@AccessToken
	public ModelAndView view(){
		ModelAndView mav = new ModelAndView("hello");
		return mav;
	}
	/**
	 * 
	 * @return
	 */
	@RequestMapping("access/request/login")
	@AccessToken(type=TokenType.Request)
	public ModelAndView viewByRequest(){
		ModelAndView mav = new ModelAndView("hello");
		return mav;
	}
	@ResponseBody
	@RequestMapping("access/session")
	@AccessToken
	public String session(@RequestBody JSONObject p){
		return p.getString("pk_param");
	}
	@ResponseBody
	@RequestMapping("access/request")
	@AccessToken(type=TokenType.Request)
	public String request(@RequestBody JSONObject p){
		return p.getString("pk_param");
	}
}
