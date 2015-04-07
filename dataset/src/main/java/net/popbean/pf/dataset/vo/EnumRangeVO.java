package net.popbean.pf.dataset.vo;

import java.math.BigDecimal;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 枚举数据集值域
 * @author to0ld
 *
 */
@Entity(code = "pb_pf_ds_range")
public class EnumRangeVO extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8328762475814960817L;
	@Field
	public String code;
	@Field
	public String name;
	@Field
	public String code_value;
	@Field(domain = Domain.Money)
	public BigDecimal money_value;
	@Field(domain = Domain.Money)
	public BigDecimal money_value_max;
	@Field(domain = Domain.Money)
	public BigDecimal money_value_min;
	@Field(domain = Domain.Memo)
	public String memo;
	@Field(domain = Domain.Ref)
	public String ds_id;// ref->pk_ds:pk_ds_name
}
