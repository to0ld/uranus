package net.popbean.pf.security.vo;

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
	//
	@Field(name = "编码")
	public String code;
	@Field(name = "名称")
	public String name;
	@Field(name = "备注")
	public String memo;
	@Field(name = "授权码")
	public String secret;
}
