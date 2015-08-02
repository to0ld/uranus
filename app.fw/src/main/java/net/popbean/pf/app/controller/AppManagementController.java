package net.popbean.pf.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.popbean.pf.app.service.AppManagementBusinessService;
import net.popbean.pf.mvc.controller.BaseController;
import net.popbean.pf.security.vo.SecuritySession;
@Controller
public class AppManagementController extends BaseController{
	@Autowired
	@Qualifier("service/pf/am")
	AppManagementBusinessService amService;
	/**
	 * 安装应用(只有开发企业和运营企业有权限)
	 * @param appkey
	 * @param client
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("service/app/install/{appkey}")
	public String install(@PathVariable("appkey")String appkey,@RequestParam(value="salt",required=false)String salt,SecuritySession client)throws Exception{
		//暂时先用一个数值做保护
		if(!"13701310963".equals(salt)){
			throw new Exception("无权执行安装");
		}
		amService.install(appkey, client);
		return appkey;
	}
}
