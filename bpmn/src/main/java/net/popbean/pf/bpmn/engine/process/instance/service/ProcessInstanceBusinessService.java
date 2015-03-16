package net.popbean.pf.bpmn.engine.process.instance.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.vo.SecuritySession;

/**
 * 流程实例管理
 * @author to0ld
 *
 */
public interface ProcessInstanceBusinessService {
	/**
	 * 启动流程
	 * 
	 * @param pd_key
	 * @param workitem
	 * @param message
	 * @param env
	 * @return
	 * @throws BuzException
	 */
	String start(String pd_key, JSONObject workitem, String message, SecuritySession env) throws BusinessError;

	/**
	 * 撤回指定流程
	 * 
	 * @param proc_inst_id
	 * @param message
	 *            撤回原因
	 * @param client
	 * @throws BuzException
	 */
	void withdraw(String proc_inst_id, String message, SecuritySession client) throws BusinessError;

	/**
	 * 
	 * @param proc_inst_id
	 * @param changeStat
	 * @param client
	 * @throws BuzException
	 */
	void delete(String proc_inst_id, Boolean changeStat, SecuritySession client) throws BusinessError;

	public JSONObject find(String proc_inst_id, SecuritySession client) throws BusinessError;
	/**
	 * 
	 * @param param
	 * @param client
	 * @return
	 * @throws BuzException
	 */
	public List<JSONObject> fetch(JSONObject param, SecuritySession client) throws BusinessError;

	void cleanAll() throws BusinessError;
}
