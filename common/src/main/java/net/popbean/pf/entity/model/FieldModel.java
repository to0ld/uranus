package net.popbean.pf.entity.model;

import java.io.Serializable;

import net.popbean.pf.entity.IValueObject;
import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.field.annotation.RelationType;
/**
 * 
 * @author to0ld
 *
 */
@Entity(code="pb_pf_field")
public class FieldModel implements Serializable, Cloneable,IValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6539336547071774284L;
	//
	public static final int REQ_YES = 3;
	public static final int REQ_NO = 0;
	//
	@Field(domain=Domain.Ref)
	public String entity_ref;
	@Field(domain=Domain.PK)
	public String field_id;
	public Domain type;
	public String code;
	public String name;
	public int required;
	public boolean ispk;
	public int length;
	public int precision;
	public String defaultValue;
	//
	public String clazz;//所用的类
	//
	public String code_relation_entity;//引用表编码
	public String pk_relation_entity;//引用表唯一标示
	public RelationType rt = RelationType.None;
	//
	/**
	 * 
	 * @return
	 */
	public boolean isRequired(){
		return (required == REQ_YES);
	}
}
