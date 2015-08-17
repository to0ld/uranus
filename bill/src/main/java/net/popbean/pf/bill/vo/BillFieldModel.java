package net.popbean.pf.bill.vo;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * 单据明细
 * @author to0ld
 *
 */
public class BillFieldModel extends AbstractValueObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String code;//属性编码
	public String name;//算是label吧
	public Domain type = Domain.code;//默认是code
	public String tpl ;//显示模板,最好是前(js)后(java)都能解释，你看 ${code}这个就挺好
	public String tip = "提示信息";
	public String def_value;//默认值：支持时间戳；登录变量,如果不嫌弃就先采用spel反正前台也不需要？。。。这是谬论，点击两次新增，难道都从后台取数据？
	public boolean showcard = true;//卡片状态显示
	public boolean showlist = true;//列表状态显示
	public int cardpos = 0;//卡片位置
	public int listpos = 0;
	public Scope scope= Scope.data;//默认是数据
	public String rangeset;
	//
	public boolean required = true;
	public boolean readonly = false;//确实有必要增加这一项，确保其不受editable控制
	public boolean editable = true;
}
