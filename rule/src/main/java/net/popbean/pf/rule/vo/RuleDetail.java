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
	@Field(domain = Domain.Ref)
	public String rule_ref;
	@Field(domain = Domain.Pk)
	public String rule_deail_id;
	@Field(domain = Domain.Ref)
	public String cond_ref;
	@Field(domain = Domain.Memo, name = "适用条件")
	public String cond_exp;
	@Field(domain = Domain.Memo, name = "执行规则")
	public String exec_exp;
	@Field(domain = Domain.Money, name = "序号")
	public Double inum;
	@Field(domain = Domain.Memo, name = "备注")
	public String detail_memo;
}
