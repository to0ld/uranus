package net.popbean.pf.dataset.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.field.annotation.RelationType;
import net.popbean.pf.entity.impl.AbstractValueObject;
@Entity(code="pb_pf_ds_field")
public class DataSetFieldModel extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2754985279324149578L;
	//
	@Field(domain=Domain.ref,rt=RelationType.Master,relation=DataSetModel.class)
	public String ds_id;
	@Field(domain=Domain.integer)
	public Integer inum;//显示顺序
	@Field
	public String code;//编码,最终需要的编码
	@Field
	public String code_vendor;//数据提供方原始的编码
	@Field
	public String name;//名称
	@Field(domain=Domain.memo)
	public String memo;
	@Field
	public Domain type;//怎么映射到数据库中啊，头疼
	@Field
	public Scope scope = Scope.data;//
	@Field
	public String rangeset;//值域
	@Field
	public String def_value;//默认值
	@Field(domain=Domain.stat)
	public Integer required ;
	//这个应该不用存
	public boolean ispk;//是否是唯一标示
}
