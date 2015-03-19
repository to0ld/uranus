package net.popbean.pf.bill.vo;

import java.util.ArrayList;
import java.util.List;

import net.popbean.pf.entity.impl.AbstractValueObject;

/**
 * <pre>
 * 事件模型 
 * -[] valueChange:本质上都是单值(某个属性值只要变化就触发；某一组合值触发) 
 * -[] bind(这个应该看成是valuechange的响应) 
 * -[] editableChange:当一组属性的编辑性发生变化时，触发 
 * -[] visibleChange
 * </pre>
 * @author to0ld
 *
 */
public class BillEventModel extends AbstractValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2889876336514064416L;
	//
	public static final String EVENT_TYPE_VALUE_CHANGE = "value_change";
	public static final String EVENT_TYPE_EDITABLE_CHANGE = "editable_change";
	public static final String EVENT_TYPE_VISIBLE_CHANGE = "visible_change";
	//
	public String type;//event type:valueChange;editableChange;visibleChange
	public List<String> editables = new ArrayList<>();// 可编辑列表
	public List<String> disables = new ArrayList<>();// 只读列表
	public List<String> visiables = new ArrayList<>();// 可见列表
	public List<String> hiddens = new ArrayList<>();// 隐藏列表
	public String condition;//触发条件:spel style?
	public String exec_exp;// 推荐为bind://restful or proc://method_name or stage://stage_name 不推荐直接上方法体
}
