package net.popbean.pf.persistence.vo;

import net.popbean.pf.entity.IValueObject;
import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
@Entity(code="pb_pf_batch")
public class BatchVO implements IValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1396210999336946036L;
	@Field(domain=Domain.pk)
	public String id;
}
