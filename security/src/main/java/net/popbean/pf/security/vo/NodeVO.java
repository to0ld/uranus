package net.popbean.pf.security.vo;

import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.impl.AbstractValueObject;
/**
 * 节点工厂注册的节点
 * @author to0ld
 *
 */
@Entity(code="pb_pf_node",name="节点工厂")
public class NodeVO extends AbstractValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1799800618183581104L;
	public String code;
	public String name;
	public String code_type;//节点类型
	public String bill_code;//单据编码
	public String app_code;//应用标示
	public String memo;//备注
	public String ctrl_content;//控制脚本
	public String ctrl_uri;//控制脚本链接
}
