package net.popbean.pf.bpmn.define.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

@Entity(code = "pb_pf_procdef")
public class ProcessDefine extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8311392492189642542L;
	//
	@Field(domain = Domain.PK)
	public String id;
	@Field
	public String code;
	@Field
	public String name;
	//单据的内容
	@Field
	public String id_key;
	@Field
	public String code_key;
	public String cate_key;//FIXME 应该换了
	@Field(name="扩展")
	public String ext;//扩展 
	@Field(domain=Domain.Code,name="标题模板")
	public String title_tpl;//列表状态显示的模板
	@Field(domain=Domain.Memo,name="标题属性")
	public String title_list;//FIXME 标题属性?
	@Field(domain=Domain.Memo,name="显示模板")
	public String msg_tpl;
	@Field(domain=Domain.Memo,name="显示属性")
	public String msg_list;
	@Field(domain=Domain.Memo,name="备注")
	public String memo;
	@Field(name="流程定义文件位置")
	public String path;
	@Field(name="排序")
	public int inum;
	@Field(name="图标路径")
	public String icon_uri;
	@Field(name="帮助连接")
	public String help_uri;
	//
}
