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
	 * 
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
