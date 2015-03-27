package net.popbean.pf.entity.impl;

import java.sql.Timestamp;

import net.popbean.pf.entity.IValueObject;
import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Field;
/**
 * 应该将实体进行分类
 * - 基础信息(extends AbstractValueObject):无status
 * - 单据信息(extends AbstractBillValueObject):提供统一的code
 * - 结论似乎非常明显。。。根本不用
 * @author to0ld
 *
 */

public class AbstractValueObject implements IValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3852105815084274103L;
	//
	public static int STAT_ALL = 999;//全部
	//
	@Field(domain = Domain.Pk, name = "主键")
	public String id;
	@Field(name = "创建者", domain = Domain.Ref)
	public String account_crt_ref;
	@Field(name = "所属企业", domain = Domain.Ref)
	public String company_crt_ref;
	@Field(name = "状态", domain = Domain.Stat, rangeset = "0:编辑@3:处理中@5:已完成")
	public Integer status;
	@Field(domain = Domain.TimeStamp, name = "创建时间")
	public Timestamp crt_ts;
	@Field(domain = Domain.TimeStamp, name = "最后修改时间")
	public Timestamp lm_ts;//FIXME 如果叫version会不会更好?
}
