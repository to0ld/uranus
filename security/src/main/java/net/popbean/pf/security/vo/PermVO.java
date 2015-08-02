package net.popbean.pf.security.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;
/**
 * 功能权限数据结构描述
 * 为了省事，直接用folder了
 * @author to0ld
 */
@Entity(code="pb_bd_perm",name="功能权限注册表")
public class PermVO extends AbstractValueObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7435167071250932466L;
	//
	public static final int TYPE_APP = 0;
	public static final int TYPE_FOLDER = 3;
	public static final int TYPE_NODE = 5;
	public static final int TYPE_ACT = 7;
	//
	@Field
	public String code;
	@Field
	public String name;
	@Field(domain=Domain.stat)
	public Integer type;//0:app;3:folder;5:node;7:action
	@Field
	public String badge;//BADGE一般对应的是一个bean
	@Field(domain=Domain.integer)
	public Integer serial;//序号
	@Field(domain=Domain.memo)
	public String uri;//资源指向
	@Field(domain=Domain.memo)
	public String param;//参数：key1=value1&key2=value2
	@Field(domain=Domain.memo)
	public String memo;
	@Field(domain=Domain.ref)
	public String app_code;//
	public Integer i_def;//默认节点
	@Field(name="图标")
	public String icon_uri;//图标
	@Field(domain=Domain.ref)
	public String folder_id;//所属目录主键
	//
	@Field(domain=Domain.stat)
	public Integer scope = 0;//默认授权；0为private 需要授权；3为public 不需要授权
}
