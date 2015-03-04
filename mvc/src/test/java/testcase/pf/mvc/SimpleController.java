package testcase.pf.mvc;

import net.popbean.pf.entity.IValueObject;
import net.popbean.pf.entity.field.annotation.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import testcase.pf.business.service.HelloBusinessService;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("testcase/simple")
public class SimpleController {
	@Autowired
	@Qualifier("service/testcase/hello")
	private HelloBusinessService helloService;
	/**
	 * 
	 * @param id
	 * @return 
	 */
	@ResponseBody
	@RequestMapping("{id}")
	public String find(@PathVariable("id")String id){
		return id;
	}
	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("show/{id}")
	public ModelAndView view(String id){
		ModelAndView mav = new ModelAndView("hello");
		mav.addObject("key", "value1");
		return mav;
	}
	@ResponseBody
	@RequestMapping("custom")
	public String findCustom(@RequestBody CustomParam p){
		return p.pk_param;
	}
	@ResponseBody
	@RequestMapping("hello/{name}")
	public String findCustom(@PathVariable("name")String name){
		String ret = helloService.say(name);
		return name;
	}
	
	@Entity(code="pb_custom_param")
	public static class CustomParam implements IValueObject{

		/**
		 * 
		 */
		private static final long serialVersionUID = 9135706202615724762L;
		public String pk_param;
		public String param_code;
	}
}