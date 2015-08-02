package net.popbean.pf.security.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

@Entity(code = "pb_bd_role", name = "角色信息")
public class RoleVO extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3918195315421352204L;
	@Field
	public String code;
	@Field
	public String name;
	@Field(domain = Domain.memo)
	public String memo;
	@Field(name = "应用编码")
	public String app_code;

}