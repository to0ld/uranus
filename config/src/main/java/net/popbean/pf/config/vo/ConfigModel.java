package net.popbean.pf.config.vo;

import java.util.ArrayList;
import java.util.List;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

@Entity(code = "pb_pf_config")
public class ConfigModel extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4004868451478918380L;
	//
	public static final int LEV_GLOBAL = 11;//全局
	public static final int LEV_APP = 7;
	public static final int LEV_COMPANY = 5;
	public static final int LEV_ORG = 3;
	public static final int LEV_ACCOUNT = 0;
	//
	@Field(name="配置参数编码")
	public String code;
	@Field(name="配置参数名称")
	public String name;
	@Field(name="应用编码")
	public String app_code;
	@Field(domain=Domain.memo,name="备注")
	public String memo;
	@Field(domain=Domain.integer,name="级别")
	public int level;
	@Field(name="存储类")
	public String clazz;
	//env 的值
	public List<ConfigFieldModel> fields = new ArrayList<>();
}
