package testcase.bill.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.field.annotation.RelationType;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 直连主子表
 * 
 * @author to0ld
 *
 */
@Entity(code = "test_bill_ds")
public class DirectSlaveVO extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2596259045738796138L;
	//
	@Field
	public String code;
	@Field(domain = Domain.Ref, rt = RelationType.Slave)
	public String master_id;
	@Field(domain = Domain.Memo)
	public String memo;
}
