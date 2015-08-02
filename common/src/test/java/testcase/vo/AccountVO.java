package testcase.vo;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import net.popbean.pf.entity.IValueObject;
import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;

/**
 * 用于示范，验证新的实体描述体系 构建一个IDataStruct的基类
 * 
 * @author to0ld
 *
 */
@Entity(code = "pb_bd_account")
public class AccountVO implements IValueObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8757986141529528707L;
	//
	@Field(name="主键",domain=Domain.pk)
	public String pk_account;
	
	@Field(name="编码",domain=Domain.code)
	public String code_account;
	
	@Field(name="会员费",domain=Domain.money)
	public BigDecimal money_account;
	
	@Field(name = "生日",domain=Domain.date)
	public Date date_birth;
	
	@Field(name = "创建时间",domain=Domain.timestamp)
	public Timestamp ts_crt;
	
	@Field(name = "状态", domain=Domain.stat,rangeset = "0:编辑@3:处理中@5:已经完成")
	public Integer i_stat;
	
	@Field(name = "用户信息备注",domain=Domain.memo)
	public String memo_account;
	@Field(domain=Domain.stat)
	public Domain domain;
	@Field
	public String domainx;
	//
	public void setName(String value){
		this.code_account = value;
		return ;
	}
	public Domain getDomain(){
		return this.domain;
	}
	public void setDomain(Domain value){
		this.domain = value;
		return ;
	}
	public String getName(){
		return this.code_account;
	}
	public AccountVO(){
		super();
	}
}
