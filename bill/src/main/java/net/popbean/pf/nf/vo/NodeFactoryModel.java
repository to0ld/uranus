package net.popbean.pf.nf.vo;

import net.popbean.pf.bill.vo.BillModel;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

public class NodeFactoryModel extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1905754075156472614L;
	//
	@Field(name = "编码")
	public String code;
	@Field(name = "名称")
	public String name;
	@Field(name = "单据模板")
	public String bill_code;
	@Field(name = "类型")
	public String type;//gp;db;cal
	@Field(name = "所属应用")
	public String app_code;
	@Field(name = "备注")
	public String memo;
	//
	public BillModel model ;
}
