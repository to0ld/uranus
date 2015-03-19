package testcase.bill.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;
/**
 * 单据测试的主表
 * @author to0ld
 *
 */
@Entity(code="test_bill_master")
public class MasterVO extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7710275374631764091L;
	//
	@Field
	public String code;
	@Field(domain=Domain.Memo)
	public String memo;
}
