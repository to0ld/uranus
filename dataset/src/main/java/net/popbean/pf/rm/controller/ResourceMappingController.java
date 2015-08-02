package net.popbean.pf.rm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import net.popbean.pf.dataset.service.DataSetBusinessService;
import net.popbean.pf.dataset.vo.DataSetModel;
import net.popbean.pf.mvc.controller.BaseController;
import net.popbean.pf.persistence.helper.DaoConst.Paging;
import net.popbean.pf.rm.service.ResourceMappingBusinessService;
import net.popbean.pf.rm.vo.ResourceMappingModel;
import net.popbean.pf.security.vo.SecuritySession;
@Controller
@RequestMapping("/service/{appkey}/rm")
public class ResourceMappingController extends BaseController {
	@Autowired
	@Qualifier("service/pf/resourcemapping")
	ResourceMappingBusinessService rmService;
	@Autowired
	@Qualifier("service/pf/dataset")
	DataSetBusinessService dsService;
	//
	@ResponseBody
	@RequestMapping("/subject/{rm_code}")
	public List<JSONObject> fetchSubjectList(@PathVariable("rm_code")String rm_code,
			SecuritySession client)throws Exception{
		List<JSONObject> list = rmService.fetchSubjectList(rm_code, null, null, client);//暂时先不要接受参数吧
		return list;
	}
	@ResponseBody
	@RequestMapping("/subject/model/{rm_code}")
	public DataSetModel findSubjectDatasetModel(@PathVariable("rm_code")String rm_code, @RequestBody(required = false) JSONObject param,
			SecuritySession client)throws Exception{
		ResourceMappingModel model = rmService.findModel(rm_code, client);
		String pk_subject = model.id;
		Paging paging = Paging.build(param);
		DataSetModel ds_model = dsService.findModel(pk_subject, param, true, true, paging, client);
		return ds_model;
	}
	/**
	 * 获取已经选中的
	 * @param code
	 * @param subject_id
	 * @param param
	 * @param client
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/resource/{code}/choiced/{subject_id}")
	public List<JSONObject> fetchChoicedResourceList(@PathVariable("code")String code,
			@PathVariable("subject_id")String subject_id,@RequestBody(required = false) JSONObject param, SecuritySession client)throws Exception{
		List<JSONObject> list = rmService.fetchRangeListForMapping(code, subject_id, true, param, client);
		return list;
	}
	/**
	 * 未选中
	 * @param code
	 * @param pk_subject
	 * @param client
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/resource/{code}/unchoice/{subject_id}")
	public JSONObject fetchUnChoicedResourceList(@PathVariable("code")String code,
			@PathVariable("subject_id")String pk_subject,
			SecuritySession client)throws Exception{
		JSONObject vo = rmService.fetchRangesForMapping(code, pk_subject, client);
		return vo;
	}
	/**
	 * 活动模型
	 * @param code
	 * @param client
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/model/{code}")
	public ResourceMappingModel findModel(@PathVariable("code")String code,
			SecuritySession client)throws Exception{
		//先拿到rm_inst分离出pk_subject,pk_resource
		//寻找各自的datapod model拼凑可以被ddb使用的数据结构
		ResourceMappingModel ret = rmService.findModel(code, client);
		return ret;
	}
}
