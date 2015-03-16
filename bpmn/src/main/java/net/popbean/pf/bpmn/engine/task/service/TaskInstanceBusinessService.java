package net.popbean.pf.bpmn.engine.task.service;

import java.util.List;
import java.util.Map;

import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.persistence.helper.DaoConst.Paging;
import net.popbean.pf.security.vo.SecuritySession;

import com.alibaba.fastjson.JSONObject;

/**
 * 流程实例服务
 * @author to0ld
 *
 */
public interface TaskInstanceBusinessService {

	void reject(String task_id, String message, SecuritySession env) throws BusinessError;

	void commit(String task_id, String message, SecuritySession env) throws BusinessError;
	/**
	 * 
	 * @param pd_key
	 * @param param
	 * @param isCurrent
	 * @param paging
	 * @param env
	 * @return
	 * @throws BusinessError
	 */
	List<JSONObject> fetchTask(String pd_key, JSONObject param, boolean isCurrent, Paging paging, SecuritySession env) throws BusinessError;
	/**
	 * @deprecated 不推荐使用该方法,尽量用task param搞定
	 * @param task_id
	 * @param key_list
	 * @return
	 * @throws BusinessError
	 */
	JSONObject getFormValue(String task_id, List<String> key_list) throws BusinessError;
	/**
	 * 读取备注信息
	 * @param param
	 * @param client
	 * @return
	 * @throws BusinessError
	 */
	List<JSONObject> fetchComment(JSONObject param, SecuritySession client) throws BusinessError;
	/**
	 * 
	 * @param task_id
	 * @param key_list
	 * @return
	 * @throws BusinessError
	 */
	Map<String, Object> getContextvalue(String task_id, List<String> key_list) throws BusinessError;

}
