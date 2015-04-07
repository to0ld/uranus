package net.popbean.pf.security.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

@Entity(code = "pb_bd_org", name = "组织信息")
public class OrgVO extends AbstractValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5462198923420969124L;
	@Field
	public String code;
	@Field
	public String name;
	@Field(domain = Domain.Memo)
	public String memo;
}
