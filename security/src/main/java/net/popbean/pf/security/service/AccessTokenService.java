package net.popbean.pf.security.service;

import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.mvc.interceptor.access.TokenType;

/**
 * 
 * @author to0ld
 *
 */
public interface AccessTokenService {
	/**
	 * 
	 * @return
	 * @throws BusinessError
	 */
	public String gen(TokenType type) throws BusinessError;
	/**
	 * 
	 * @param token
	 * @throws BusinessError
	 */
	public void auth(TokenType type,String token) throws BusinessError;
}
