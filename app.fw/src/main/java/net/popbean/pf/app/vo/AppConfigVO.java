package net.popbean.pf.app.vo;

import java.util.ArrayList;
import java.util.List;

import net.popbean.pf.entity.impl.AbstractValueObject;

public class AppConfigVO extends AbstractValueObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6915140453843276452L;
	public boolean skip =false;
	public String code;//app_code
	public String name;
	public String badge;
	public String org_code;
	public String mt;//用于多租户建账的bean
	public String secret;
	public String memo;
//	public List<SrvConfigVO> steps = new ArrayList<SrvConfigVO>();
	public List<String> steps = new ArrayList<>();//原始信息
}
