package net.popbean.pf.bpmn.engine.task.service;

import java.util.Map;

import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.vo.SecuritySession;

import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.Task;

public interface TaskInstanceExtBusinessService extends TaskListener {
	/**
	 * 提交之前验证
	 * @param task
	 * @param context
	 * @param env
	 * @throws BusinessError
	 */
	public void validateForCommit(Task task, Map<String, Object> context, SecuritySession env) throws BusinessError;
	/**
	 * 可能叫callbackForCommit更合适
	 * @param task
	 * @param context
	 * @param env
	 * @throws BusinessError
	 */
	public void executeForCommit(Task task, Map<String, Object> context, SecuritySession env) throws BusinessError;

	public void validateForReject(Task task, Map<String, Object> context, SecuritySession env) throws BusinessError;

	public void executeForReject(Task task, Map<String, Object> context, SecuritySession env) throws BusinessError;
}
