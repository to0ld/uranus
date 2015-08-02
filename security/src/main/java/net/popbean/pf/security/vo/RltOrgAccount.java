package net.popbean.pf.security.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.field.annotation.RelationType;
import net.popbean.pf.entity.impl.AbstractValueObject;
import net.popbean.pf.entity.model.EntityType;

/**
 * 组织机构-账号，映射关系 仅仅适用于业务过程
 * 需要注意的是，一定是先注册用户，然后再登录创建企业
 * @author to0ld
 *
 */
@Entity(code="rlt_org_account",type=EntityType.Bridge)
public class RltOrgAccount extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1099916488760376741L;
	//
	@Field(domain = Domain.ref,relation=OrgVO.class,rt=RelationType.Master)
	public String org_id;//可以化简一下n:n vs 1:n的判断逻辑，只要有slave就一定是n:n的关系
	
	@Field(domain = Domain.ref,relation=AccountVO.class,rt=RelationType.Slave)
	public String account_id;
	
	@Field(domain = Domain.stat, name = "最新状态")
	public int last;
	
	@Field(domain = Domain.stat, name = "用户类型")
	public int type = 0;//0:普通用户；3：企业管理员
}
