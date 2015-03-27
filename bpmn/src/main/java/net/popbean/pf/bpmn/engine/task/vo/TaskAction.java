package net.popbean.pf.bpmn.engine.task.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 指定的任务描述有和可以进行的操作
 * 不太确定，需要用前端的代码进行验证
 * @author to0ld
 *
 */
@Entity(code = "pb_pf_taskact", name = "任务操作")
public class TaskAction extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1276514296777734693L;
	//
	@Field(domain = Domain.Pk)
	public String id;
	@Field(name = "编码")
	public String code;
	@Field(name = "名称")
	public String name;//
	@Field(name = "启用规则")
	public String condition_exp;// 根据经验似乎不是所有的action在任何情况下都能用的,看起来是个expression比较合适，应该不需要上rule service
								// 
	@Field(name = "备注", domain = Domain.Memo)
	public String memo;
	@Field(name="响应链接")
	public String uri;//点击响应的uri,参数为当前的workitem，task，securitysession
}
