package net.popbean.pf.bill.controller;

import java.util.List;

import net.popbean.pf.bill.service.BillDataBusinessService;
import net.popbean.pf.business.service.CommonBusinessService;
import net.popbean.pf.entity.helper.JOHelper;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.mvc.controller.BaseController;
import net.popbean.pf.security.vo.SecuritySession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
@Controller
@RequestMapping(value = "/service/{appkey}/bill")
public class BillDataController extends BaseController{
	@Autowired
	@Qualifier("service/pf/common")
	CommonBusinessService commonService;
	@Autowired
	@Qualifier("service/pf/bill/data")
	BillDataBusinessService bdService;
	//
	@ResponseBody
	@RequestMapping("/data/{bill_code}/save")
	public String save(@PathVariable("bill_code") String bill_code,
			@RequestParam(value="stage",defaultValue="")String stage,
			@RequestBody JSONObject data, SecuritySession client) throws Exception {
		try {
//			commonService.lockData("c:"+bill_code, client.account_id);
			//
//			if (client == null) {
//				LoginHelper.redirectLogin(request, response);
//				return null;
//			}
			return bdService.save(bill_code, stage, true, data, client);
		} catch (Exception e) {
			throw e;
		}finally{
//			commonService.unlockData("c:"+bill_code, client.login_pk_acc);
		}
	}

	@ResponseBody
	@RequestMapping("/data/{bill_code}/delete/{pk_list}")
	public String delete(@PathVariable("bill_code") String bill_code, @PathVariable("pk_list") List<String> pk_list, SecuritySession client) throws Exception {
//		if (client == null) {
//			LoginHelper.redirectLogin(request, response);
//			return null;
//		}
		bdService.delete(bill_code, pk_list, client);
		return "success";
	}

	@ResponseBody
	@RequestMapping("/data/{bill_code}/fetch")
	public List<JSONObject> fetch( @PathVariable("bill_code") String bill_code, @RequestBody(required = false) JSONObject param, SecuritySession client) throws Exception {
//		if (client == null) {
//			LoginHelper.redirectLogin(request, response);
//			return null;
//		}
		param = JOHelper.cleanEmptyStr(param);
		List<JSONObject> list = bdService.fetchMainData(bill_code, param, client);
		return list;
	}

	@ResponseBody
	@RequestMapping("/data/{bill_code}/find/{pk_value_main}")
	public JSONObject findBillData(@PathVariable("bill_code") String bill_code, 
			@PathVariable("pk_value_main") String pk_value_main,
			@RequestParam(value = "child", required = false, defaultValue = "true") Boolean child, 
			SecuritySession client) throws BusinessError {
		return bdService.findBillData(bill_code, pk_value_main, child, client);
	}
}
