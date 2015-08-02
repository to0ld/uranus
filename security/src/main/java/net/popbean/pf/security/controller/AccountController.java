package net.popbean.pf.security.controller;

import net.popbean.pf.business.service.CommonBusinessService;
import net.popbean.pf.entity.model.FieldModel;
import net.popbean.pf.mvc.controller.BaseController;
import net.popbean.pf.security.vo.AccountVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 与用户有关的操作
 * -[] 注册
 * -[] 验证(实名；邮件；手机号)
 * -[] 指定条件查询用户
 * -[]  
 * @author to0ld
 *
 */
@Controller
public class AccountController extends BaseController{
	@Autowired
	@Qualifier("service/pf/common")
	CommonBusinessService commonService;
	//
	/**
	 * 注册
	 * @param account
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("service/pf/security/account/signup")
	public String signUp(@RequestBody AccountVO account)throws Exception{
		//先要过验证，不然会被刷死
		FieldModel code_fm = new FieldModel();
		code_fm.code = "code";
		commonService.save(account, account.code, new FieldModel[]{code_fm});
		return null;
	}
	
}
