package net.popbean.pf.security.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.mvc.controller.BaseController;
import net.popbean.pf.security.helper.AuthHelper;
import net.popbean.pf.security.service.AuthenticationBusinessService;
import net.popbean.pf.security.vo.SecuritySession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndViewDefiningException;
/**
 * 
 * @author to0ld
 *
 */

@Controller
public class AuthenticationController extends BaseController{
	@Autowired
	@Qualifier("service/pf/security/auth")
	AuthenticationBusinessService authService;
	//
	/**
	 * 暂时先这么写，将来完整的逻辑是
	 * 读取request中的sid(由认证服务器提供)->校验通过后直接跳转->如果没通过，重定向到登陆界面
	 * 如果request中没有sid，直接发送请求到认证服务器进行认证，驻留信息后予以返回(携带sid信息)
	 * @param request
	 * @param response
	 * @param account_code
	 * @param password
	 * @return
	 * @throws ModelAndViewDefiningException
	 * @throws BusinessError
	 */
	@ResponseBody
	@RequestMapping("/service/pf/security/account/signin")
	public SecuritySession login(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "account_code", defaultValue = "") String account_code,
			@RequestParam(value = "pwd", defaultValue = "") String password)
			throws ModelAndViewDefiningException, BusinessError {
		SecuritySession client;
		if (account_code.length() > 0) {
			client = authService.auth(account_code, password);
			AuthHelper.setClientEnv(request, client);//钝起来
		} else {
			client = AuthHelper.getClientEnv(request);
			/**先不考虑
			if (client == null || client.account_code == null) {
				VO account = SSOHelper.login(request, response);
				if (account == null) {
					return null;
				}
				try {
					client = authService.loginEnv(account.getString("login"));
				} catch (BusinessError e) {
//					client = new ClientEnv(account.getString("login"), account.getString("name"));
					client = new SecuritySession();
					client.account_name = account
				}
				AuthHelper.setClientEnv(request, client);
			}**/
		}
//		LoginHelper.redirectIfNeeded(request, response);
		SecuritySession session = new SecuritySession();
		session.account_name = client.account_name;
		session.account_code = client.account_code;
		session.account_id = client.account_id;
		return session;
//		return VO.gen("name", client.login_acc_name, "code", client.login_acc_code, "pk", client.login_pk_acc);
	}
}
