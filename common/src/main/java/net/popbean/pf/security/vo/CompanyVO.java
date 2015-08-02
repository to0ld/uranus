package net.popbean.pf.security.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 多租户的企业信息
 * 可能会根据注册
 * @author to0ld
 *
 */
@Entity(code = "pb_bd_company")
public class CompanyVO extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4168320084899460389L;
	// //
	// @Field(name="企业主键",domain=Domain.Pk)
	// public String id;
	@Field(name = "企业编码", domain = Domain.code)
	public String code;
	@Field(name = "企业名称", domain = Domain.code)
	public String name;
	@Field(name = "类型", domain = Domain.stat, rangeset = "0:租户@3:运维")
	public Integer type = 0;
	@Field(domain=Domain.memo,name="简介")
	public String summary;
	@Field(domain=Domain.memo,name="经营范围")
	public String scope;
}
