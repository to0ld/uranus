package net.popbean.pf.bill.vo;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import net.popbean.pf.entity.impl.AbstractValueObject;
/**
 * 
 * @author to0ld
 *
 */
public class BillModel extends AbstractValueObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 704990313420163784L;
	//
	public static final String LAYOUT_TAB = "tab";//子表之间是用tabed摆放
	public static final String LAYOUT_LIST = "list"; //主子表之间是线性排开
	//
	public String code;//编码
	public String stage;//根据stage来确定分类，不再有cate_ref
	public String ext;//单据的扩展实现
	//FIXME 有无status一说呢？
	public String layout = LAYOUT_LIST;//布局
	public BillEntityModel main;//主表
	public List<BillEntityModel> slaves = new ArrayList<>();//子集
	public List<BillEventModel> events = new ArrayList<>();//事件
	public JSONObject data;
}
