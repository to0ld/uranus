package net.popbean.pf.attachement.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

@Entity(code = "rlt_attachement")
public class RltAttVO extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5834487064778701120L;
	//
	@Field(domain=Domain.Ref)
	public String ref;
	@Field(domain = Domain.Ref)
	public String attachement_ref;
}
