package net.popbean.pf.srv.service;

import com.alibaba.fastjson.JSONObject;

import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.vo.SecuritySession;

/**
 * 服务管理，暂时只提供数据的导出功能
 * 
 * @author to0ld
 *
 */
public interface SrvManagementBusinessService {
	/**
	 * 
	 * @param version
	 * @param client
	 * @return
	 * @throws BusinessError
	 */
	public String exp(String version, SecuritySession client) throws BusinessError;
	/**
	 * 本质上是安装的意思
	 * @param version 服务很少需要识别版本
	 * @param param
	 * @param client
	 * @return
	 * @throws BusinessError
	 */
	public String imp(String version,JSONObject param, SecuritySession client) throws BusinessError;
}
