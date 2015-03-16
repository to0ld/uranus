package net.popbean.pf.bpmn.engine.task.service;

import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.vo.SecuritySession;

import org.activiti.engine.delegate.DelegateExecution;

import com.alibaba.fastjson.JSONObject;

/**
 * 任务配置
 * 
 * @author to0ld
 *
 */
public interface TaskConfigBusinessService {
	/**
	 * 
	 * @param task_code
	 * @return
	 * @throws BuzException
	 */
	public JSONObject find(String pd_code, String task_code, SecuritySession client) throws BusinessError;

	/**
	 * 
	 * @param work_item
	 * @param execution
	 * @return
	 * @throws BuzException
	 */
	String message(JSONObject work_item, DelegateExecution execution) throws BusinessError;
}
