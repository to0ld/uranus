package net.popbean.pf.security.vo;

import net.popbean.pf.entity.IValueObject;
import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
/**
 * 多租户的企业信息
 * @author to0ld
 *
 */
@Entity(code="pb_bd_company")
public class CompanyVO implements IValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4168320084899460389L;
	//
	@Field(name="企业主键",domain=Domain.Pk)
	public String id;
	@Field(name="企业编码",domain=Domain.Code)
	public String code;
	@Field(name="企业名称",domain=Domain.Code)
	public String name;
}
