package net.popbean.pf.security.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 组织机构-账号，映射关系 仅仅适用于业务过程
 * 
 * @author to0ld
 *
 */
@Entity(code="rlt_org_account")
public class RltOrgAccount extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1099916488760376741L;
	//
	@Field(domain = Domain.Ref)
	public String org_id;
	@Field(domain = Domain.Ref)
	public String account_id;
	@Field(domain = Domain.Stat, name = "最新状态")
	public int last;
}
