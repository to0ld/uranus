package net.popbean.pf.app.service;

import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.vo.SecuritySession;
/**
 * 应用管理
 * 为指定的app提供安装，卸载，升级，回滚的操作
 * @author to0ld
 *
 */
public interface AppManagementBusinessService {
	/**
	 * 安装
	 * @param appkey
	 * @param client
	 * @throws BusinessError
	 */
	void install(String appkey, SecuritySession client) throws BusinessError;
	/**
	 * 反安装
	 * @param appkey
	 * @param client
	 * @throws BusinessError
	 */
	void unInstall(String appkey, SecuritySession client) throws BusinessError;
	/**
	 * 
	 * @param appkey
	 * @param version
	 * @param client
	 * @throws BusinessError
	 */
	void upgrade(String appkey,String version,SecuritySession client)throws BusinessError;
	/**
	 * 回滚到指定版本
	 * @param appkey
	 * @param version
	 * @param client
	 * @throws BusinessError
	 */
	void rollback(String appkey,String version,SecuritySession client)throws BusinessError;

}
