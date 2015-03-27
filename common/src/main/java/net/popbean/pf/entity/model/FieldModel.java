package net.popbean.pf.entity.model;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.field.annotation.RelationType;
import net.popbean.pf.entity.impl.AbstractValueObject;
/**
 * 
 * @author to0ld
 *
 */
@Entity(code="pb_pf_field")
public class FieldModel extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6539336547071774284L;
	//
	public static final int REQ_YES = 3;
	public static final int REQ_NO = 0;
	//
	@Field(domain=Domain.Ref)
	public String entity_id;
	@Field
	public Domain type;
	@Field
	public String code;
	@Field
	public String name;
	@Field
	public String rangeset;
	@Field(domain=Domain.Stat)
	public int required;
//	@Field
//	public boolean ispk;
	@Field(domain=Domain.Int)
	public int length;
	@Field(domain=Domain.Int)
	public int fidelity;//没办法precison不能用，是关键字
	@Field
	public String def_value;
	//
	@Field
	public String clazz;//属性所用的类
	//来源表编码
	@Field
	public String relation_code;//引用表编码
	//来源表的引用字段a.pk到了b可能就是叫fk了
	@Field
	public String id_key_relation;//引用表唯一标示
	@Field
	public String source_class;//引用源头的类
	public RelationType rt = RelationType.None;
	//
	/**
	 * 
	 * @return
	 */
	public boolean isRequired(){
		return (required == REQ_YES);
	}
	public boolean isPk(){
		return Domain.Pk.equals(type);
	}
}
