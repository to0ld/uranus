package net.popbean.pf.bpmn.engine.task.service.impl;

import net.popbean.pf.bpmn.engine.task.service.TaskConfigBusinessService;
import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.helper.JOHelper;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.persistence.helper.DaoHelper;
import net.popbean.pf.security.vo.SecuritySession;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service("service/pf/bpmn/task/config")
public class TaskConfigBusinessServiceImpl extends AbstractBusinessService implements TaskConfigBusinessService {
	/**
	 * 显然这哥们需要cache
	 */
	@Override
	public JSONObject find(String pd_code, String task_code, SecuritySession client) throws BusinessError {
		try {
			StringBuilder sql = new StringBuilder(" select a.* from mt_pf_task_cfg a where pd_code=${PD_CODE} and task_code=${TASK_CODE}");
			JSONObject p = JO.gen("PD_CODE",pd_code,"TASK_CODE","task_code");
			JSONObject ret = _commondao.find(sql, p,"没有找到pd_code="+pd_code+",task_code="+task_code+"的流程任务配置");
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	/**
	 * 根据execution得到配属的消息
	 * @param work_item
	 * @param execution
	 * @return
	 * @throws BuzException
	 */
	@Override
	public String message(JSONObject work_item,DelegateExecution execution)throws BusinessError{
		try {
			String proc_def_id = execution.getProcessDefinitionId();
//			String pd_code = proc_def_id.split(":")[0];
			StringBuilder sql = new StringBuilder(" select key_ from act_re_procdef where id_=${PROC_DEF_ID} ");
			JSONObject proc_def_inst = _commondao.find(sql,JO.gen("PROC_DEF_ID",proc_def_id),"没有找到指定的流程定义(proc_def_id="+proc_def_id+")");
			String pd_code = proc_def_inst.getString("KEY_");
			//
			String aprv_tpl = "";
			sql = new StringBuilder("select * from mt_pf_task_cfg where pd_code=${PD_KEY} and task_code=${TASK_CODE}");
			JSONObject cfg_inst = _commondao.find(sql, JO.gen("PD_KEY",pd_code,"TASK_CODE",execution.getCurrentActivityId()));
			if(!JOHelper.has("APRV_TPL", cfg_inst)){
				//如果没有就使用流程的默认模板
				sql = new StringBuilder("select * from mt_pf_pd where pd_key=${PD_KEY}");
				JSONObject pd_inst = _commondao.find(sql, JO.gen("PD_KEY",pd_code));
				aprv_tpl = pd_inst.getString("TITLE_TPL");
//				return "你不应该看见我";
			}else{
				aprv_tpl = cfg_inst.getString("APRV_TPL");
			}
			String ret = DaoHelper.parseMsg(aprv_tpl, work_item);
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return "系统异常：没有配置任务消息模板";
	}
}
