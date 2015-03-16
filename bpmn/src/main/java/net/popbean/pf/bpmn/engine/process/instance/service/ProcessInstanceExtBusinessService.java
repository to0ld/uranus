package net.popbean.pf.bpmn.engine.process.instance.service;

import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.vo.SecuritySession;

import org.activiti.engine.delegate.ExecutionListener;

import com.alibaba.fastjson.JSONObject;


/**
 * 
 * @author to0ld
 * 
 */
public interface ProcessInstanceExtBusinessService extends ExecutionListener{
	/**
	 * 启动前执行
	 * 
	 * @param context
	 * @param client
	 * @throws BuzException
	 */
	public void validateForStart(JSONObject context, SecuritySession client) throws BusinessError;
	/**
	 * 流程启动后执行
	 * 
	 * @param context
	 * @param client
	 * @throws BuzException
	 */
	public void executeForStart(String proc_inst_id,JSONObject context, SecuritySession client) throws BusinessError;

	/**
	 * 清除前执行 <br>
	 * <strong>注意</strong>: 此方法和{@link #validateForDelete(VO, SecuritySession)}
	 * 不同，传的不是整个主子表
	 * 
	 * @param proc_inst
	 * @param client
	 * @throws BuzException
	 */
	public void validateForDelete(JSONObject context, SecuritySession client) throws BusinessError;

	/**
	 * 撤回后执行 <br>
	 * <strong>注意</strong>: 此方法和{@link #executeForDelete(VO, SecuritySession)}
	 * 不同，传的不是整个主子表
	 * 
	 * @param context
	 * @param client
	 * @throws BuzException
	 */
	public void executeForDelete(JSONObject context, SecuritySession client) throws BusinessError;
	/**
	 * 
	 * @param context
	 * @param client
	 * @throws BuzException
	 */
	public void validateForWithDraw(JSONObject context, SecuritySession client) throws BusinessError;
	/**
	 * 
	 * @param context
	 * @param client
	 * @throws BuzException
	 */
	public void executeForWithDraw(JSONObject context, SecuritySession client) throws BusinessError;
}
