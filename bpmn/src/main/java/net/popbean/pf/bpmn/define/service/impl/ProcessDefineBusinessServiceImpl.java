package net.popbean.pf.bpmn.define.service.impl;

import java.util.ArrayList;
import java.util.List;

import net.popbean.pf.bpmn.define.service.ProcessDefineBusinessService;
import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.helper.JOHelper;
import net.popbean.pf.entity.helper.VOHelper;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.helper.IOHelper;
import net.popbean.pf.persistence.helper.DaoHelper;
import net.popbean.pf.security.vo.SecuritySession;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.repository.DeploymentBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;

@Service("service/pf/bpmn/process/define")
public class ProcessDefineBusinessServiceImpl extends AbstractBusinessService implements ProcessDefineBusinessService {
	@Autowired
	@Qualifier("processEngine")
	ProcessEngine processEngine;
	//
	@Autowired
	@Qualifier("processEngineConfiguration")
	ProcessEngineConfigurationImpl processConfig;
	//
	@Autowired
	@Qualifier("repositoryService")
	RepositoryService repositoryService;
	@Override
	public List<String> deploy(List<String> resources,SecuritySession client) throws BusinessError {
		String current = null;
		try {
			List<String> ret = new ArrayList<>();
			for (String r : resources) {
				current = r;
				DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
				deploymentBuilder.addClasspathResource(r);
				String id = deploymentBuilder.deploy().getId();
				ret.add(id);
			}
			oplogService.log("BPM.PD.DEPLOY", JO.gen("msg","["+StringUtils.join(resources, ",")+"]被重新部署(id="+ret+")"), client);
			return ret;
		} catch (Exception e) {
			processError("current:"+current,e);
		}
		return null;
	}
	/**
	 * 根据内容来部署
	 * @param resource_list 随意
	 * @param content_list
	 * @param client
	 * @return
	 * @throws BuzException
	 */
	@Override
	public List<String> deployByContent(List<String> resource_list,List<String> content_list,SecuritySession client)throws BusinessError{
		try {
			List<String> ret = new ArrayList<>();
			for (String r : content_list) {
				DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
				deploymentBuilder.addString("", r);
				String id = deploymentBuilder.deploy().getId();
				ret.add(id);
			}
			oplogService.log("BPM.PD.DEPLOY", JO.gen("msg","["+StringUtils.join(resource_list, ",")+"]被重新部署(id="+ret+")"), client);
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	/**
	 * 
	 */
	@Override
	public void unDeploy(List<String> deploy_id_list,SecuritySession client) throws BusinessError {
		for(String deploy_id : deploy_id_list){
			repositoryService.deleteDeployment(deploy_id);	
		}
		log("BPM.PD.UNDEPLOY","["+StringUtils.join(deploy_id_list, ",")+"]被删除部署",client);
	}
	/**
	 * 
	 */
	@Override
	public List<JSONObject> fetch(JSONObject param, SecuritySession client) throws BusinessError {
		try {
			param = JOHelper.cleanEmptyStr(param);
			if(VOHelper.has("name", param)){
				param.put("name","%"+param.get("name")+"%");
			}
			if(VOHelper.has("code", param)){
				param.put("code","%"+param.get("code")+"%");
			}
			StringBuilder sql = new StringBuilder("select a.* from act_re_procdef a ");
			sql.append(" where 1=1 ");
			sql.append(" $[and a.KEY_ like ${code}] ");
			sql.append(" $[and a.NAME_ like ${name}] ");
			if(VOHelper.has("TASK_ID", param)){
				sql.append(" and exists (select 1 from act_ru_task b where 1=1 b.proc_def_id_ = a.proc_def_id_ and b.id_=${task_id})");
			}
			sql.append(" order by a.key_,a.deployment_id_ ");
			List<JSONObject> list = _commondao.query(sql, param);
			return list;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	@Override
	public JSONObject findDeployModel(String deploy_id,SecuritySession client)throws BusinessError{
		try {
			StringBuilder sql = new StringBuilder(" select a.*,b.* ");
			sql.append(" from act_re_procdef a left join act_ge_bytearray b on (a.deployment_id_=b.deployment_id_) ");
			sql.append(" where 1=1 and b.generated_=0 ");
			sql.append(" and b.deployment_id_=${deployment_id} ");
			JSONObject ret = _commondao.find(sql, JO.gen("deployment_id",deploy_id));
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	/**
	 * 
	 * @param processDefinitionId
	 * @return
	 * @throws BuzException
	 */
	@Override
	public JSONObject getStartFormValue(String processDefinitionId) throws BusinessError {
		try {
			StartFormData tfd = processEngine.getFormService().getStartFormData(processDefinitionId);
			List<FormProperty> list = tfd.getFormProperties();
			JSONObject ret = new JSONObject();
			for (FormProperty fp : list) {
				ret.put(fp.getName(), fp.getValue());
			}
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	/**
	 * 将deploy_id_list指定的部署版本更新为deploy_id_target
	 * @param deploy_id_list_target 需要变更的部署版本清单，如果为空，就以deploy_id_source所在proc_def所有的版本都刷了
	 * @param deploy_id_source 指定的流程模板
	 * @throws Exception
	 */
	@Override
	public void syncVersion(String deploy_id_source,List<String> deploy_id_list_target) throws Exception {
		//
		JSONObject p = JO.gen("deploy_id",deploy_id_source);
		StringBuilder sql = new StringBuilder(" select id_ as proc_def_id,deployment_id_ as deploy_id,key as proc_def_code from act_re_procdef where deployment_id_=${deploy_id} ");
		JSONObject procdef = _commondao.find(sql, p,"没有找到指定部署的deploy_id="+deploy_id_source+"流程定义");
		p.put("proc_def_code",procdef.get("proc_def_code"));
		//
		sql = new StringBuilder(" select bytes_ from act_ge_bytearray where generated=0 and deployment_id_=${deploy_id} ");//得到流程模板
		JSONObject ba_inst = _commondao.find(sql, p,"没有找到指定部署的deploy_id="+deploy_id_source+"流程部署");
		//
		StringBuilder sql_update = new StringBuilder("update act_ge_bytearray set bytes_=${bytes}");
		sql_update = new StringBuilder(" where 1=1 and generated_=0 and name_=${proc_def_code} ");//如果指定了就再加deployment_id_
		//
		if(CollectionUtils.isEmpty(deploy_id_list_target)){//如果没有就去找所有的
			
		}else{
			sql_update.append(" and deployment_id_ ");
			sql_update.append(DaoHelper.Sql.in("deploy_id", deploy_id_list_target.size()));
			p = DaoHelper.Sql.in(p, deploy_id_list_target, "deploy_id");
		}
		p.put("bytes", ba_inst.get("bytes"));
		_commondao.executeChange(sql_update, p);
		//清除缓存
		processConfig.getDeploymentManager().getProcessDefinitionCache().remove(procdef.getString("proc_def_id"));		
	}
}
