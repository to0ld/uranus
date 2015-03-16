package net.popbean.pf.bpmn.define.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;
/**
 * 流程的关键参数
 * 可以用于流程仿真及参数验证，流程定义(参数先定义后使用)
 * @author to0ld
 *
 */
@Entity(code = "pb_pf_procdef_param")
public class ProcessParam extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -470403335475503617L;
	@Field(domain = Domain.PK)
	public String param_id;
	@Field(domain = Domain.Ref, name = "流程定义引用")
	public String proc_def_ref;
	@Field
	public Domain type;
	@Field
	public String code;
	@Field
	public String name;
	@Field(domain = Domain.Stat)
	public int required;
	@Field
	public String range;
	@Field
	public String def_value;
	@Field
	public String memo;
}
