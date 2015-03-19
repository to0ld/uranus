package net.popbean.pf.entity.model;

import net.popbean.pf.entity.field.annotation.Entity;
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
	public String code;//桥接表自身，也可以叫bridge_code;
	public String entity_code_main;
	public String id_key_main;
	public String entity_code_slave;
	public String id_key_slave;
	public String type;//关系类型
}
