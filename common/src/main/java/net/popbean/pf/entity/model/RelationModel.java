package net.popbean.pf.entity.model;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;
/**
 * 
 * @author to0ld
 *
 */
@Entity(code="pb_pf_relation")
public class RelationModel extends AbstractValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 735854453658840266L;
	//
	//
	public static final String TYPE_INNER = "inner";
	public static final String TYPE_OUTER = "outer";
	public static final String TYPE_BRIDGE = "bridge";
	//
	@Field
	public String code;//桥接表自身，也可以叫bridge_code;
	@Field
	public String entity_code_main;
	@Field(domain=Domain.ref)
	public String id_key_main;
	@Field
	public String entity_code_slave;
	@Field(domain=Domain.ref)
	public String id_key_slave;
	@Field
	public String type;//关系类型:inner:outer；bridge
}
