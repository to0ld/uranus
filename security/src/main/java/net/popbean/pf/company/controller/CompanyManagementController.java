package net.popbean.pf.company.controller;

import net.popbean.pf.business.service.CommonBusinessService;
import net.popbean.pf.company.service.CompanyManagementBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.mvc.controller.BaseController;
import net.popbean.pf.security.vo.AccountVO;
import net.popbean.pf.security.vo.CompanyVO;
import net.popbean.pf.security.vo.SecuritySession;

import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
public class CompanyManagementController extends BaseController{
	@Autowired
	@Qualifier("service/company/management")
	CompanyManagementBusinessService cmService;
	//
	@Autowired
	@Qualifier("service/pf/common")
	CommonBusinessService commonService;
	//
	/**
	 * 安装应用(只有开发企业和运营企业有权限)
	 * 暂时先写死，如果要变更可以放到配置文件中(pf的安装配置中)
	 * @param appkey
	 * @param client
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("service/company/master/create")
	public String createMaster(@RequestParam("company_code") String code,
			@RequestParam("company_name") String name,
			@RequestParam("account_code") String account_code,
			@RequestParam("account_name") String account_name,
			@RequestParam(value="salt",required=false)String salt)throws Exception{
		//暂时先用一个数值做保护
		if(!"13707460987".equals(salt)){
			throw new Exception("无权执行安装");
		}
		//注册用户
		AccountVO account = new AccountVO();
		account.code = account_code;
		account.name = account_name;
		String salt_str = "fuckinganything";
		account.salt = salt_str;
		account.pwd = Md5Crypt.apr1Crypt("13707460987", salt_str);
//		account.pwd = Md5Crypt.md5Crypt("13707460987".getBytes(), salt_str);
		AccountVO tmp = commonService.find(new StringBuilder("select * from pb_bd_account where code=${code} "), JO.gen("code",account.code), AccountVO.class,null);
		if(tmp != null){
			throw new Exception("编码为"+account.code+"的用户已经存在");
		}
		commonService.save(account, account_code, null);
		//
		CompanyVO inst = new CompanyVO();
		inst.code = code;
		inst.name = name;
		inst.type = 3;
		//需要检测一下是否已经注册，否则就不能再注册了
		SecuritySession client = new SecuritySession();
		client.account_id = account_code;
		client.company = new CompanyVO();
		client.company.id = code;
		cmService.create(inst, client);
		return "success";
	}
	@ResponseBody
	@RequestMapping("service/company/tenant/create")
	public String createTenant(@RequestBody CompanyVO inst,@RequestParam(value="salt",required=false)String salt,SecuritySession client)throws Exception{
		//暂时先用一个数值做保护
		if(!"13701310963".equals(salt)){
			throw new Exception("无权执行安装");
		}
		cmService.create(inst, client);
		return "success";
	}
	@ResponseBody
	@RequestMapping("service/company/app/active/{appkey}")
	public String enableApp(@RequestParam("company_code") String company_code ,@PathVariable("appkey") String appkey,@RequestParam(value="salt",required=false)String salt,SecuritySession client)throws Exception{
		//暂时先用一个数值做保护
		if(!"13701310963".equals(salt)){
			throw new Exception("无权执行安装");
		}
		cmService.enableApp(company_code, appkey, client);
		return "success";
	}
}
