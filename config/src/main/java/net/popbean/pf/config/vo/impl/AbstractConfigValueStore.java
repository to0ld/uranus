package net.popbean.pf.config.vo.impl;

import net.popbean.pf.config.vo.ConfigModel;
import net.popbean.pf.config.vo.IConfigValueStore;
import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.security.vo.SecuritySession;
@Entity(code = "pb_pf_config_value")
public abstract class AbstractConfigValueStore implements IConfigValueStore {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7943339256709298640L;

	//
	@Field(domain=Domain.Pk)
	public String id;
	@Field(domain=Domain.Ref,relation=ConfigModel.class)
	public String config_id;//通过这种方式，来建立relation，从形式上讲是冗余的
	@Field(name="所属应用")
	public String app_code;
	@Field(name="所属组织")
	public String org_id;
	@Field(name="所属企业")
	public String company_id;
	@Field(name="所属用户")
	public String account_id;
	//
	@Override
	public void merge(ConfigModel model,SecuritySession session) {
		config_id = model.id;
		//
		if(ConfigModel.LEV_APP == model.level){
			app_code = model.app_code;
		}else if(ConfigModel.LEV_COMPANY == model.level){
			company_id = session.company.id;
		}else if(ConfigModel.LEV_ORG == model.level){
			org_id = session.org.id;
		}else if(ConfigModel.LEV_ACCOUNT == model.level){
			account_id = session.account_id;
		}
		//
	}
}
