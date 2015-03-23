package testcase.config.vo;

import net.popbean.pf.config.vo.impl.AbstractConfigValueStore;
import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Field;

public class MailConfig extends AbstractConfigValueStore {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3078869389855484906L;
	//
	@Field(domain=Domain.Stat,code="status_0")
	public boolean error_send_mail;//发生错误后是否发出邮件
}
