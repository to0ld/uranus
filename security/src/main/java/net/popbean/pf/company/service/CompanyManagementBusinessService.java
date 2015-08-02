package net.popbean.pf.company.service;

import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.vo.AppVO;
import net.popbean.pf.security.vo.CompanyVO;
import net.popbean.pf.security.vo.SecuritySession;
/**
 * - [?] 第一个企业创建?
 * @author to0ld
 *
 */

public interface CompanyManagementBusinessService {
	/**
	 * 创建企业(由当前登陆者创建企业，兵建立起与之的关联)
	 * 
	 * @param inst
	 * @param client
	 * @throws BusinessError
	 */
	public void create(CompanyVO inst, SecuritySession client) throws BusinessError;

	/**
	 * 
	 * @param inst
	 * @param client
	 * @throws BusinessError
	 */
	public void activate(CompanyVO inst, SecuritySession client) throws BusinessError;

	/**
	 * 
	 * @param inst
	 * @param client
	 * @throws BusinessError
	 */
	public void seal(CompanyVO inst, SecuritySession client) throws BusinessError;

	/**
	 * 启用应用
	 * @param company_code
	 * @param app_code
	 * @param client
	 * @throws BusinessError
	 */
	public void enableApp(String company_code, String app_code, SecuritySession client) throws BusinessError;
	/**
	 * 封存应用
	 * @param company_code
	 * @param app_code
	 * @param client
	 * @throws BusinessError
	 */
	public void disableApp(String company_code, String app_code, SecuritySession client) throws BusinessError;
}
