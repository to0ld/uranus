package net.popbean.pf.config.service;

import net.popbean.pf.config.vo.ConfigModel;
import net.popbean.pf.config.vo.IConfigValueStore;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.vo.SecuritySession;

import com.alibaba.fastjson.JSONObject;

/**
 * 配置服务
 * 
 * @author to0ld
 *
 */
public interface ConfigBusinessService {

	/**
	 * 获取Json
	 * 
	 * @param app_code
	 * @param cfg_unique
	 * @param env
	 * @return
	 */
	public IConfigValueStore findValue(String app_code, String cfg_unique, SecuritySession env) throws BusinessError;

	/**
	 * 
	 * @param app_code
	 * @param cfg_unique
	 * @param env
	 * @return
	 * @throws BusinessError
	 */
	public ConfigModel findModel(String app_code, String cfg_unique, SecuritySession env) throws BusinessError;

	/**
	 * 由于参数有分级别(global,app,company,account)，如果允许在各自的app内定义IConfigValueStore
	 * @param cfg_unique
	 * @param value
	 * @param env
	 * @return
	 * @throws BuzException
	 */
	public IConfigValueStore changeValue(String app_code, String cfg_unique, IConfigValueStore value, SecuritySession env) throws BusinessError;

	/**
	 * 保存模型，建议最好进行代码级的授权
	 * 
	 * @param model
	 * @param env
	 * @return
	 * @throws BusinessError
	 */
	public String saveModel(ConfigModel model, SecuritySession env) throws BusinessError;
}
