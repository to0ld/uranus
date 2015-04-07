package net.popbean.pf.bpmn.engine.process.instance.service.impl;

import java.sql.Timestamp;
import java.util.List;

import net.popbean.pf.bpmn.define.vo.ProcessDefine;
import net.popbean.pf.bpmn.engine.process.instance.service.ProcessInstanceBusinessService;
import net.popbean.pf.bpmn.engine.process.instance.service.ProcessInstanceExtBusinessService;
import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.helper.JOHelper;
import net.popbean.pf.entity.helper.VOHelper;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.exception.ErrorBuilder;
import net.popbean.pf.lock.LockService;
import net.popbean.pf.security.vo.SecuritySession;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service("service/pf/bpmn/process/instance")
public class ProcessInstanceBusinessServiceImpl extends AbstractBusinessService implements ProcessInstanceBusinessService {
	public static final String COLL_PI = "pb_pf_procinst";
	//
	@Autowired
	@Qualifier("service/pf/lock/redis")
	protected LockService lockService;
	
	@Autowired
	@Qualifier("taskService")
	protected TaskService taskService;
	//
	@Autowired
	private ApplicationContext appContext;
	@Autowired
	@Qualifier("runtimeService")
	protected RuntimeService runtimeService;
	@Autowired
	@Qualifier("processEngineConfiguration")
	ProcessEngineConfigurationImpl processEngineConfiguration;

