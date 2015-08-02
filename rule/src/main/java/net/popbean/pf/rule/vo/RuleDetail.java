package net.popbean.pf.rule.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 规则细则
 * 
 * @author to0ld
 *
 */
@Entity(code = "pb_pf_rule_detail")
public class RuleDetail extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3461489074210806393L;
	//
	@Field(domain = Domain.ref)
	public String rule_id;
	@Field(domain = Domain.ref)
	public String cond_id;
	@Field(domain = Domain.memo, name = "适用条件")
	public String cond_exp;
	@Field(domain = Domain.memo, name = "执行规则")
	public String exec_exp;
	@Field(domain = Domain.money, name = "序号")
	public Double inum;
	@Field(domain = Domain.memo, name = "备注")
	public String memo;
}
