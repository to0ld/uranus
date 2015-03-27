package net.popbean.pf.bill.vo;

import java.util.List;

import net.popbean.pf.bill.helpers.BillModelHelper;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 单据实体模型
 * @author to0ld
 *
 */
public class BillEntityModel extends AbstractValueObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1166654519700500124L;
	//
	public static final String REF_TYPE_NONE = "none";
	public static final String REF_TYPE_WEAK = "weak";
	//
	public String code;
	public String name;
	public String ref_type = REF_TYPE_NONE;
	public List<BillFieldModel> fields;
	public int column = 1;//默认一行一个控件，咱就别layout了
	public BillFieldModel findPk(){
		return BillModelHelper.findPK(this.fields);
	}
}
