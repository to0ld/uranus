package net.popbean.pf.entity.helper;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.model.FieldModel;

public class FieldHelper {
	/**
	 * 
	 * @param code
	 * @param name
	 * @return
	 */
	public static FieldModel pk(String code, String name) {
		FieldModel ret = new FieldModel();
		ret.code = code;
		ret.name = name;
		ret.type = Domain.pk;
		ret.length = Domain.pk.getLength();
		return ret;
	}
	public static FieldModel ref(String code, String name) {
		FieldModel ret = new FieldModel();
		ret.code = code;
		ret.name = name;
		ret.type = Domain.ref;
		ret.length = Domain.ref.getLength();
		return ret;
	}
	public static FieldModel stat(String code, String name) {
		FieldModel ret = new FieldModel();
		ret.code = code;
		ret.name = name;
		ret.type = Domain.stat;
		return ret;
	}
	public static FieldModel integer(String code, String name) {
		FieldModel ret = new FieldModel();
		ret.code = code;
		ret.name = name;
		ret.type = Domain.integer;
		return ret;
	}
	public static FieldModel memo(String code, String name) {
		FieldModel ret = new FieldModel();
		ret.code = code;
		ret.name = name;
		ret.type = Domain.memo;
		ret.length = Domain.memo.getLength();
		return ret;
	}
}
