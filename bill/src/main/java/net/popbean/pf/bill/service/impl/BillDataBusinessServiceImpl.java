package net.popbean.pf.bill.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.popbean.pf.attachement.vo.RltAttVO;
import net.popbean.pf.bill.helpers.BillConst;
import net.popbean.pf.bill.helpers.BillModelHelper;
import net.popbean.pf.bill.service.BillDataBusinessService;
import net.popbean.pf.bill.service.BillDataExtendBusinessService;
import net.popbean.pf.bill.service.BillModelBusinessService;
import net.popbean.pf.bill.vo.BillEntityModel;
import net.popbean.pf.bill.vo.BillModel;
import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.helper.JOHelper;
import net.popbean.pf.entity.impl.AbstractValueObject;
import net.popbean.pf.entity.model.EntityModel;
import net.popbean.pf.entity.model.FieldModel;
import net.popbean.pf.entity.model.RelationModel;
import net.popbean.pf.entity.service.EntityBusinessService;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.exception.ErrorBuilder;
import net.popbean.pf.persistence.helper.DaoConst.Paging;
import net.popbean.pf.persistence.helper.DaoHelper;
import net.popbean.pf.security.vo.SecuritySession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
@Service("service/pf/bill/data")
public class BillDataBusinessServiceImpl extends AbstractBusinessService implements BillDataBusinessService {
	@Autowired
	private ApplicationContext context;
	//
	@Autowired
	@Qualifier("service/pf/bill/model/redis")
	BillModelBusinessService bmService;
	@Autowired
	@Qualifier("service/pf/entity")
	EntityBusinessService<String> entityService; 
	//
	@CacheEvict(value="service/bill/data",key="#code+'-'+#stage+'-'+#data['id']")
	@Override
	public String save(String code, String stage, boolean forceValid, JSONObject data, SecuritySession session) throws BusinessError {
		if (data == null || data.size() == 0) {
			return "";
		}
		//这样会导致操作数据的丢失，应该给出特定的异常进行处理
		//FIXME 1-不利于soa
		//FIXME 2-如果真的需要判断，应该定义一个拦截器来做，只要方法中包含了Session参数，或者有@Auth就进行判断
		if(session == null){
			ErrorBuilder.createBusiness().msg("尚未登录，无法进行后续的操作，请登录后再试").execute();
		}
		try {
			//加一个锁，假定每个登陆id，每次只能保存一个单据
			lockService.lock(code, session.account_id);
			//
			BillModel model = bmService.find(code, stage, false, session);
			BillDataExtendBusinessService ext = getExt(model);
			EntityModel em = entityService.findModel(model.main.code);
			String pkField = em.findPK().code;//可能会有没主键的，这么写不好
			data = JOHelper.fixType(em.field_list, data);//纠正一下类型的问题
			String pkValue = data.getString(pkField);
			boolean isAdd = false;
			if (StringUtils.isBlank(pkValue)) {
				isAdd = true;
				data.put(pkField, null);
				pkValue = null;
			}
			// FIXME: 需要把子集的引用值全改为pkValue，目前是在entityService里处理的
			if (ext != null) {
				ext.validForSave(model, data, isAdd, session);
				// 允许使用返回值，但是如果为null就不使用
				JSONObject newData = ext.beforeSave(model, data, isAdd, session);
				if (newData != null) {
					data = newData;
				}
			}
			String dataId = _commondao.save(em, data, true, false, null);
//			String dataId = entityService.saveData(data, false);//(data, pkEntity, true, session);
			//
			List<BillEntityModel> slaves = model.slaves;
			Map<String,BillEntityModel> slave_bus = new HashMap<>();
			for(BillEntityModel bem:slaves){
				slave_bus.put(bem.code, bem);
			}
			//
			List<RelationModel> rlt_list = entityService.fetchRelation(em.code);
			if(isAdd){//新增
				//分为直连和桥接两种(桥接中分为强引用，弱引用两种)

			}else{//修改
				//先删除数据再说
				for(RelationModel rm:rlt_list){
					BillEntityModel bem = slave_bus.get(rm.code);
					if(bem == null){//没有这个关系就撤吧
						continue;
					}
					if(RelationModel.TYPE_BRIDGE.equals(rm.type)){
						if(!BillEntityModel.REF_TYPE_WEAK.equals(bem.ref_type)){
							//删除子表数据
							//delete from slave where id in (select xx from bridge where main_id=${id})
							StringBuilder delete_slave = new StringBuilder(" delete from "+rm.entity_code_slave+" where "+bem.findPk().code+" in ( select "+rm.id_key_slave+" from "+rm.code+"   ");
							delete_slave.append(" where "+rm.id_key_main+"=${id} ) ");
							_commondao.executeChange(delete_slave,JO.gen("id",dataId));
						}
						//删除桥数据
						StringBuilder delete_bridge = new StringBuilder("delete from "+bem.code+" where "+rm.id_key_main+"=${id}");
						_commondao.executeChange(delete_bridge,JO.gen("id",dataId));
					}else{//如果不是桥接就直接删除
						StringBuilder sql = new StringBuilder(" delete from "+rm.code+" where "+rm.id_key_slave+"=${id} ");
						_commondao.executeChange(sql,JO.gen("id",dataId));
					}
				}
			}
			//写入子集信息
			Map<String,RelationModel> rlt_bus = new HashMap<>();
			for(RelationModel rm:rlt_list){
				rlt_bus.put(rm.entity_code_slave, rm);
			}
			for(BillEntityModel bem:slaves){
				if("rlt_attachement".equals(bem.code)){//附件单独处理
					continue;
				}
				RelationModel rm = rlt_bus.get(bem.code);
				EntityModel tmp = BillModelHelper.convert(bem,rm);
				List<JSONObject> list = JOHelper.ja2list(data.getJSONArray(bem.code));
				//
				String pk_field = bem.findPk().code;
				List<JSONObject> new_list = new ArrayList<>();
				for(JSONObject curror:list){//补齐主表的主键
					JSONObject t = JOHelper.fixType(tmp.field_list, curror);
					t.put(rm.id_key_slave, dataId);//补齐外键的主键，就算不用，也不误事
					t.put(pk_field, _commondao.genId());//补齐主键
					new_list.add(t);
				}
				_commondao.batchInsertJO(tmp, new_list, null);
				//
				if(RelationModel.TYPE_BRIDGE.equals(rm.type)){//桥接需要补一把数据
					List<JSONObject> bridge_list = new ArrayList<>();
					for(JSONObject curror:list){//补齐主表的主键
						JSONObject jo = JO.gen(rm.id_key_main,dataId,rm.id_key_slave,curror.getString(pk_field));
						bridge_list.add(jo);
					}
					_commondao.batchInsertJO(tmp, bridge_list, null);
				}else{//非桥接模式，直接写入(已经补齐外键)
				}
//				_commondao.batchInsertJO(tmp, list, null);
			}
			//
			@SuppressWarnings("unchecked")
			List<String> pkAtts = (List<String>) data.get("rlt_attachement");
			if (pkAtts != null) {
				List<RltAttVO> atts = new ArrayList<>();
				for (String pk : pkAtts) {
					RltAttVO inst = new RltAttVO();
					inst.business_id = dataId;
					inst.id = pk;
					atts.add(inst);
				}
				try {
					StringBuilder select_sql = new StringBuilder("select id from rlt_attachement where ref=${PK_BIZ} ");
					List<JSONObject> att_list = _commondao.query(select_sql, JO.gen("PK_BIZ", dataId));
					if(!CollectionUtils.isEmpty(att_list)){
						StringBuilder sql = new StringBuilder("delete from rlt_attachement where id=${PK_RLT_BIZ_ATT}");
						_commondao.batch(sql, att_list);
					}
					_commondao.batchInsert(atts);
				} catch (Exception e) {
					// what ?
					ErrorBuilder.createSys().msg("无法保存附件，请联系（向rlt_attachement表中保存数据时出错）" + e).execute();
				}
			}
			if (ext != null) {
				ext.afterSave(model, data, isAdd, session);
			}
			if (dataId == null) {
				return "";
			}
			//添加oplog
			if(isAdd){
				log(BillConst.OpLog.Data.ADD,JSON.toJSONString(data), session);	
			}else{
				log(BillConst.OpLog.Data.UP,JSON.toJSONString(data), session);
			}
			return dataId;
		} catch (Exception e) {
			processError(e);
		}finally{
			lockService.unlock(code, session.account_id);
		}
		return null;
	}

