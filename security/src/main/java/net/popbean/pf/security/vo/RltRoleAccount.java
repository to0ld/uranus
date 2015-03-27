package net.popbean.pf.security.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 角色-账号关系
 * 
 * @author to0ld
 *
 */
@Entity(code = "rlt_role_account")
public class RltRoleAccount extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9133292986850360590L;
	@Field(domain = Domain.Pk)
	public String id;
	@Field(domain = Domain.Ref)
	public String role_ref;
	@Field(domain = Domain.Ref)
	public String account_ref;
}
