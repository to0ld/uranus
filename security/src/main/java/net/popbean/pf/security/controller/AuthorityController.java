package net.popbean.pf.security.controller;

import java.util.List;

import net.popbean.pf.mvc.controller.BaseController;
import net.popbean.pf.security.service.AuthenticationBusinessService;
import net.popbean.pf.security.vo.PermVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
/**
 * 
 * @author to0ld
 *
 */

@Controller
public class AuthorityController extends BaseController{
	@Autowired
	@Qualifier("service/pf/security/auth")
	AuthenticationBusinessService authService;
	//
	public List<PermVO> fetchNodesByAccount(String account_code)throws Exception{
		//FIXME 应该是调用resource-mapping中的实现(需要带参数)
		return null;
	}
}
