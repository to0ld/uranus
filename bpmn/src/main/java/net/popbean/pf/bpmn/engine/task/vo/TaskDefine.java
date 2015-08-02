package net.popbean.pf.bpmn.engine.task.vo;

import java.util.List;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;
@Entity(code="pb_pf_taskdef")
public class TaskDefine extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 180974509656528478L;
	@Field(domain=Domain.pk)
	public String id;
	@Field(domain=Domain.ref)
	public String task_id;//proc_def_code:task_code可好
	public String proc_def_code;//流程定义的编码等同于主键
	@Field(name="单据编号")
	public String bill_code;//最好定义一下stage,
//	@Field(domain=Domain.Memo,name="可选任务")
//	public String task_action;
	@Field(domain=Domain.memo,name="创建者模板")
	public String crt_tpl;
	@Field(domain=Domain.memo,name="审批者模板")
	public String aprv_tpl;
	@Field(domain=Domain.memo,name="提醒模板模板")
	public String tip_tpl;
	@Field(name="扩展类")
	public String ext;
	@Field(domain=Domain.memo,name="备注")
	public String memo;
	public List<TaskAction> actions;
	//FIXME 是否要针对每个任务提供参与者表达式：candidator_std_service.fetch(task_id)
}
