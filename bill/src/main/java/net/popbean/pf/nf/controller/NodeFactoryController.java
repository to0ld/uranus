package net.popbean.pf.nf.controller;

import net.popbean.pf.business.service.CommonBusinessService;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.nf.service.NodeFactoryBusinessService;
import net.popbean.pf.nf.vo.NodeFactoryModel;
import net.popbean.pf.security.vo.SecuritySession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/service/{appkey}/node-factory")
public class NodeFactoryController {
	@Autowired
	@Qualifier("service/pf/common")
	CommonBusinessService commonService;
	@Autowired
	@Qualifier("service/pf/node-factory")
	NodeFactoryBusinessService nfService;

	//
	/**
	 * 获得node factory model
	 * 
	 * @param code
	 * @param client
	 * @return
	 * @throws BusinessError
	 */
	@ResponseBody
	@RequestMapping("/{code}/model")
	public NodeFactoryModel find(@PathVariable("code") String code, SecuritySession client) throws BusinessError {
		NodeFactoryModel model = nfService.find(code, client);
		return model;
	}
}
