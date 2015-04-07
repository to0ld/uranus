package net.popbean.pf.bpmn.engine.process.instance.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;
/**
 * 
 * @author to0ld
 *
 */
@Entity(code="pb_pf_procinst")
public class ProcessInstance extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6277673110914110969L;
	//
	public static final int STAT_EDIT = 0;//编辑|草稿状态
	public static final int STAT_PROCESSING = 3;//处理中
	public static final int STAT_FINISH = 5;//完成状态
	public static final int STAT_REJECT  = -3;//驳回
	//
	@Field
	public String proc_inst_id;
	@Field
	public String work_item_id;
	@Field(name="工作项编码值")
	public String code_value;//一般是单据号
	@Field(domain=Domain.Ref)
	public String dept_id;//所在组织
	@Field(domain=Domain.Seriescode,name="级次码")
	public String series;//[/a/b/c/]
	@Field(name="流程编码")
	public String proc_def_code;
	@Field(name="流程名称")
	public String proc_def_name;
	@Field(domain=Domain.Memo,name="标题")
	public String title ;////根据processdefine.title_tpl得到
	@Field(domain=Domain.Memo)
	public String content;//processdefine.content_tpl得到
	@Field(domain=Domain.Memo,name="提醒消息")
	public String tip;
	@Field(name="当前任务名称")
	public String current_task_name;//相当于当前的指针
	//
}
