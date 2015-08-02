package net.popbean.pf.security.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 角色-权限
 * 
 * @author to0ld
 *
 */
@Entity(code="rlt_role_perm")
public class RltRolePerm extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1573215611190866663L;
	//
	public static final int STAT_NONE = 3;// 正常
	public static final int STAT_SEAL = 0;// 封存
	//
	@Field(domain = Domain.ref, name = "角色")
	public String role_id;
	@Field(domain = Domain.ref, name = "权限")
	public String perm_id;
	@Field(domain = Domain.integer, name = "序号")
	public int serial;
}
