package net.popbean.pf.rm.helper;

import java.util.ArrayList;
import java.util.List;

import net.popbean.pf.entity.helper.FieldHelper;
import net.popbean.pf.entity.model.EntityModel;
import net.popbean.pf.entity.model.FieldModel;


public class ResourceMappingHelper {
	/**
	 * 用于生成授权表
	 * 
	 * @param rlt_md_code
	 * @return
	 */
	public static EntityModel buildRelationEntityModel(String rlt_md_code) {
		EntityModel tm = new EntityModel();
		tm.code = rlt_md_code;
		tm.name = "资源授权表(" + rlt_md_code + ")";
		//
		List<FieldModel> field_list = new ArrayList<>();
		FieldModel id = FieldHelper.pk("id", "主键");
		FieldModel subject_id = FieldHelper.ref("subject_id","主体参照");
		FieldModel resource_id = FieldHelper.ref("resource_id","资源参照");
		FieldModel status = FieldHelper.stat("status","状态");
		FieldModel serial = FieldHelper.integer("serial","序号");
		FieldModel memo = FieldHelper.memo("memo","备注");
		field_list.add(id);
		field_list.add(subject_id);
		field_list.add(resource_id);
		field_list.add(status);
		field_list.add(serial);
		field_list.add(memo);
		//
		tm.field_list = field_list;
		return tm;
	}
}
