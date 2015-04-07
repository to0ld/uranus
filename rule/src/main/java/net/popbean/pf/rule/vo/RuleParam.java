package net.popbean.pf.rule.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;
/**
 * 规则参数
 * @author to0ld
 *
 */
@Entity(code="pb_pf_rule_param")
public class RuleParam extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7093324741650749171L;
	//
	@Field(domain=Domain.Ref)
	public String rule_id;//FIXME left join 这事今后是不是就不好弄了？无法避免多表联查
	//select 1 from a left join b on (a.pk=b.fk)
	@Field
	public Domain type;
	@Field
	public String code;
	@Field
	public String name;
	@Field(domain=Domain.Stat)
	public int required;
}
