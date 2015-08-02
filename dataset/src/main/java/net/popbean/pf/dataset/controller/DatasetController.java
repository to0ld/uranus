package net.popbean.pf.dataset.controller;

import net.popbean.pf.dataset.service.DataSetBusinessService;
import net.popbean.pf.dataset.vo.DataSetModel;
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
public class DatasetController extends BaseController{
	@Autowired
	@Qualifier("service/pf/dataset")
	DataSetBusinessService dsService;
	/**
	 * 获取model
	 * @param appkey
	 * @param client
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("service/{appkey}/dataset/{ds_code}")
	public DataSetModel findModel(@PathVariable("ds_code")String ds_code,
			@RequestParam(value="with_data",defaultValue="false")Boolean withData,
			@RequestBody JSONObject param,
			SecuritySession client)throws Exception{
		//暂时先用一个数值做保护
//		amService.install(appkey, client);
		//FIXME 可以从其中得到paging参数
		DataSetModel model = dsService.findModel(ds_code, param, withData, false, null, client);
		return model;
	}
}
