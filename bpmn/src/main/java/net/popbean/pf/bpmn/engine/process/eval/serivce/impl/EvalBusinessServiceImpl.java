package net.popbean.pf.bpmn.engine.process.eval.serivce.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.popbean.pf.bpmn.engine.process.instance.service.ProcessInstanceBusinessService;
import net.popbean.pf.bpmn.engine.task.service.TaskInstanceBusinessService;
import net.popbean.pf.bpmn.helper.BpmnHelper;
import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.dataset.service.DataSetBusinessService;
import net.popbean.pf.dataset.vo.DataSetModel;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.helper.JOHelper;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.exception.ErrorBuilder;
import net.popbean.pf.persistence.helper.DaoHelper;
import net.popbean.pf.security.service.AuthenticationBusinessService;
import net.popbean.pf.security.vo.SecuritySession;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author to0ld
 *
 */
@Service("service/pf/bpmn/process/eval")
public class EvalBusinessServiceImpl extends AbstractBusinessService {
	//
	@Autowired
	@Qualifier("service/pf/security/auth")
	AuthenticationBusinessService authService;
	@Autowired
	@Qualifier("processEngine")
	ProcessEngine processEngine;
	//
	@Autowired
	@Qualifier("runtimeService")
	protected RuntimeService runtimeService;
	@Autowired
	@Qualifier("taskService")
	protected TaskService taskService;//FIXME 应该去掉taskservice 的直接引用
	//
	@Autowired
	@Qualifier("service/pf/bpmn/process/instance")
	protected ProcessInstanceBusinessService piService;
	@Autowired
	@Qualifier("service/pf/bpmn/task/instance")
	protected TaskInstanceBusinessService tiService;
	@Autowired
	@Qualifier("service/pf/dataset")
	protected DataSetBusinessService dsService;
	//
	public JSONObject parseScript(JSONObject inst)throws Exception{
		JSONObject ret = JO.gen("code",inst.getString("code"),"pd",inst.getString("pd"),"memo",inst.getString("memo"));
		if(JOHelper.equalsStringValue(ret, "code", "cm_cpo_svc013")){
			System.out.println();
		}
		//1-制单人的切割：首先按照空格先切割开，奇数位作为key：作为type识别一下：owner;org;job;
		String pk_account_crt = inst.getString("pk_account_crt");
		//
		List<String> account_list = splitParam(pk_account_crt);
		
		String type = account_list.get(0);
		String value = account_list.get(1);
		pk_account_crt = buildCandidator(type, value,null);
		StringBuilder sql = new StringBuilder("select * from mt_bd_account where account_code=${ACCOUNT_CODE}");
		JSONObject account = _commondao.find(sql, JO.gen("ACCOUNT_CODE",pk_account_crt));
		//以下部分抽象为一个方法:public String buildCandidator(String type,String type)
		ret.put("PK_ACCOUNT_CRT_CODE",pk_account_crt);
		ret.put("PK_ACCOUNT_CRT",account.getString("PK_ACCOUNT"));
		pk_account_crt = account.getString("PK_ACCOUNT");
		//2-bill的切割:根据pd来找属性
		String pd = inst.getString("pd").trim();
		sql = new StringBuilder(" select * from mt_pf_pd where pd_key=${PD_KEY} ");
		JSONObject pd_inst = _commondao.find(sql, JO.gen("PD_KEY",pd));
		String pk_key = pd_inst.getString("PK_KEY");
		
		sql = new StringBuilder(" select a.* from pb_pf_pd_param a left join mt_pf_pd b on (a.pk_pd=b.pk_pd) ");
		sql.append(" where 1=1 and b.pd_key=${PD_KEY} ");
		List<JSONObject> param_list = _commondao.query(sql, JO.gen("PD_KEY",pd));
		JSONObject param_bus = new JSONObject();
		for(JSONObject v:param_list){
			param_bus.put(v.getString("NAME"),v);
		}
		String bill = inst.getString("bill");
		JSONObject work_item = new JSONObject();
		List<String> bill_field_list = splitParam(bill);
		for(int i=0,len=bill_field_list.size();i<len;i+=2){
			String key = bill_field_list.get(i);
			JSONObject p = param_bus.getJSONObject(key);
			if(p != null){
				String code = p.getString("CODE");
				if(JOHelper.equalsStringValue(p, "TYPE", "T_REF")){
					String rangeset = p.getString("RANGESET");
					if(StringUtils.isBlank(rangeset)){
						ErrorBuilder.createSys().msg("没有找到"+key+"="+bill_field_list.get(i+1)+"指定的值域，可能是pd_param配置错误的问题").execute();
					}
					DataSetModel model = dsService.findModel(rangeset, null, false, false, null,null);
					String pk_field = model.pk_field;
					//
					if(bill_field_list.get(i+1).equals("销售部")){
						System.out.println();
					}
					if(code.indexOf("PK_DEPT")!=-1){//如果有部门主键，就分拆开处理
						value = bill_field_list.get(i+1);
						value = value.replaceAll("：", ":");//取消冒号
						String[] tmp = value.split(":");
						//得到第一级的pk
						if(tmp.length>1){
							StringBuilder find_sql = new StringBuilder(" select org_id from pf_bd_org where org_name=${ORG_NAME} ");
							JSONObject parent_dept = _commondao.find(find_sql, JO.gen("ORG_NAME",tmp[0]),"没有找到"+tmp[0]);
							String pk_parent = parent_dept.getString("PK_DEPT");
//							find_sql = new StringBuilder("select pk_dept from mt_bd_dept where seriescode like ${SERIESCODE} and dept_name=${DEPT_NAME}");
//							VO current = _commondao.find(find_sql, VO.gen("SERIESCODE","%"+pk_parent+"%","DEPT_NAME",tmp[tmp.length-1]),"没有找到"+tmp[tmp.length-1]);
							find_sql = new StringBuilder("select pk_dept from mt_bd_dept where concat(seriescode,'/') like ${SERIESCODE} and dept_name=${DEPT_NAME}");
							JSONObject current = _commondao.find(find_sql, JO.gen("SERIESCODE","%/"+pk_parent+"/%","DEPT_NAME",tmp[tmp.length-1]),"没有找到"+tmp[tmp.length-1]);
							String pk_dept = current.getString("PK_DEPT");
							work_item.put(code,pk_dept);
						}else{
							StringBuilder find_sql = new StringBuilder("select pk_dept from mt_bd_dept where 1=1 and dept_name=${DEPT_NAME}");
							JSONObject current = _commondao.find(find_sql, JO.gen("DEPT_NAME",tmp[tmp.length-1]),"没有找到"+tmp[tmp.length-1]);
							String pk_dept = current.getString("PK_DEPT");
							work_item.put(code,pk_dept);
						}
					}else{
						List<JSONObject> tmp = dsService.fetchPagingRangeList(rangeset, JO.gen("FILTER",bill_field_list.get(i+1),"LOGIN_PK_ORG","sankuai"), false, null, null);
						if(CollectionUtils.isEmpty(tmp)){
							ErrorBuilder.createSys().msg("没有找到"+key+"="+bill_field_list.get(i+1)+"(rangeset="+rangeset+")的对应值域").execute();
						}
						JSONObject vv = tmp.get(0);//还得定位主键的key才能提取出来
						String pk = vv.getString(pk_field);
						work_item.put(code,pk);						
					}

				}else if(JOHelper.equalsStringValue(p, "TYPE", "T_MONEY")|| JOHelper.equalsStringValue(p, "TYPE", "T_DECIMAL")){
//					ret.set(code,bill_field_list.get(i+1));	
					work_item.put(code,new BigDecimal(bill_field_list.get(i+1)) );
				}else if(JOHelper.equalsStringValue(p, "TYPE", "T_STAT")){
					String rangeset = p.getString("RANGESET");
					if(!StringUtils.isBlank(rangeset)){
						String[] pairs = rangeset.split("@");
						for(String pair:pairs){
							String[] kv = pair.split(":");
							if(bill_field_list.get(i+1).equals(kv[1])){
								work_item.put(code,Integer.valueOf(kv[0]));
							}
						}
					}
				}else{
					work_item.put(code,bill_field_list.get(i+1));
				}
			}else{
				ErrorBuilder.createSys().msg("测试用例("+inst.getString("code")+")中没有在bill中，找到key＝"+key+"的参数(pd_parm中没有这个key:"+JSON.toJSONString(param_bus)+")，请核对一下流程测试工具要求的key是否齐备").execute();
			}
		}
		work_item.put("PK_ACCOUNT_CRT",pk_account_crt);
		work_item.put("pk_account_crt",pk_account_crt);
		work_item.put(pk_key.toUpperCase(),"pk_work_item");
		ret.put("bill",work_item);
		ret.put("work_item",work_item);
		//
		String step = inst.getString("step");
		step = step.replaceAll(",", "");
		List<String> list = splitParam(step);
		List<String> step_list = new ArrayList<>();
		for(int i=0,len=list.size();i<len;i+=2){
			String t = list.get(i);//类型
			String v = list.get(i+1);//编码
			String rs = buildCandidator(t,v,pk_account_crt);
			if(!StringUtils.isBlank(rs)){
				step_list.add(rs);
			}
		}
		ret.put("step",step_list);
		ret.put("pk_work_item","pk_work_item");
		ret.put("PK_WORK_ITEM","pk_work_item");
		ret.put(pk_key.toUpperCase(),"pk_work_item");
		return ret;
	}
	/**
	 * 根据指定的路径执行
	 * @param work_item
	 * @param client
	 * @return
	 * @throws BuzException
	 */
	public JSONObject scriptPath(JSONObject script,SecuritySession client)throws BusinessError{
		String proc_inst_id = null;
		boolean isEnd = true;
		List<String> real_list = new ArrayList<>();
		JSONObject inst = null;
		try {
			//分解数据
			inst = parseScript(script);
			inst.put("status",0);//默认失败，成功了再改
			String pd = inst.getString("pd");
			JSONObject work_item = inst.getJSONObject("work_item");
			work_item.put(BpmnHelper.STAT_TEST, BpmnHelper.STAT_TEST);
			//得到启动者的编码
			String acc_code = inst.getString("PK_ACCOUNT_CRT_CODE");
			//
			SecuritySession env = authService.buildSession(acc_code);
			work_item.put("PK_ACCOUNT_CRT_NAME", env.account_name);
			work_item.put("pk_account_crt", env.account_id);// 请暂时无视这个悲剧
			work_item.put("pk_account_crt_code", env.account_code);
			if(!JOHelper.has("pk_work_item", work_item)){
				work_item.put("pk_work_item", "pk_work_item");
			}
			if(!JOHelper.has("pk_value_main", work_item)){
				work_item.put("pk_value_main", "pk_work_item");
			}
			//
			List<String> step_list = (List<String>)inst.get("step");
			proc_inst_id = piService.start(pd, work_item, "bpm test", env);
			boolean flag = true;
			int pos = 0;
			while(flag){
				if( (step_list.size()) <= pos+1 ){
					flag = false;//保护一下吧
				}
				String candidate = step_list.get(pos++);
				Task t = taskService.createTaskQuery().processInstanceId(proc_inst_id).taskCandidateUser(candidate).singleResult();
				if(t == null){
					Task task = taskService.createTaskQuery().processInstanceId(proc_inst_id).singleResult();
					if(task!=null){
						List<IdentityLink> id_list = taskService.getIdentityLinksForTask(task.getId());
						String candidator = "";
						for(IdentityLink il:id_list){
							candidator += "#"+il.getUserId();
						}
						real_list.add(task.getTaskDefinitionKey()+"#"+candidator);						
					}else{
						
					}

					flag = false;
					isEnd = false;
					break;
				}else{
					real_list.add(t.getTaskDefinitionKey()+"-"+candidate);
					env = authService.buildSession(candidate);
					tiService.commit(t.getId(), "test(from bpm test)", env);//FIXME 自由行的意思是随意哈
				}
			}
			//执行
			if(real_list.size() != step_list.size()){
				inst.put("status",0);
				inst.put("real_list",real_list);
			}else{
				inst.put("status",1);
			}
			//匹配结果
			return inst;
		} catch (Exception e) {
			processError(e);//原样出去就好
		}finally{
			if(!StringUtils.isBlank(proc_inst_id)){
				inst.put("real_list",real_list);
				if(isEnd){
					piService.delete(proc_inst_id, false, client);
				}else{
					runtimeService.deleteProcessInstance(proc_inst_id, "发起人撤回");//清除	
				}
				try {
					StringBuilder sql = new StringBuilder("delete from rlt_procinst_account where proc_inst_id=${PROC_INST_ID}");
					_commondao.executeChange(sql, JO.gen("PROC_INST_ID",proc_inst_id));					
				} catch (Exception e2) {
					processError(e2);
				}

			}
		}
		return inst;
	}
	public List<List<JSONObject>> freePath(JSONObject workitem,SecuritySession client)throws BusinessError{
		List<List<JSONObject>> ret = new ArrayList<List<JSONObject>>();
		String proc_inst_id = null;
		boolean isEnd = false;//是否正常结束
		try {
			workitem.put(BpmnHelper.STAT_TEST, BpmnHelper.STAT_TEST);//FIXME 就当是一个标志，有了就不发消息得了
			String pd_key = workitem.getString("PD_KEY");
			//
			//FIXME 需要获得第一个人的client，而不是直接使用传入的client
			StringBuilder sql_find = new StringBuilder(" select account_code from mt_bd_account where pk_account=${PK_ACCOUNT_CRT} ");
			JSONObject startor = _commondao.find(sql_find, workitem);
			String acc_code = startor.getString("ACCOUNT_CODE");
			//
			SecuritySession env = authService.buildSession(acc_code);
			//
			workitem.put("PK_ACCOUNT_CRT_NAME", env.account_name);
			workitem.put("pk_account_crt", env.account_id);// 请暂时无视这个悲剧
			workitem.put("pk_account_crt_code", env.account_code);
			if(!JOHelper.has("pk_work_item", workitem)){
				workitem.put("pk_work_item", "pk_work_item");
			}
			if(!JOHelper.has("pk_value_main", workitem)){
				workitem.put("pk_value_main", "pk_work_item");
			}
			if(env.org == null){
				workitem.put("ORG_DEPT",env.org.id);
				workitem.put("ORG_NAME",env.org.name);
			}
			//
			proc_inst_id = piService.start(pd_key, workitem, "bpm test", env);
			//
			
			Task t = taskService.createTaskQuery().processInstanceId(proc_inst_id).singleResult();
			int loop = 0;//如果到了20都没玩，咱就滚蛋回家吧
			while(t!=null){
				long start = System.currentTimeMillis();//开始时间
				List<IdentityLink> id_list = taskService.getIdentityLinksForTask(t.getId());
				if(!CollectionUtils.isEmpty(id_list)){
					//拼写成一个串,写到字符串中处理
					List<String> pk_account_list = new ArrayList<String>();
					for(IdentityLink il:id_list){
						pk_account_list.add(il.getUserId());
					}
					StringBuilder sql = new StringBuilder(" select a.account_code,c.dept_name,a.account_name");
					sql.append(" from pb_bd_account a left join rlt_account_dept b on (a.pk_account=b.pk_account and b.itype=1)");
					sql.append(" left join mt_bd_dept c on (b.pk_dept=c.pk_dept) ");
					sql.append(" where 1=1 and a.account_code  ").append(DaoHelper.Sql.in("ACCOUNT_CODE", pk_account_list.size()));
					JSONObject p = new JSONObject();
					p = DaoHelper.Sql.in(p, pk_account_list, "ACCOUNT_CODE");
					List<JSONObject> acc_list = _commondao.query(sql, p);//得到候选人列表
					if(CollectionUtils.isEmpty(acc_list)){
						System.out.println("数据没找到，肯定也是有问题的");
					}else{
						JSONObject acc_inst = acc_list.get(0);
						acc_inst.put("TASK_ID",t.getId());
						acc_inst.put("TASK_NAME",t.getName());
						acc_inst.put("TASK_DESC",t.getDescription());
						acc_code = acc_inst.getString("ACCOUNT_CODE");
						env = authService.buildSession(acc_code);
						tiService.commit(t.getId(), "test(from bpm test)", env);//FIXME 自由行的意思是随意哈
						t = taskService.createTaskQuery().processInstanceId(proc_inst_id).singleResult();
						//
						acc_list.get(0).put("EXEC_TIME",(System.currentTimeMillis()-start));
						ret.add(acc_list);
					}
					if(t == null){
						isEnd = true;
					}
					if(loop++>20){
						break;
					}
				}else{//这是异常把
//					System.out.println("这肯定是有问题的(task_id="+t.getId()+")");
				}
			}
			return ret;//如何标定其
		} catch (Exception e) {
			processError(e);
		}finally{
			//FIXME 清除一下数据，省得麻烦(咱手工清除咋样?)
			if(!StringUtils.isBlank(proc_inst_id)){
				piService.delete(proc_inst_id, false, client);
				if(isEnd){
					piService.delete(proc_inst_id, false, client);
				}else{
					runtimeService.deleteProcessInstance(proc_inst_id, "发起人撤回");//清除	
				}
				StringBuilder sql = new StringBuilder("");
			}
		}
		return null;//FIXME 不行就返回一个size=0
	}
	public String buildCandidator(String type,String value,String pk_account_crt)throws BusinessError{
		try {
			String ret = null;
			value = value.replace("：", ":");//排除双字节的流氓
			if("owner".equals(type)){//指定部门的负责人
				value = value.replaceAll("：", ":");//先干掉双字节的
				String[] tmp = value.split(":");//切割一下，防止外卖城市和团购城市的处理
				JSONObject rs = null;
				if(tmp.length>1){
					StringBuilder sql = new StringBuilder("select org_id from pb_bd_org where org_name=${ORG_NAME}");//得到第一个部门主键
					JSONObject parent = _commondao.find(sql, JO.gen("ORG_NAME",tmp[0]),"没有找到org_name="+value+"的员工");
					//
					sql = new StringBuilder("select pk_account_owner_code,pk_account_owner,pk_account_owner_name from pb_bd_org where org_name=${ORG_NAME}");
					
					sql.append(" and seriescode like ${SERIESCODE} ");
					rs = _commondao.find(sql, JO.gen("ORG_NAME",tmp[tmp.length-1],"SERIESCODE","%/"+parent.getString("PK_DEPT")+"/%"));
				}else{
					StringBuilder sql = new StringBuilder("select pk_account_owner_code,pk_account_owner,pk_account_owner_name from mt_bd_dept where dept_name=${PK_DEPT_NAME}");
					List<JSONObject> t_list = _commondao.query(sql, JO.gen("PK_DEPT_NAME",value));
					if(t_list.size()>1){
						ErrorBuilder.createSys().msg("type="+value+" value="+value+"不止一个值").execute();
					}
					rs = t_list.get(0);
				}

				if(rs != null){
					ret = rs.getString("PK_ACCOUNT_OWNER_CODE");
				}
			}else if("org".equals(type)){//find即可；不是owner
				value = value.replaceAll("：", ":");//先干掉双字节的
				String[] tmp = value.split(":");//切割一下，防止外卖城市和团购城市的处理
				if(tmp.length>1){
					StringBuilder sql = new StringBuilder("select pk_dept from pb_bd_org where org_name=${ORG_NAME}");//得到第一个部门主键
					JSONObject parent = _commondao.find(sql, JO.gen("ORG_NAME",tmp[0]),"没有找到org_name="+value+"的账号");
					//
					sql = new StringBuilder(" select * from rlt_account_dept where pk_dept_name=${ORG_NAME} and seriescode like ${SERIESCODE}");
					sql.append(" and pk_account not in (select pk_account_owner from mt_bd_dept where dept_name=${PK_DEPT_NAME})");//排除owner
					sql.append(" and pk_account in (select pk_account from mt_bd_account where istat!=-5) ");
					sql.append(" and pk_account_code is not null ");
					
					JSONObject rs = _commondao.find(sql, JO.gen("ORG_NAME",tmp[tmp.length-1],"SERIESCODE","%/"+parent.getString("PK_DEPT")+"/%"),"没有找到org_name="+value+"的员工");
					ret = rs.getString("PK_ACCOUNT_CODE");
				}else{
					StringBuilder sql = new StringBuilder(" select * from rlt_account_dept where org_name=${ORG_NAME} ");
					sql.append(" and pk_account not in (select account_owner_id from pb_bd_org where ORG_name=${ORG_NAME}) ");
					sql.append(" and pk_account in (select account_id from pb_bd_account where istat!=-5) ");
					JSONObject rs = _commondao.find(sql, JO.gen("PK_DEPT_NAME",value),"没有找到pk_dept_name="+value+"的员工");
					ret = rs.getString("PK_ACCOUNT_CODE");
				}
			}else if("job".equals(type)){//FIXME 
				//coo;ceo
				String account_code = "";
				if("coo".equals(value)){
					ret = "ganjiawei";
				}else if("ceo".equals(value)){
					ret = "wangxing";
				}
			}else if("code".equals(type)){//找到指定的人
				return value;
			}else if("acc_owner".equals(type)){
				StringBuilder sql = new StringBuilder(" select pk_account_owner_code from rlt_account_dept where pk_account=${PK_ACCOUNT} ");
				JSONObject inst = _commondao.find(sql, JO.gen("PK_ACCOUNT",pk_account_crt));
				if(inst != null){
					ret = inst.getString("PK_ACCOUNT_OWNER_CODE");
				}
			}else{
				ErrorBuilder.createSys().msg("无法解析的关键字:"+type+"(其值为"+value+")").execute();
			}
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	public List<String> splitParam (String content)throws Exception{//切割参数
		String[] tmp = content.split(" ");
		List<String> ret = new ArrayList<>();
		for(String t:tmp){
			if(!t.trim().isEmpty()){
				ret.add(t.trim());
			}
		}
		if(ret.size()%2!=0){
			ErrorBuilder.createSys().msg("奇数位，不匹配").execute();
		}
		return ret;
	} 
}
