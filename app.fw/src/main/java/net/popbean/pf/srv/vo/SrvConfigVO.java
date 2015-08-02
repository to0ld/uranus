package net.popbean.pf.srv.vo;

import net.popbean.pf.entity.impl.AbstractValueObject;

public class SrvConfigVO extends AbstractValueObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4787342798867164891L;
	public String bean;
	public boolean skip = false;
	public String memo;//备注
}
