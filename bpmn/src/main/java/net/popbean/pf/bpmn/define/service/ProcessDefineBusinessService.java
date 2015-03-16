package net.popbean.pf.bpmn.define.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import net.popbean.pf.bpmn.define.vo.ProcessDefine;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.vo.SecuritySession;


/**
 * 流程定义管理
 * 
 * @author to0ld
 *
 */
public interface ProcessDefineBusinessService {
	/**
	 * 
	 * @param resources
	 * @param client
	 * @return 返回流程部署的id
	 * @throws BusinessError
	 */
	public List<String> deploy(List<String> resources, SecuritySession client) throws BusinessError;

	/**
	 * 删除部署
	 * @param resources
	 * @param client
	 * @throws BusinessError
	 */
	public void unDeploy(List<String> resources, SecuritySession client) throws BusinessError;

	/**
	 * 查找流程定义信息
	 * @param param 支持code，name，task_id来查询
	 * @param client
	 * @return
	 * @throws BusinessError
	 */
	public List<JSONObject> fetch(JSONObject param, SecuritySession client) throws BusinessError;

	/**
	 * 找到指定deploy_id的流程
	 * @param deploy_id
	 * @param client
	 * @return
	 * @throws BusinessError
	 */
	JSONObject findDeployModel(String deploy_id, SecuritySession client) throws BusinessError;
	/**
	 * 这个方法应该被禁止使用
	 * @param processDefinitionId
	 * @return
	 * @throws BusinessError
	 */
	JSONObject getStartFormValue(String processDefinitionId) throws BusinessError;
	/**
	 * 
	 * @param resource_list
	 * @param content_list
	 * @param client
	 * @return
	 * @throws BusinessError
	 */
	List<String> deployByContent(List<String> resource_list, List<String> content_list, SecuritySession client) throws BusinessError;
	/**
	 * 将depoy_id_list_target的流程更新为deploy_id_source
	 * @param deploy_id_source
	 * @param deploy_id_list_target
	 * @throws Exception
	 */
	void syncVersion(String deploy_id_source, List<String> deploy_id_list_target) throws Exception;
}