	@Override
	public void delete(String code, List<String> pk_list, SecuritySession env) throws BusinessError {
		try {
			if (pk_list == null) {
				pk_list = new ArrayList<String>();
			}
			if (pk_list.size() == 0) {
				return ;
			}
			BillModel model = bmService.find(code, null, false, env);
			List<String> deleted = new ArrayList<String>();
			for (String dataId : pk_list) {
				// FIXME: 真的要查一遍数据，以获取ext?
				delete(model,dataId,env);
				deleted.add(dataId);
			}
			
		} catch (Exception e) {
			processError(e);
		}
		return ;
	}
	@CacheEvict(value="service/bill/data",key="#code+'-'+#stage+'-'+#pk")
	private void delete(BillModel model,String pk,SecuritySession session)throws Exception{
		StringBuilder sql = new StringBuilder("update "+model.main.code+" set status=-5 where id=${id}");
		JSONObject jo = JO.gen("id",pk);
		BillDataExtendBusinessService ext = getExt(model);
		if (ext != null) {
			ext.validForDelete(model, jo, session);
			ext.beforeDelete(model, jo, session);
		}
		_commondao.executeChange(sql, jo);
		if (ext != null) {
			ext.afterDelete(model, jo, session);
		}
		log(BillConst.OpLog.Data.DEL,JSON.toJSONString(jo), session);//删除之前不存一份，有点可惜哈，否则还能做一个美妙的还原
	}

