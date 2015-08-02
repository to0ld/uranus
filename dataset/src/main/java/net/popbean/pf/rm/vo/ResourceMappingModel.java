package net.popbean.pf.rm.vo;

import net.popbean.pf.dataset.vo.DataSetModel;
import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

@Entity(code = "pb_pf_rm")
public class ResourceMappingModel extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3607238787796118266L;
	//
	public static final int TYPE_INCLUDE = 3;// 包含类型
	public static final int TYPE_EXCLUDE = 0;// 排除类型
	//
	@Field
	public String code;
	@Field
	public String name;
	@Field
	public String relation_code;// 映射存储所在表，应该予以自动生成为宜
	@Field(domain = Domain.memo)
	public String memo;// 备注
	@Field
	public String app_code;// 所属应用
	@Field(domain = Domain.stat)
	public Integer type = TYPE_INCLUDE;// 0:排除;3:包含
	@Field(domain = Domain.ref, name = "主体数据集")
	public String subject_id;// ref->pk_ds:name
	@Field(domain = Domain.ref, name = "资源数据集")
	public String resource_id;//
	//
	public DataSetModel subject_ds_model;
	public DataSetModel resource_ds_model;
}