	//
//	@Autowired
//	@Qualifier("taskService")
//	protected TaskService taskService;
	//
	/**
	 * 清除所有的流程相关信息
	 * 
	 * @throws BuzException
	 */
	@Override
	public void cleanAll() throws BusinessError {
		try {
			//
			CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutor();
			CommandConfig config = new CommandConfig().transactionNotSupported();
			commandExecutor.execute(config, new Command<Object>() {
				public Object execute(CommandContext commandContext) {
					DbSqlSession session = commandContext.getSession(DbSqlSession.class);
					session.dbSchemaDrop();
					session.dbSchemaCreate();
					return null;
				}
			});
		} catch (Exception e) {
			processError(e);
		}
	}
	private ProcessInstanceExtBusinessService getProcInstExt(String beanname) {		
		try {
			return appContext.getBean(beanname,ProcessInstanceExtBusinessService.class);
		} catch (NoSuchBeanDefinitionException e) {
			return null;
		}
	}
	/**
	 * 启动流程
	 */
	@Override
	public String start(String pd_key, JSONObject workitem, String message, SecuritySession env) throws BusinessError {
//		String pk_value = workitem.getString("pk_work_item");
		String pk_value = null;
		try {
			//另外一种思路就是一旦启动就植入，结束/撤回再删除,这样就能拦截了吧。。。
			//
			StringBuilder sql = new StringBuilder("select * from pb_pf_procdef where code=${PD_KEY}");
			// FIXME 要是没有，就喊冤去吧
			ProcessDefine proc_def = _commondao.find(sql, JO.gen("PD_KEY", pd_key),ProcessDefine.class, "没有找到注册配置(" + pd_key + ")");
			String pk_key = proc_def.code.toUpperCase();//统一使用大写
			//找到所有的必须的key用于判断
			String proc_def_ref = proc_def.id;
			sql = new StringBuilder(" select code,type,rangeset,pk_pd,name from pb_pf_procdef_param where proc_def_id=${PK_PD} ");
			List<JSONObject> field_list = _commondao.query(sql, JO.gen("PK_PD",proc_def_ref));
			for(JSONObject field:field_list){//校验一下是否有必须，但是workitem又没有的属性
				if(StringUtils.isBlank(workitem.getString(field.getString("CODE")))){
					ErrorBuilder.createSys().msg("该工作项中，必填项"+field.getString("NAME")+"为空，无法进行后续操作，请检查后，再试").execute();
				}
			}
			//
			pk_value = workitem.getString(pk_key);
			lockService.lock("start", pk_value);// 简单锁
			ProcessInstanceExtBusinessService proc = getProcInstExt(proc_def.ext);
			if (proc != null) {
				proc.validateForStart(workitem, env);
			}
			String code_key = proc_def.code_key;
			String code_value = workitem.getString(code_key.toUpperCase());
			if (StringUtils.isBlank(code_key)) {
				ErrorBuilder.createSys().msg("注册配置中没有配置CODE_KEY").execute();
			}
			//FIXME 抽取为buildWorkItem(data,client)
//			String pk_work_item = workitem.getString("pk_work_item");
			workitem.put("pk_work_item",pk_value);
			workitem.put("PK_WORK_ITEM",pk_value);
			workitem.put("pk_account_crt_name", env.account_name);
			workitem.put("PK_ACCOUNT_CRT_NAME", env.account_name);
			workitem.put("pk_account_crt", env.account_id);// 请暂时无视这个悲剧
			workitem.put("pk_account_crt_code", env.account_code);
			workitem.put("PK_ACCOUNT_CRT_CODE",env.account_code);
			if(env.org != null){// FIXME?
				//为了避免被覆盖，选择先判断，有就保留，没有才覆盖
				workitem.put("LOGIN_PK_DEPT",env.org.id);
				if(!VOHelper.has("PK_DEPT_NAME", workitem)){
					workitem.put("PK_DEPT_NAME",env.org.name);	
				}
			}
			workitem.put("CODE_VALUE", code_value);
			
			Authentication.setAuthenticatedUserId(env.account_code);// 确保userid能找到
			JSONObject p = JO.gen("work_item",workitem,"isCommit",true);
			if(VOHelper.has("IS_BPM_TEST", workitem)){
				p.put("IS_BPM_TEST",workitem.get("IS_BPM_TEST"));
			}
			org.activiti.engine.runtime.ProcessInstance pi = runtimeService.startProcessInstanceByKey(pd_key,p);
			
			log("BPM.PI.START", "[" + env.account_code + "]启动一个编号为[" + pi.getId() + "]，单据编号为[" + workitem.get("pk_work_item") + "]的[" + pd_key + "]流程", env);
			if (proc != null) {
				proc.executeForStart(pi.getId(),workitem, env);
			}
			return pi.getId();

		} catch (Exception e) {
			processError(e);
		} finally {
			if(!StringUtils.isBlank(pk_value)){
				lockService.unlock("start", pk_value);	
			}
		}
		return null;
	}
	/**
	 * @deprecated 应该使用pb_pf_procinst
	 */
	@Override
	public JSONObject find(String proc_inst_id,SecuritySession client)throws BusinessError{
		try {
			JSONObject p = JO.gen("PROC_INST_ID", proc_inst_id);
			StringBuilder sql = new StringBuilder(" select a.end_time_,a.delete_reason_,b.*,c.pd_ext ");
			sql.append(" from act_hi_procinst a left join pb_pf_procinst b on (a.proc_inst_id_=b.proc_inst_id)");
			sql.append(" left join pb_pf_procdef c on (b.pd_key=c.pd_key) ");
			sql.append(" where 1=1  and a.proc_inst_id_=${PROC_INST_ID}");
			JSONObject proc_inst = _commondao.find(sql, p,"流程实例不存在，proc_inst_id=" + proc_inst_id);
			return proc_inst;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	/**
	 * task: 0:撤回;3:完成;7:驳回
	 * proc: 处理中:3；完成:5;撤回:7
	 */
	@Override
	public void withdraw(String proc_inst_id,String message,SecuritySession client)throws BusinessError{//撤回
		try {
			JSONObject p = JO.gen("PROC_INST_ID", proc_inst_id);
			JSONObject proc_inst = find(proc_inst_id,client);//FIXME 这是干嘛来着？只是验证该流程是否存在？
			ProcessInstanceExtBusinessService proc = getProcInstExt(proc_inst.getString("PD_EXT"));
			if (proc != null) {
				proc.validateForWithDraw(proc_inst, client);
			}
			
			StringBuilder sql = new StringBuilder(" select istat from pb_pf_procinst where proc_inst_id=${PROC_INST_ID} and istat=7");
			_commondao.assertTrue(sql, p, "指定的流程实例(proc_inst_id="+proc_inst_id+")已撤销，请到\"草稿箱\"查看");
			//
			lockService.unlock("clean", proc_inst_id);
			//
			//FIXME 这个操作应该是在execution listener中进行，而不是显性调用
			sql = new StringBuilder(" update rlt_procinst_account set istat=7,end_ts=${CURRENT_TS} where proc_inst_id=${PROC_INST_ID} and istat=3 ");// 确保被撤回的状态是7
			_commondao.executeChange(sql, JO.gen("PROC_INST_ID", proc_inst_id,"CURRENT_TS",new Timestamp(System.currentTimeMillis())));
			//如果是撤回，现有end_ts is null and proc_inst_id=${PROC_INST_ID}的min task_id,干掉其他的
			sql = new StringBuilder(" select min(a.task_id) as task_id,a.proc_inst_id from rlt_taskinst_account a where a.proc_inst_id=${PROC_INST_ID} and a.end_ts is null ");
			JSONObject task_inst = _commondao.find(sql, p,"系统异常，没有找到proc_inst_id="+proc_inst_id+"的当前task数据");
			//清除candidate的其他task
			sql = new StringBuilder(" delete from pb_pf_taskinst where 1=1 and proc_inst_id=${PROC_INST_ID} and end_ts is null and task_id!=${TASK_ID}");
			_commondao.executeChange(sql, task_inst);
			task_inst.put("START_TS",new Timestamp(System.currentTimeMillis()));
			task_inst.put("COMMENT",client.account_name + "(" + client.account_code + ")强制撤回");
			task_inst.put("PK_ACCOUNT",client.account_id);
			task_inst.put("ACCOUNT_CODE",client.account_code);
			task_inst.put("ACCOUNT_NAME",client.account_name);
			//
			sql = new StringBuilder(" update pf_pf_taskinst set istat=0,task_id=null,pk_account=${PK_ACCOUNT},pk_account_code=${ACCOUNT_CODE},pk_account_name=${ACCOUNT_NAME},end_ts=${START_TS},comment=${COMMENT} where proc_inst_id=${PROC_INST_ID} and task_id=${TASK_ID} ");
			_commondao.executeChange(sql, task_inst);//确保有撤回的信息:撤回是0
			//找到该流程的一个task,写入comment
			sql = new StringBuilder("select id_ as task_id,proc_inst_id_ as proc_inst_id from act_ru_task where proc_inst_id_=${PROC_INST_ID} order by create_time_");
			JSONObject ru_task_inst = _commondao.find(sql, p,"没有找到id="+proc_inst_id+"的流程实例");
			taskService.addComment(ru_task_inst.getString("TASK_ID"), proc_inst_id, message+"\n"+client.account_name + "(" + client.account_code + ")强制撤回");
			//
			runtimeService.deleteProcessInstance(proc_inst_id, message+"\n"+client.account_name + "(" + client.account_code + ")强制撤回");// 清除
			//
			if (proc != null) {
				proc.executeForWithDraw(proc_inst, client);
			}
		} catch (Exception e) {
			processError(e);
		}finally{
			lockService.unlock("clean", proc_inst_id);
		}
	}
	@Override
	public void delete(String proc_inst_id,Boolean changeStat,SecuritySession client)throws BusinessError{
		try {
			if (StringUtils.isBlank(proc_inst_id)) {
				return;
			}
			// 限制开发组员工才能使用
			JSONObject p = JO.gen("PROC_INST_ID", proc_inst_id);
			JSONObject rlt_inst = null;
			if(changeStat){//FIXME 如果需要更明细的提示信息就根据返回结果来判断
				StringBuilder sql_v = new StringBuilder(" select * from pb_pf_procinst where proc_inst_id=${PROC_INST_ID} and (istat =5 or istat=7)");
				rlt_inst = _commondao.find(sql_v, p,  "流程已经结束或没有找到指定的流程实例，如果刷新后，重新操作还是出错，请和管理员联系");
			}
			//
			StringBuilder sql = new StringBuilder(" delete from act_ru_identitylink where task_id_ in (select id_ from act_hi_taskinst where proc_inst_id_=${PROC_INST_ID}) ");
			_commondao.executeChange(sql, p);
			sql = new StringBuilder(" delete from act_ru_task where proc_inst_id_=${PROC_INST_ID} ");
			_commondao.executeChange(sql, p);
			sql = new StringBuilder(" delete from act_ru_variable where proc_inst_id_=${PROC_INST_ID} ");
			_commondao.executeChange(sql, p);
			sql = new StringBuilder(" delete from act_hi_taskinst where proc_inst_id_=${PROC_INST_ID} ");
			_commondao.executeChange(sql, p);
			sql = new StringBuilder(" delete from act_hi_varinst where proc_inst_id_=${PROC_INST_ID} ");
			_commondao.executeChange(sql, p);
			sql = new StringBuilder(" delete from act_hi_procinst where id_=${PROC_INST_ID} ");
			_commondao.executeChange(sql, p);
			sql = new StringBuilder(" delete from act_ru_identitylink where proc_inst_id_=${PROC_INST_ID} ");
			_commondao.executeChange(sql, p);
			sql = new StringBuilder(" delete from act_hi_identitylink where proc_inst_id_=${PROC_INST_ID} ");
			_commondao.executeChange(sql, p);
			sql = new StringBuilder(" delete from act_ru_execution where proc_inst_id_=${PROC_INST_ID} and parent_id_ is not null ");
			_commondao.executeChange(sql, p);
			sql = new StringBuilder(" delete from act_ru_execution where proc_inst_id_=${PROC_INST_ID} ");
			_commondao.executeChange(sql, p);
			sql = new StringBuilder(" delete from rlt_procinst_account where proc_inst_id=${PROC_INST_ID} ");
			_commondao.executeChange(sql, p);
			sql = new StringBuilder(" delete from rlt_taskinst_account where proc_inst_id=${PROC_INST_ID} ");
			_commondao.executeChange(sql, p);
			// FIXME 是否要考虑将业务单据的状态也处理掉呢？
			if(changeStat){//FIXME 如果要求变更状态，则已经完成或驳回的数据就不能再处理了
//				sql = new StringBuilder(" update "+rlt_inst.getString("META_CODE")+" set istat=0 where "+rlt_inst.getString("PK_KEY")+"=${PK_WORK_ITEM} ");
				sql = new StringBuilder(" select META_CODE, PK_KEY from pb_pf_procdef where code = ${PD_KEY} ");
				JSONObject vo = _commondao.find(sql, rlt_inst, "在表mt_pf_pd中没有查到PD_KEY对应的META_COE");
				sql = new StringBuilder(" update " + vo.getString("META_CODE") + " set istat=0 where " + vo.getString("PK_KEY") + " = ${PK_WORK_ITEM} ");
				_commondao.executeChange(sql, rlt_inst);
			}
			log("BPM.PI.DELETE", "proc_inst_id="+proc_inst_id, client);
		} catch (Exception e) {
			processError(e);
		}
	}
	/**
	 * 根据指定的条件查找流程实例
	 * @param param(pd_code;isClose,start_time,end_time,duration,start_user_id)
	 * @param client
	 * @return
	 * @throws BuzException
	 */
	@Override
	public List<JSONObject> fetch(JSONObject param,SecuritySession client)throws BusinessError{
		try {
			param = JOHelper.cleanEmptyStr(param);
			StringBuilder sql = new StringBuilder(" select a.* from act_hi_procinst a ");
			sql.append(" left join pb_pf_procinst b on (a.id_=b.proc_inst_id) ");
			sql.append(" where 1=1  ");
			sql.append(" $[and a.proc_def_id_ like ${PD_CODE}] ");
			sql.append(" $[and a.start_time_ >= ${START_TIME}] ");
			if(VOHelper.has("END_TIME", param)){
				sql.append(" and (a.end_time_ <= ${END_TIME} or a.end_time_ is null)");	
			}
			
			if(VOHelper.has("CODE_VALUE", param)){
				sql.append(" $[ and b.code_value=${CODE_VALUE}] ");//根据单据号查询
				param.put("CODE_VALUE","%"+param.getString("CODE_VALUE")+"%");
			}
			if(VOHelper.has("ACCOUNT_CODE", param)){
				sql.append(" $[ and b.account_code like ${ACCOUNT_CODE}] ");//根据制单人(mis账号)来查找
				param.put("ACCOUNT_CODE","%"+param.getString("ACCOUNT_CODE")+"%");
			}
			if(VOHelper.has("DEPT_NAME", param)){
				sql.append(" $[ and dept_name like ${DEPT_NAME} ] ");//根据部门名称来查询
				param.put("DEPT_NAME","%"+param.getString("DEPT_NAME")+"%");
			}
			sql.append(" $[and a.start_user_id_=${PK_ACCOUNT_CRT}] ");//FIXME 不太确定传入的是不是主键
			List<JSONObject> ret = _commondao.query(sql, param);
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
}