	@Override
	public List<JSONObject> fetchSlaveData(String code, String stage, String pk_value_main, String slave_entity_code, SecuritySession session) throws BusinessError {
		try {
			BillModel model = bmService.find(code, stage, false, session);
			// 子集数据
			List<RelationModel> relaitons = entityService.fetchRelation(model.main.code);
			if (CollectionUtils.isEmpty(relaitons)) {
				return null;
			}
			for (RelationModel entity : relaitons) {
				if(slave_entity_code.equals(entity.entity_code_slave)){
					StringBuilder sql = new StringBuilder("select * from ");
					sql.append(entity.entity_code_slave);
					sql.append(" where  ").append(entity.id_key_slave).append("=${").append(entity.id_key_slave).append("}");
					List<JSONObject> ret = _commondao.query(sql, JO.gen(entity.id_key_slave, pk_value_main));
					return ret;
				}
//				TableMeta slave = entityService.findTableMeta(entity.getString("PK_ENTITY_SLAVE_CODE"));
//				if (slave == null) {
//					continue;
//				}
//				String tableCode = slave.getTableCode();
				// 如果不要求返回，就直接跳过
//				if (!slaveEntityCodes.contains(tableCode)) {
//					continue;
//				}
//				String fk_key = entity.getString("PK_FIELD_MAIN_CODE");

//				StringBuilder sql = new StringBuilder(DaoHelper.Sql.select(slave));
//				sql.append(" from  ").append(tableCode);
//				sql.append(" where  ").append(fk_key).append("=${").append(fk_key).append("}");
//				try {
//					slaveDatas.put(tableCode, _commondao.query(sql, VO.gen(fk_key, pkValue)));
//				} catch (Exception e) {
//					slaveDatas.put(tableCode, new ArrayList<VO>());
//				}
			}
			return new ArrayList<>();
		} catch (Exception e) {
			processError("根据单据中表的pk查询主表的基本信息时出错", e);
		}
		return null;
	}
	@Override
	public JSONObject findBillData(String code, String data_id, Boolean includeSlave, SecuritySession client) throws BusinessError {
		try {
			// 获取默认模型
			BillModel model = bmService.find(code, null, false, client);
			BillDataExtendBusinessService ext = getExt(model);
			// 获取实体
			JSONObject billData = findMainData(code, data_id, client);
			if(billData == null){
				ErrorBuilder.createSys().msg("单据"+code+"中找不到id="+data_id+"的数据").execute();
			}
			model = bmService.findById(code,data_id,false,client);//需要去解析
			
			if(ext!=null && ext.enableCustomFind()){//如果启用了自定义的查询，那就自己来吧
				billData = ext.findBillData(model, data_id, includeSlave, client);
				billData.put("X-MODEL", model);
				return billData;
			}
			billData.put("X-MODEL", model);
			if (!includeSlave) {
				return billData;
			}
			
			List<BillEntityModel> slaves = model.slaves;
			if (CollectionUtils.isEmpty(slaves)) {
				return billData;
			}
			for (BillEntityModel slave : slaves) {
				String slaveCode = slave.code;
				if (slaveCode == null) {
					slaveCode = slave.code;
				}
				if ("RLT_ATTACHEMENT".equalsIgnoreCase(slaveCode)) {
					StringBuilder sql = new StringBuilder();
					sql.append("select a.name, a.size, a.id as uuid from pb_bd_attachement a");
					sql.append(" left join rlt_attachement b on (a.id = b.attachement_id) ");
					sql.append(" where b.ref = ${PK}");
					try {
						List<JSONObject> rlt_att_list = _commondao.query(sql, JO.gen("PK", data_id));
						billData.put(slaveCode, rlt_att_list);
					} catch (Exception e) {
					}
					continue;
				}
				if (ext != null) {
					// 子集自定义数据扩展
					boolean flag = ext.enableSlaveDataCustomFetch(slaveCode); 
					if(flag){
						List<JSONObject> slaveData = ext.fetchSlave(model, data_id, slaveCode, client);
						billData.put(slaveCode, slaveData);
						continue;
					}else{//不用自定义就用默认的
					}
				}
			}
			for(BillEntityModel slave:slaves){
				List<JSONObject> slave_data = fetchSlaveData(code, model.stage, data_id, slave.code, client);
				billData.put(slave.code,slave_data);
			}
			return billData;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	public JSONObject findBillData(String code,String stage,String data_id, Boolean includeSlave, SecuritySession client) throws BusinessError {
		try {
			// 获取默认模型
			BillModel model = bmService.find(code, null, false, client);
			
			// 获取实体
			JSONObject billData = findMainData(code, data_id, client);
			if(billData == null){
				ErrorBuilder.createSys().msg("单据"+code+"中找不到id="+data_id+"的数据").execute();
			}
			model = bmService.findById(code,data_id,false,client);//需要去解析
			
			BillDataExtendBusinessService ext = getExt(model);
			if(ext!=null && ext.enableCustomFind()){//如果启用了自定义的查询，那就自己来吧
				billData = ext.findBillData(model, data_id, includeSlave, client);
				billData.put("X-MODEL", model);
				return billData;
			}
			billData.put("X-MODEL", model);
			if (!includeSlave) {
				return billData;
			}
			
			List<BillEntityModel> slaves = model.slaves;
			if (CollectionUtils.isEmpty(slaves)) {
				return billData;
			}
			for (BillEntityModel slave : slaves) {
				String slaveCode = slave.code;
				if (slaveCode == null) {
					slaveCode = slave.code;
				}
				if ("RLT_ATTACHEMENT".equalsIgnoreCase(slaveCode)) {
					StringBuilder sql = new StringBuilder();
					sql.append("select a.name, a.size, a.id as uuid from pb_bd_attachement a");
					sql.append(" left join rlt_attachement b on (a.id = b.attachement_id) ");
					sql.append(" where b.ref = ${PK}");
					try {
						List<JSONObject> rlt_att_list = _commondao.query(sql, JO.gen("PK", data_id));
						billData.put(slaveCode, rlt_att_list);
					} catch (Exception e) {
					}
					continue;
				}
				if (ext != null) {
					// 子集自定义数据扩展
					boolean flag = ext.enableSlaveDataCustomFetch(slaveCode); 
					if(flag){
						List<JSONObject> slaveData = ext.fetchSlave(model, data_id, slaveCode, client);
						billData.put(slaveCode, slaveData);
						continue;
					}else{//不用自定义就用默认的
					}
				}
			}
			for(BillEntityModel slave:slaves){
				List<JSONObject> slave_data = fetchSlaveData(code, model.stage, data_id, slave.code, client);
				billData.put(slave.code,slave_data);
			}
			return billData;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	private JSONObject findMainData(String code,String id,SecuritySession session)throws BusinessError{
		try {
			BillModel model = bmService.find(code, null, false, session);
			EntityModel em = entityService.findModel(model.main.code);//FIXME 晕头晕脑的，我也忘记了
			if (em == null) {
				return null;
			}
			//为了支持自定义的查询
			String id_key = em.findPK().code;
			JSONObject condition = JO.gen(id_key,id);
			BillDataExtendBusinessService ext = getExt(model);
			if(ext != null && ext.enableCustomFetch()){//
				List<JSONObject> ret = ext.fetchMain(model, condition, true, session);
				return CollectionUtils.isEmpty(ret)?null:ret.get(0);
			}
			//
			//
			StringBuilder sql = new StringBuilder(" select ");
			sql.append("a.*");
			sql.append(" from ").append(em.code).append(" a ");
			sql.append(" where 1=1 ");
			sql.append(" and ").append("a.").append(id_key).append("=${"+id_key+"}");
			JSONObject ret = _commondao.find(sql, JO.gen(id_key,id));
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	@Override
	public List<JSONObject> fetchMainData(String code, JSONObject condition, SecuritySession client) throws BusinessError {
		try {
			condition = JOHelper.cleanEmptyStr(condition);
			Paging paging = Paging.build(condition);
			BillModel model = bmService.find(code, null, false, client);
			//为了支持自定义的查询
			BillDataExtendBusinessService ext = getExt(model);
			if(ext != null && ext.enableCustomFetch()){//
				List<JSONObject> ret = ext.fetchMain(model, condition, true, client);
				return ret;
			}
			EntityModel em = entityService.findModel(model.main.code);
			if (em == null) {
				return null;
			}
			//
			Set<String> key_bus = new HashSet<>();
			for(FieldModel f:em.field_list){
				key_bus.add(f.code);
			}
			//
			StringBuilder sql = new StringBuilder(" select ");
			sql.append("a.*");
			sql.append(" from ").append(em.code).append(" a ");
			sql.append(" where 1=1 ");
			ArrayList<String> keys = new ArrayList<String>();
			for (String key : condition.keySet()) {
				keys.add(key);
			}
			for (String key : keys) {
				String originalKey = key;
				key = key.trim();
				Object value = condition.get(key);
				String upperKey = key.toUpperCase(Locale.US);
				//FIXME 这个命名规则可能不对
				if (upperKey.endsWith("_MIN") || upperKey.endsWith("_MAX") || upperKey.endsWith("_END")) {
					key = key.substring(0, key.length() - 4);
				} else if (upperKey.endsWith("_START")) {
					key = key.substring(0, key.length() - 6);
				}
				// 希望findField是Hash实现，要不然效率就大打折扣了
				if (!key_bus.contains(key)) {// 避免用户多传了值(如分页)，而无法查找
					continue;
				}
				if (upperKey.endsWith("STAT") && String.valueOf(AbstractValueObject.STAT_ALL).equals(condition.getString(key))) {
					// 约定999就是all
					continue;
				} else if (upperKey.endsWith("_START") || upperKey.endsWith("_MIN")) {
					sql.append(" and a.").append(key).append(">=${" + originalKey + "}");
				} else if (upperKey.endsWith("_END") || upperKey.endsWith("_MAX")) {
					sql.append(" and a.").append(key).append("<=${" + originalKey + "}");
				} else if ((upperKey.endsWith("_CODE") || upperKey.endsWith("_NAME")) && paging.totalCount != 1) {
					// 编码，名称啥的，支持模糊查询
					sql.append(" and a.").append(key).append(" like ${" + originalKey + "}");
					condition.put(key, "%" + String.valueOf(condition.get(originalKey)).trim() + "%");
				} else if (value instanceof List) {
					@SuppressWarnings("unchecked")
					List<Object> values = (List<Object>) value;
					sql.append(" and a.").append(key).append(" ").append(DaoHelper.Sql.in(key, values.size()));
					DaoHelper.Sql.in(condition, values, key);
				} else {
					sql.append(" and a.").append(key).append("=${" + originalKey + "}");
				}
			}
			sql.append(" and (a.status!=-5 or a.status is null)");
			List<JSONObject> list = _commondao.paging(sql, condition, paging);
			return list;
		} catch (Exception e) {
			processError(e);
		}
		return new ArrayList<JSONObject>();
	}
	private BillDataExtendBusinessService getExt(BillModel model) {
		String ext = model.ext;
		if (!StringUtils.isBlank(ext)) {
			try {
				return (BillDataExtendBusinessService) context.getBean(ext.trim());
			} catch (NoSuchBeanDefinitionException e) {
				// do nothing
			} catch (BeansException e) {
				// do nothing
			}
		}
		return null;
	}
}
