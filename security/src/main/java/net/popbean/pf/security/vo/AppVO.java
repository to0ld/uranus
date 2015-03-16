package net.popbean.pf.security.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 应用信息
 * 
 * @author to0ld
 *
 */
@Entity(code = "pb_bd_app", name = "应用信息")
public class AppVO extends AbstractValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2692355483300031372L;
	@Field(domain = Domain.PK)
	public String id;
	@Field(name = "编码")
	public String code;
	@Field(name = "名称")
	public String name;
	@Field(name = "备注")
	public String memo;
	@Field(domain = Domain.Ref, name = "所属企业")
	public String company_crt_ref;
	@Field(name = "授权码")
	public String secret_code;
}
