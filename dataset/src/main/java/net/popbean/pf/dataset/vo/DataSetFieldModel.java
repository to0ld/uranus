package net.popbean.pf.dataset.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;
@Entity(code="pb_pf_ds_field")
public class DataSetFieldModel extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2754985279324149578L;
	//
	@Field(domain=Domain.Ref)
	public String ds_id;
	@Field(domain=Domain.PK)
	public String ds_field_id;
	@Field(domain=Domain.Int)
	public Integer inum;//显示顺序
	@Field
	public String code_field;//编码,最终需要的编码
	@Field
	public String code_field_vendor;//数据提供方原始的编码
	@Field
	public String field_name;//名称
	@Field(domain=Domain.Memo)
	public String memo;
	public Domain domain;//怎么映射到数据库中啊，头疼
	@Field
	public Scope scope = Scope.Data;//
	public String rangeset;//值域
	public String def_value;//默认值
	public Integer ireq ;
	//这个应该不用存
	public boolean ispk;//是否是唯一标示
}
