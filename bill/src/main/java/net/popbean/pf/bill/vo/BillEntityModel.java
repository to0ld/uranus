package net.popbean.pf.bill.vo;

import java.util.List;

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
	public String code;
	public String name;
	public List<BillFieldModel> fields;
	public int column = 1;//默认一行一个控件，咱就别layout了
}
