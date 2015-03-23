package net.popbean.pf.config.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;
/**
 * 配置的值域
 * 方案1，稀疏矩阵
 * 方案2，nosql存储
 * @author to0ld
 *
 */
@Entity(code="pb_pf_config_value")
public class ConfigValue extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7334996765043955328L;
	@Field(domain=Domain.Ref,relation=ConfigModel.class)
	public String config_id;//通过这种方式，来建立relation，从形式上讲是冗余的
	@Field
	public String config_name;
	//
	@Field(name="编码值0")
	public String code_0;
	@Field(name="编码值1")
	public String code_1;
	@Field(name="编码值2")
	public String code_2;
	@Field(name="编码值3")
	public String code_3;
	@Field(name="编码值4")
	public String code_4;
	//
	@Field(domain=Domain.TimeStamp,name="时间戳值0")
	public Timestamp ts_0;
	@Field(domain=Domain.TimeStamp,name="时间戳值0")
	public Timestamp ts_1;
	@Field(domain=Domain.TimeStamp,name="时间戳值0")
	public Timestamp ts_2;
	@Field(domain=Domain.TimeStamp,name="时间戳值0")
	public Timestamp ts_3;
	@Field(domain=Domain.TimeStamp,name="时间戳值0")
	public Timestamp ts_4;
	//
	@Field(domain=Domain.Ref,name="参照值0")
	public String ref_0;
	@Field(domain=Domain.Ref,name="参照值1")
	public String ref_1;
	@Field(domain=Domain.Ref,name="参照值2")
	public String ref_2;
	@Field(domain=Domain.Ref,name="参照值3")
	public String ref_3;
	@Field(domain=Domain.Ref,name="参照值4")
	public String ref_4;
	//虽然叫备注，但一般用来存规则，表达式啥的，建议别用
	@Field(domain=Domain.Ref,name="备注值0")
	public String memo_0;
	@Field(domain=Domain.Ref,name="备注值1")
	public String memo_1;
	//
	@Field(domain=Domain.Int,name="整型0")
	public int int_0;
	@Field(domain=Domain.Stat,name="整型1")
	public int int_1;
	@Field(domain=Domain.Stat,name="整型2")
	public int int_2;
	@Field(domain=Domain.Stat,name="整型3")
	public int int_3;
	@Field(domain=Domain.Stat,name="整型4")
	public int int_4;
	//
	@Field(domain=Domain.Stat,name="状态0")
	public int status_0;
	@Field(domain=Domain.Stat,name="状态1")
	public int status_1;
	@Field(domain=Domain.Stat,name="状态2")
	public int status_2;
	@Field(domain=Domain.Stat,name="状态3")
	public int status_3;
	@Field(domain=Domain.Stat,name="状态4")
	public int status_4;
	//
	@Field(domain=Domain.Money,name="小数值0")
	public BigDecimal money_0;
	@Field(domain=Domain.Money,name="小数值1")
	public BigDecimal money_1;
	@Field(domain=Domain.Money,name="小数值2")
	public BigDecimal money_2;
	@Field(domain=Domain.Money,name="小数值3")
	public BigDecimal money_3;
	@Field(domain=Domain.Money,name="小数值4")
	public BigDecimal money_4;
	//以下为n选1
	@Field(name="所属应用")
	public String app_code;
	@Field(name="所属组织")
	public String org_id;
	@Field(name="所属企业")
	public String company_id;
	@Field(name="所属用户")
	public String account_id;
}
