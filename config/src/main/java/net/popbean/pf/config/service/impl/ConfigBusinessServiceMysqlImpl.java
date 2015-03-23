package net.popbean.pf.config.service.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.config.service.ConfigBusinessService;
import net.popbean.pf.config.vo.ConfigFieldModel;
import net.popbean.pf.config.vo.ConfigModel;
import net.popbean.pf.config.vo.IConfigValueStore;
import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.id.helper.IdGenHelper;
import net.popbean.pf.security.vo.SecuritySession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
@Service("service/pf/config/mysql")
public class ConfigBusinessServiceMysqlImpl extends AbstractBusinessService implements ConfigBusinessService {
	//
	/**
	 * 
	 */
	@Override
	@Cacheable(value="service/pf/config",key="#app_code+'-'+#cfg_unique")
	public IConfigValueStore findValue(String app_code, String cfg_unique,SecuritySession env) throws BusinessError {
		try {
			//得到model
			ConfigModel model = findModel(app_code, cfg_unique, env);
			//根据model去拼写sql搞数据
			@SuppressWarnings("unchecked")
			Class<? extends IConfigValueStore> cl = (Class<? extends IConfigValueStore>)Class.forName(model.clazz);
			//要区分level
			JSONObject p = JO.gen("config_id",model.id);
			StringBuilder sql = new StringBuilder("select * from pb_pf_config_value where 1=1 ");
			sql.append(" and config_id=${config_id} ");
			if(ConfigModel.LEV_GLOBAL == model.level){//全局性

			}else if(ConfigModel.LEV_APP == model.level){
				sql.append(" and app_code=${app_code} ");
				p.put("app_code",app_code);
			}else if(ConfigModel.LEV_COMPANY == model.level){
				sql.append(" and company_id=${company_id} ");
				p.put("company_id",env.company.id);
			}else if(ConfigModel.LEV_ORG == model.level){
				sql.append(" and org_id=${org_id} ");
				p.put("org_id",env.org.id);
			}else if(ConfigModel.LEV_ACCOUNT == model.level){
				sql.append(" and account_id=${account_id} ");
				p.put("account_id",env.account_id);
			}
			//木有就找默认值
			IConfigValueStore ret = _commondao.find(sql, p, cl, null);
			return ret;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}

	@Override
	public IConfigValueStore changeValue(String app_code, String cfg_unique, IConfigValueStore value, SecuritySession env) throws BusinessError {
		try {
			ConfigModel model = findModel(app_code, cfg_unique, env);
			//将model.id,env中的各种owenr设置进去
			value.merge(model, env);
			_commondao.save(value);
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	/**
	 *  
	 */
	@CacheEvict(value="service/pf/config",key="#model.app_code+'-'+#model.code")
	@Override
	public String saveModel(ConfigModel model, SecuritySession env) throws BusinessError {
		try {
			//1-存主表
			boolean isAdd = StringUtils.isBlank(model.id)?true:false;
			if(isAdd){
				
			}else{
				String id = IdGenHelper.genID(model.app_code, model.code);
				model.id = id;
			}
			_commondao.batchReplace(Arrays.asList(model));
			//2-提取field信息
			if(isAdd){
				
			}else{//3-存field model(先清除)
				StringBuilder sql = new StringBuilder(" delete from pb_pf_config_field where config_id=${config_id} ");
				_commondao.executeChange(sql, JO.gen("config_id",model.id));
				List<ConfigFieldModel> list = parseField(model,model.id);
				_commondao.batchInsert(list);
			}
			//4-返回主键
			return model.id;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}

	@Override
	public ConfigModel findModel(String app_code, String cfg_unique, SecuritySession env) throws BusinessError {
		try {
			StringBuilder sql = new StringBuilder(" select * from pb_pf_config where app_code=${app_code} and (code=${unique} or id=${unique})");
			ConfigModel model = _commondao.find(sql, JO.gen("app_code",app_code,"unique",cfg_unique), ConfigModel.class,"没有找到唯一标识为的"+cfg_unique+"的，属于"+app_code+"应用的参数");
			//
			sql = new StringBuilder("select * from pb_pf_config_field where config_id=${config_id}");
			List<ConfigFieldModel> list = _commondao.query(sql,JO.gen("config_id",model.id),ConfigFieldModel.class);
			model.fields = list;
			return model;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	/**
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private List<ConfigFieldModel> parseField(ConfigModel model,String id)throws Exception{
		Class<? extends IConfigValueStore> clazz = (Class<? extends IConfigValueStore>)Class.forName(model.clazz);
		Field[] field_list = clazz.getFields();
		List<ConfigFieldModel> ret = new ArrayList<>();
		for(Field f:field_list){
			net.popbean.pf.entity.field.annotation.Field af = f.getAnnotation(net.popbean.pf.entity.field.annotation.Field.class);
			if(af!=null){
				ConfigFieldModel cfm = new ConfigFieldModel();
				cfm.code = StringUtils.isBlank(af.code())?f.getName():af.code();//这个可不能为空
				cfm.domain = (cfm.domain == null)?Domain.Code:af.domain();//如果为空就默认
				cfm.rangeset = af.rangeset();
				cfm.config_id = id;
				//
				ret.add(cfm);
			}
		}
		return ret;
	}
}
