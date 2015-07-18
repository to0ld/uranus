package net.popbean.pf.security.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 组织机构-账号，映射关系 仅仅适用于业务过程
 * 需要注意的是，一定是先注册用户，然后再登录创建企业
 * @author to0ld
 *
 */
@Entity(code="rlt_org_account")
public class RltOrgAccount extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1099916488760376741L;
	//
	@Field(domain = Domain.Ref)
	public String org_id;
	@Field(domain = Domain.Ref)
	public String account_id;
	@Field(domain = Domain.Stat, name = "最新状态")
	public int last;
	@Field(domain = Domain.Stat, name = "用户类型")
	public int type = 0;//0:普通用户；3：企业管理员
}
