package net.popbean.pf.security.service;

import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.vo.SecuritySession;
/**
 * 鉴权服务
 * @author to0ld
 *
 */
public interface AuthenticationBusinessService {
	/**
	 * 个人感觉应该用account_code+app_code来做，但这个吧又似乎与resource-mapping功能重合了
	 * @param account_code
	 * @param account_pwd
	 * @return
	 * @throws BusinessError
	 */
	public SecuritySession buildSession(String account_code) throws BusinessError;
	/**
	 * 
	 * @param account_code
	 * @param account_pwd
	 * @return
	 * @throws BusinessError
	 */
	public SecuritySession auth(String account_code, String account_pwd) throws BusinessError;
}
