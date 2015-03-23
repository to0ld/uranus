package testcase.config.vo;

import net.popbean.pf.config.vo.impl.AbstractConfigValueStore;
import net.popbean.pf.entity.field.annotation.Field;

/**
 * 示范multi-key的使用
 * 
 * @author to0ld
 *
 */
public class DataSourceConfig extends AbstractConfigValueStore {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8711828461588805412L;
	//
	@Field(name = "链接", code = "code_0")
	public String url;
	@Field(name = "数据库用户", code = "code_1")
	public String account_code;
	@Field(name = "数据库密码", code = "code_2")
	public String pwd;
	@Field(name = "数据库驱动类", code = "code_3")
	public String driver_clazz;
}
