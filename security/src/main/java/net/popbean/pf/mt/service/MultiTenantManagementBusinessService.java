package net.popbean.pf.mt.service;

import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.vo.AppVO;
import net.popbean.pf.security.vo.CompanyVO;
import net.popbean.pf.security.vo.SecuritySession;

/**
 * 多租户管理(适用于应用被启用时,回调)
 * 
 * @author to0ld
 *
 */
public interface MultiTenantManagementBusinessService {
	/**
	 * 
	 * @param company
	 * @param app
	 * @param client
	 * @throws BusinessError
	 */
	public void enable(CompanyVO company, AppVO app, SecuritySession client) throws BusinessError;
	/**
	 * 
	 * @param company
	 * @param app
	 * @param client
	 * @throws BusinessError
	 */
	public void disable(CompanyVO company, AppVO app, SecuritySession client) throws BusinessError;
}
