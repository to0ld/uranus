package net.popbean.pf.rule.vo;

import java.util.List;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 规则的数据模型
 * 
 * @author to0ld
 *
 */
@Entity(code = "pb_pf_rule", name = "业务规则")
public class RuleModel extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7887680396883210204L;
	//
	public static final int RESULT_BOOL = 0;
	public static final int RESULT_JO = 3;
	public static final int RESULT_LIST = 5;
	//
	@Field(domain = Domain.Pk)
	public String rule_id;
	@Field
	public String rule_code;
	@Field
	public String rule_name;
	@Field(domain = Domain.Int, name = "返回类型")
	public Integer result_type;
	@Field(domain = Domain.Memo,name="备注")
	public String rule_memo;

	//
	public List<RuleDetail> details;
	public List<RuleParam> params;
}
