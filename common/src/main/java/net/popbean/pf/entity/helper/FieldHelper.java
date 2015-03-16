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
		ret.type = Domain.PK;
		ret.ispk = true;
		ret.length = Domain.PK.getLength();
		return ret;
	}
	public static FieldModel ref(String code, String name) {
		FieldModel ret = new FieldModel();
		ret.code = code;
		ret.name = name;
		ret.type = Domain.Ref;
		ret.length = Domain.Ref.getLength();
		return ret;
	}
	public static FieldModel stat(String code, String name) {
		FieldModel ret = new FieldModel();
		ret.code = code;
		ret.name = name;
		ret.type = Domain.Stat;
		return ret;
	}
	public static FieldModel integer(String code, String name) {
		FieldModel ret = new FieldModel();
		ret.code = code;
		ret.name = name;
		ret.type = Domain.Int;
		return ret;
	}
	public static FieldModel memo(String code, String name) {
		FieldModel ret = new FieldModel();
		ret.code = code;
		ret.name = name;
		ret.type = Domain.Memo;
		ret.length = Domain.Memo.getLength();
		return ret;
	}
}
