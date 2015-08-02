package net.popbean.pf.security.vo;

import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 常规账号，用手机号，邮箱做账号
 * 注册用户如果量级比较大，比如千万，为了确保速度(计算是缓存也是有成本的)可以做标注：日活；月活；季活；年活，针对不同level进行缓存
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
	public static int STAT_DEL = -7;//删除
	public static int STAT_BLOCK = -5;//黑名单
	public static int STAT_SEAL = -3;//封存
	public static int STAT_EDIT = 0;//注册
	public static int STAT_NONE = 5;//正常
	
	//
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
