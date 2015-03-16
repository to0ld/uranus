package net.popbean.pf.security.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;
/**
 * 组织机构
 * @author to0ld
 *
 */
@Entity(code="pb_bd_org",name="组织信息")
public class OrgVO extends AbstractValueObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9198231455239532834L;
	//
	public String pk_org;
	@Field(code="ref_org_cate",name="部门类型",domain=Domain.Ref)
	public String ref_org_cate;
	@Field(code="ref_company",name="所属企业",domain=Domain.Ref)
	public String ref_company;
	@Field(code="ref_account_owenr",name="组织负责人",domain=Domain.Ref)
	public String ref_account_owner;
	@Field(name="组织编码")
	public String code;
	@Field(name="组织名称")
	public String name;
	@Field(domain=Domain.Memo,name="备注")
	public String memo;
	@Field(domain=Domain.Int,name="节点深度")
	public Integer deep;//节点深度
	@Field(domain=Domain.Seriescode,name="级次码")
	public String seriescode;//[/a/b/c/结构，以便查询]
}
