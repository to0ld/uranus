package testcase.bill.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 桥接表
 * 
 * @author to0ld
 *
 */
@Entity(code = "rlt_master_someone")
public class BridgeSlaveVO extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7285490996455405607L;
	@Field(domain = Domain.Ref)
	public String master_id;
	@Field(domain = Domain.Ref)
	public String someone_id;
}
