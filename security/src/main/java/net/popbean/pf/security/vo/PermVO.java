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
	@Field(domain=Domain.Pk)
	public String id;
	@Field
	public String code;
	@Field
	public String name;
	@Field(domain=Domain.Stat)
	public Integer type;//0:app;3:folder;5:node;7:action
	@Field
	public String badge_code;//BADGE键值
	@Field(domain=Domain.Int)
	public Integer serial;//序号
	@Field(domain=Domain.Memo)
	public String uri;//资源指向
	@Field(domain=Domain.Memo)
	public String memo;
	@Field(domain=Domain.Ref)
	public String app_ref;//
	public Integer i_def;//默认节点
	@Field(name="图标")
	public String icon_uri;//图标
	@Field(domain=Domain.Ref)
	public String folder_ref;//所属目录主键
	//
}
