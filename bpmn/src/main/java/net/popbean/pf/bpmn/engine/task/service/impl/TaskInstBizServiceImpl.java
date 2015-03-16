package net.popbean.pf.bpmn.engine.task.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.popbean.pf.bpmn.engine.task.service.TaskInstanceBusinessService;
import net.popbean.pf.bpmn.engine.task.service.TaskInstanceExtBusinessService;
import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.helper.JOHelper;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.exception.ErrorBuilder;
import net.popbean.pf.persistence.helper.DaoConst.Paging;
import net.popbean.pf.persistence.helper.DaoHelper;
import net.popbean.pf.security.vo.SecuritySession;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Service("service/pf/bpmn/task/instance")
public class TaskInstBizServiceImpl extends AbstractBusinessService implements TaskInstanceBusinessService {
	@Autowired
	private ApplicationContext appContext;
	//
	@Autowired
	@Qualifier("taskService")
	protected TaskService taskService;
	@Autowired
	@Qualifier("runtimeService")
	protected RuntimeService runtimeService;
	//
	@Autowired
	@Qualifier("processEngine")
	ProcessEngine processEngine;
	//
	public static final String COLL_BPM_VAR = "mt_pf_bpm_var";
	//
	@Override
	public void commit(String task_id, String message, SecuritySession env) throws BusinessError {
		try {
			lockService.lock("commit", task_id);
			validateTaskOwner(task_id, env);
			Task t = taskService.createTaskQuery().taskId(task_id).singleResult();
			// 为了确保能读取到正确的流转记录，空的也插入
			Authentication.setAuthenticatedUserId(env.account_code);// 确保userid能找到
			taskService.setAssignee(t.getId(), env.account_code);// 确保act_hi_taskinst的assignee有数据

			//
			taskService.addComment(t.getId(), t.getProcessInstanceId(), message);
			//
			runtimeService.setVariable(t.getExecutionId(), "isCommit", true);// 确保flow中有,全局变量
			Map<String, Object> ctx = runtimeService.getVariables(t.getExecutionId());
			
			TaskInstanceExtBusinessService taskext = getTaskExt(t.getProcessDefinitionId(),t.getTaskDefinitionKey());
			if (taskext != null) {
				ctx.put("COMMIT_MESSAGE", message);
				taskext.validateForCommit(t, ctx, env);// FIXME 如果有必要就clone一下数据，避免被污染
			}
			updateTask(t, 3, message, env);
			taskService.complete(t.getId(), ctx);
			String process = getProcessName(t.getProcessInstanceId());
			log("BPM.TASK.COMMIT", "[" + env.account_code + "]批准通过一个任务编号为[" + t.getId() + "]，流程实例为[" + t.getProcessInstanceId() + "]的[" + process + "]流程", env);
			
			if (taskext != null) {
				ctx.put("COMMIT_MESSAGE", message);
				taskext.executeForCommit(t, ctx, env);// FIXME 如果有必要就clone一下数据，避免被污染
			}
		} catch (Exception e) {
			processException(e);
			processError(BusinessError.MSG_NORMAL + "(" + task_id + ")", e);
		} finally {
			lockService.unlock("commit", task_id);
		}
	}
	void processException(Exception e) throws BusinessError{
		Throwable ex = e;
		while((ex = ex.getCause()) != null) {
			if(ex instanceof BusinessError) {
				throw (BusinessError) ex;
			}
		}
	}
	private void updateTask(Task task,Integer new_stat,String message,SecuritySession env)throws BusinessError{
		try {
			String task_id = task.getId();
			StringBuilder sql = new StringBuilder(" update rlt_taskinst_account set istat=${ISTAT},");
			sql.append(" comment=${COMMENT},");
			sql.append(" end_ts=${END_TS},");
			sql.append(" duration=cast((end_ts-start_ts) as signed)/1000, ");//timestamp 2 int
			sql.append(" pk_account=${PK_ACCOUNT},");
			sql.append(" pk_account_code=${ACCOUNT_CODE},");
			sql.append(" pk_account_name=${ACCOUNT_NAME},");
			sql.append(" pk_dept=${PK_DEPT},");
			sql.append(" pk_dept_name=${DEPT_NAME}, ");
			sql.append(" task_code=${TASK_CODE} ");
			sql.append(" where task_id=${TASK_ID} ");
			JSONObject p = JO.gen("ISTAT",new_stat,"COMMENT",message,"TASK_ID",task_id,"END_TS",new Timestamp(System.currentTimeMillis()));
			p.put("PK_ACCOUNT",env.account_id);
			p.put("ACCOUNT_CODE",env.account_code);
			p.put("ACCOUNT_NAME",env.account_name);
			p.put("PK_DEPT",env.org.id);
			p.put("DEPT_NAME",env.org.name);
			p.put("TASK_CODE",task.getTaskDefinitionKey());
			_commondao.executeChange(sql, p);			
		} catch (Exception e) {
			processError(e);
		}

	}
	@Override
	public void reject(String task_id, String message, SecuritySession env) throws BusinessError {
		try {
			lockService.lock("reject", task_id);
			validateTaskOwner(task_id, env);
			Task t = taskService.createTaskQuery().taskId(task_id).singleResult();
			// FIXME 需要判断是否为空吧
			//
			// 模拟添加自己的审批意见
			if (!StringUtils.isBlank(message)) {
				Authentication.setAuthenticatedUserId(env.account_id);// 确保userid能找到
				taskService.addComment(t.getId(), t.getProcessInstanceId(), message);
			}
			runtimeService.setVariable(t.getExecutionId(), "isCommit", false);
			taskService.setVariableLocal(task_id, "isCommit", false);
			taskService.setAssignee(t.getId(), env.account_id);// 确保act_hi_taskinst的assignee有数据
			// FIXME 能拿到整个process instance的变量么？
			Map<String, Object> ctx = runtimeService.getVariables(t.getExecutionId());
																						// 
			TaskInstanceExtBusinessService taskext = getTaskExt(t.getProcessDefinitionId(),t.getTaskDefinitionKey());
			if (taskext != null) {
				taskext.validateForReject(t, ctx, env);// FIXME 如果有必要就clone一下数据，避免被污染
			}
			
			updateTask(t, 7, message, env);//更新rlt_taskinst_account的状态,要更新_code,_name
			taskService.complete(t.getId(), ctx);
			//
			
			//
			if (taskext != null) {
				taskext.executeForReject(t, ctx, env);// FIXME 如果有必要就clone一下数据，避免被污染
			}
			String process = getProcessName(t.getProcessInstanceId());
			log("BPM.TASK.REJECT", "任务编号为[" + t.getId() + "]，流程实例为[" + t.getProcessInstanceId() + "]的[" + process + "]流程", env);
		} catch (Exception e) {
			processError(e);
		} finally {
			lockService.unlock("reject", task_id);
		}
	}
	/**
	 * 
	 * @param t
	 * @return
	 */
	private String getProcessName(String proc_inst_id) throws Exception {
		StringBuilder sql = new StringBuilder(" select pd_name from pb_pf_procinst where proc_inst_id=${PROC_INST_ID} ");
		JSONObject inst = _commondao.find(sql, JO.gen("PROC_INST_ID",proc_inst_id),"系统异常，请与运维人员联系(proc_inst_id="+proc_inst_id+"没找到)");
		return inst.getString("PD_NAME");
	}
	private TaskInstanceExtBusinessService getTaskExt(String proc_def_id,String task_code) throws Exception {
		try {
			StringBuilder sql = new StringBuilder(" select * from mt_pf_task_cfg where pd_code in (select key_ from act_re_procdef where id_=${PROC_DEF_ID}) ");
			sql.append(" and task_code=${TASK_CODE} ");
			JSONObject task = _commondao.find(sql, JO.gen("PROC_DEF_ID",proc_def_id,"TASK_CODE",task_code));
			//
			if(!JOHelper.has("EXT", task)){
				return null;
			}
			String ext = task.getString("EXT");
			return (TaskInstanceExtBusinessService) appContext.getBean(ext);			
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 验证当前登陆者是不是任务的拥有者
	 * 
	 * @param task_id
	 * @param env
	 * @throws BuzException
	 */
	private void validateTaskOwner(String task_id, SecuritySession env) throws BusinessError {
		try {
			StringBuilder sql = new StringBuilder("select user_id_ from act_ru_identitylink where task_id_=${TASK_ID} ");
			List<JSONObject> id_inst = _commondao.query(sql, JO.gen("TASK_ID", task_id));
			if (CollectionUtils.isEmpty(id_inst)) {
				ErrorBuilder.createBusiness().msg("该任务已经执行完成，请刷新界面，重新加载数据，确认后，再试").execute();;
			}
			for (JSONObject id : id_inst) {
				if (JOHelper.equalsStringValue(id, "USER_ID_", env.account_code)) {
					return;
				}
			}
			//
			List<String> list = JOHelper.leach(id_inst, "USER_ID_", false);
			ErrorBuilder.createBusiness().msg("您不是当前任务的拥有者，不能进行相关操作task_id=" + task_id + "-owner:" + list + "-current:" + env.account_code).execute();
		} catch (Exception e) {
			processError(e);
		}
	}
	@Override
	public List<JSONObject> fetchTask(String pd_key,JSONObject param,boolean isCurrent,Paging paging,SecuritySession env)throws BusinessError{
		if(isCurrent){
			return fetchCurrentTask(pd_key,param,paging,env);
		}else{
			return fetchHistoryTask(pd_key,param,paging,env);
		}
	}
	private List<JSONObject> fetchHistoryTask(String pd_key,JSONObject param,Paging paging,SecuritySession env)throws BusinessError{
		try {
			JSONObject p = JOHelper.cleanEmptyStr(param);
			StringBuilder sql = new StringBuilder(" select a.* from rlt_taskinst_account a");
			sql.append(" where 1=1 and istat!=0 ");//排除掉自己撤回的单据
			pd_key = StringUtils.trimToEmpty(pd_key);
			
			if(!StringUtils.isBlank(pd_key)){
				sql.append(" and a.pd_key=${PD_KEY} ");
				p.put("PD_KEY",pd_key);
			}
			if (env != null && StringUtils.isNotBlank(env.account_code)) {
				sql.append(" and (a.pk_account_code=${ACCOUNT_CODE} or a.pk_account_o_code=${ACCOUNT_CODE})");
				p.put("ACCOUNT_CODE",env.account_code);
			}
			if(JOHelper.equalsStringValue(p, "ISTAT", "999")){
				p.remove("ISTAT");
			}
			sql.append(" $[and a.proc_inst_id=${PROC_INST_ID}] ");//支持按照流程实例进行过滤
			sql.append(" $[and a.istat=${ISTAT}] ");//方便过滤通过还是驳回的信息
			sql.append(" $[and a.task_id=${TASK_ID}] ");
			sql.append(" $[and a.pk_dept=${PK_DEPT}] ");
			if(JOHelper.has("DEPT_NAME", p)){
				p.put("DEPT_NAME","%"+p.get("DEPT_NAME")+"%");
				sql.append(" $[and a.dept_name like ${DEPT_NAME}] ");
			}
			sql.append(" $[and a.end_ts >=${START_TS}] ");
			sql.append(" and a.end_ts is not null ");
			sql.append(" $[and a.end_ts<=${END_TS}] ");
//			
			if(JOHelper.has("TASK_DESC", p)){
				p.put("TASK_DESC","%"+p.get("TASK_DESC")+"%");
			}
			sql.append(" $[ and a.task_desc like ${TASK_DESC} ] ");
			sql.append(" order by a.end_ts desc ");
			List<JSONObject> ret = _commondao.paging(sql, p, paging);
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	/**
	 * 查询还没完成的任务
	 * @param pd_key
	 * @param param 支持如下参数：PD_KEY;ACCOUNT_CODE;TASK_ID;PK_DEPT;START_TS;END_TS;TASK_DESC
	 * @param paging
	 * @param env
	 * @return
	 * @throws BuzException
	 */
	private List<JSONObject> fetchCurrentTask(String pd_key,JSONObject param,Paging paging,SecuritySession env)throws BusinessError{
		try {
			JSONObject p = JOHelper.cleanEmptyStr(param);
//			pd_key = StringUtils.trimToEmpty(pd_key);
			//为了提升效率，将该过程分解为两部，第一步得到task列表，第二步得到task信息
			//需要建索引：create index idx_rlt_account_dept_pk_account_code on rlt_account_dept (pk_account_code)
			//
			StringBuilder sql = new StringBuilder(" select a.id_ as task_id ");
			sql.append(" from act_ru_task a ");
			sql.append(" where 1=1 ");
			//
			sql.append(" $[and a.id_=${TASK_ID}] ");
			sql.append(" $[and a.proc_inst_id_=${PROC_INST_ID}] ");//支持按照流程实例进行过滤
			sql.append(" $[and a.create_time_ >=${START_TS}] ");
			sql.append(" $[and a.create_time_<=${END_TS}] ");
			
			//
			sql.append(" and a.id_ in ( ");
			sql.append(" select b.task_id_ from act_ru_identitylink b ");
			sql.append(" where b.user_id_ is not null ");
			//
			if (env != null && StringUtils.isNotBlank(env.account_code)) {
				sql.append(" and b.user_id_=${ACCOUNT_CODE} ");
				p.put("ACCOUNT_CODE",env.account_code);
			}			
			//
			if (JOHelper.has("PK_DEPT", p)) {
				sql.append(" and b.user_id_ in ( ");
				sql.append(" select c.pk_account_code from rlt_account_dept c ");
				sql.append(" where $[c.pk_dept=${PK_DEPT}] ");
				sql.append(" ) ");
			}

			sql.append(" ) ");
			if(!StringUtils.isBlank(pd_key)){
				sql.append(" and a.proc_def_id_ like ${PD_KEY} ");
				p.put("PD_KEY","%"+pd_key+"%");
			}
			//
			if(JOHelper.equalsStringValue(p, "ISTAT", "999")){
				p.remove("ISTAT");
			}

			if(JOHelper.has("TASK_DESC", p)){
				p.put("TASK_DESC","%"+p.get("TASK_DESC")+"%");
				sql.append(" and a.description_ like ${TASK_DESC} ");
			}
			sql.append(" order by a.create_time_ asc ");
			List<JSONObject> ret = _commondao.paging(sql, p, paging);//要保留分页信息
			if(CollectionUtils.isEmpty(ret)){
				return new ArrayList<>();
			}
			JSONObject ret_page = JO.gen("pageNo",ret.get(0).get("pageNo"),"totalCount",ret.get(0).get("totalCount"),"totalPageCount",ret.get(0).get("totalPageCount"));
			sql = new StringBuilder("select a.* from rlt_taskinst_account a where task_id ");
			sql.append(DaoHelper.Sql.in("TASK_ID", ret.size()));
			sql.append(" order by start_ts ");
			List<String> task_id_list = JOHelper.leach(ret, "TASK_ID", false);
			JSONObject tmp = new JSONObject();
			tmp = DaoHelper.Sql.in(tmp, task_id_list, "TASK_ID");
			ret = _commondao.paging(sql, tmp, null);//FIXME 超过了1000就会死得很难看
			//确保其带有action
			sql = new StringBuilder(" select a.task_code,a.proc_def_code,a.task_action from pb_pf_task_cfg a where 1=1 ");
			List<JSONObject> list = _commondao.query(sql, null);
			Map<String,String> bus = new HashMap<String, String>();
			for(JSONObject v:list){
				String action = v.getString("TASK_ACTION");
				if(!StringUtils.isBlank(action)){
					bus.put(v.getString("PD_CODE")+"#"+v.getString("TASK_CODE"), action);
				}
			}
			for(JSONObject v:ret){
				String key = v.getString("PD_KEY")+"#"+v.getString("TASK_CODE");
				String action = bus.get(key);
				if(!StringUtils.isBlank(action)){
					List<JSONObject> action_list = JOHelper.ja2list(JSON.parseArray(action));
					v.put("action",action_list);
				}
			}
			if(!CollectionUtils.isEmpty(ret)){
				ret.get(0).put("pageNo",ret_page.get("pageNo"));
				ret.get(0).put("totalCount",ret_page.get("totalCount"));
				ret.get(0).put("totalPageCount",ret_page.get("totalPageCount"));
			}
			//
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	/**
	 * 
	 * @param param
	 * @param client
	 * @return
	 * @throws BuzException
	 */
	@Override
	public List<JSONObject> fetchComment(JSONObject param,SecuritySession client)throws BusinessError{
		try {
			StringBuilder sql = new StringBuilder(" select a.* from rlt_taskinst_account a where 1=1  ");
			sql.append(" $[and a.pk_work_item=${PK_WORK_ITEM}] ");//尤其要注意撤回时的task list
			List<JSONObject> ret = _commondao.query(sql, param);
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	/**
	 * 
	 * @param task_id
	 * @param key_list
	 * @return
	 * @throws BuzException
	 */
	@Override
	public JSONObject getFormValue(String task_id, List<String> key_list) throws BusinessError {
		try {
			TaskFormData tfd = processEngine.getFormService().getTaskFormData(task_id);
			List<FormProperty> list = tfd.getFormProperties();
			JSONObject ret = new JSONObject();
			if(CollectionUtils.isEmpty(key_list)){
				for (FormProperty fp : list) {
					ret.put(fp.getName(), fp.getValue());	
				}
			}else{
				for (String key : key_list) {
					for (FormProperty fp : list) {
						if (key.equals(fp.getName())) {
							ret.put(fp.getName(), fp.getValue());
							break;
						}
					}					
				}
			}
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	@Override
	public Map<String, Object> getContextvalue(String task_id, List<String> key_list) throws BusinessError {
		try {
			Task t = taskService.createTaskQuery().taskId(task_id).singleResult();
			if (t == null) {
				// FIXME 抛出异常，好吧
			}
			Map<String, Object> ret = runtimeService.getVariables(t.getExecutionId(), key_list);
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
}
