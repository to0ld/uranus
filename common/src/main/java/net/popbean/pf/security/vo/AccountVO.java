package net.popbean.pf.security.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 常规账号，用手机号，邮箱做账号
 * 
 * @author to0ld
 *
 */
@Entity(code = "pb_bd_account")
public class AccountVO extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7301638432874869014L;
	//
	public static int STAT_DEL = -5;//删除
	public static int STAT_SEAL = -3;//封存
	public static int STAT_EDIT = 0;//注册
	public static int STAT_NONE = 3;//正常
	public static int STAT_BLOCK = 5;//黑名单
	
	//
	@Field(domain = Domain.PK)
	public String id;
	@Field
	public String code;
	@Field
	public String name;
	@Field
	public String pwd;// 加密后的
	@Field
	public String salt;//
	//
}
