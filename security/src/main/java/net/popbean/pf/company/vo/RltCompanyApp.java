package net.popbean.pf.company.vo;

import java.sql.Timestamp;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 组织机构-应用，映射关系 仅仅适用于业务过程
 * 
 * @author to0ld
 *
 */
@Entity(code = "rlt_company_app")
public class RltCompanyApp extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1099916488760376741L;
	//
	@Field(domain = Domain.ref)
	public String company_id;
	@Field(domain = Domain.ref)
	public String app_id;
	@Field(domain = Domain.timestamp, name = "开始时间")
	public Timestamp start_ts;
	@Field(domain = Domain.timestamp, name = "结束时间")
	public Timestamp end_ts;
}
