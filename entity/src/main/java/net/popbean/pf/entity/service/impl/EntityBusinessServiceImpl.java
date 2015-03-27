package net.popbean.pf.entity.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.IValueObject;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.helper.JOHelper;
import net.popbean.pf.entity.helper.VOHelper;
import net.popbean.pf.entity.model.EntityModel;
import net.popbean.pf.entity.model.FieldModel;
import net.popbean.pf.entity.model.RelationModel;
import net.popbean.pf.entity.model.helper.EntityModelHelper;
import net.popbean.pf.entity.service.EntityBusinessService;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.exception.ErrorBuilder;
import net.popbean.pf.persistence.helper.DaoHelper;
/**
 * 
 * @author to0ld
 *
 */
@Service("service/pf/entity")
public class EntityBusinessServiceImpl extends AbstractBusinessService implements EntityBusinessService<String> {
	//FIXME 缺乏支持校验的delete方法以及valid的方法
	
	public EntityModel findModel(String entity_code) throws BusinessError {
		try {
			//1- 查主表，子表
			StringBuilder sql = new StringBuilder("select * from pb_pf_entity where code=${code}");
			EntityModel model = _commondao.find(sql, JO.gen("code",entity_code),EntityModel.class,"没有找到编码为"+entity_code+"的实体");
			sql = new StringBuilder("select * from pb_pf_field where entity_id=${id}");
			List<FieldModel> fields = _commondao.query(sql, JO.gen("id",model.id),FieldModel.class);
			model.field_list = fields;
			return model;
		} catch (Exception e) {
			ErrorBuilder.process(e);
		}
		return null;
	}

	public <T extends IValueObject> String saveData(T vo, boolean withChild) throws BusinessError {
		//FIXME 暂时先不考虑子集的情况
		try {
			if(withChild){
				throw new UnsupportedOperationException("目前暂不支持带子集的处理");
			}
			String pk_value = _commondao.save(vo);
			return pk_value;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	/**
	 * 
	 */
	public <T extends IValueObject> int deleteData(T vo,boolean withChild) throws BusinessError {
		try {
			if(withChild){
				throw new UnsupportedOperationException("目前暂不支持带子集的处理");
			}
			int ret = _commondao.delete(vo);
			return ret;			
		} catch (Exception e) {
			processError(e);
		}
		return 0;
	}

	public <T extends IValueObject> T findData(T vo, boolean withChild) throws BusinessError {//如果是核心实体，你这是要作死啊
		try {
			if(withChild){
				throw new UnsupportedOperationException("目前暂不支持带子集的处理");
			}
			T ret = _commondao.find(vo, null);
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}

	public <T extends IValueObject> List<T> fetchData(JSONObject condition, Class<T> clazz) throws BusinessError {
		try {
			List<T> ret = _commondao.query(condition, clazz);
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return new ArrayList<T>();
	}
	/**
	 * 
	 */
	@Override
	public List<RelationModel> fetchRelation(String entity_code) throws BusinessError {
		try {
			//以左侧或者右侧为基准查找
			StringBuilder sql = new StringBuilder("select * from pb_pf_relation where 1=1 and (entity_code_main=${code} or entity_code_slave=${code})");
			List<RelationModel> ret = _commondao.query(sql, JO.gen("code",entity_code), RelationModel.class);
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return new ArrayList<>();
	}

}
