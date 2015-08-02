package net.popbean.pf.config.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

@Entity(code = "pb_pf_config_field")
public class ConfigFieldModel extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 129090965466982441L;
	//
	@Field(domain = Domain.ref)
	public String config_id;
	//
	@Field(name = "数据类型")
	public Domain domain = Domain.ref;// 数据类型
	@Field
	public String code;// 对外使用的key
	@Field
	public String name;// 名称
	@Field
	public String value;// 用string来转型吧，省事
	@Field(name = "值域")
	public String rangeset;
	@Field
	public String def_value;// 默认值
	public String store_code;// 存储所用的key
	@Field(domain = Domain.memo, name = "备注")
	public String memo;
	//

}
